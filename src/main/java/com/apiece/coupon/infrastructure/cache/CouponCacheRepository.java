package com.apiece.coupon.infrastructure.cache;

import static com.apiece.coupon.infrastructure.cache.RedisLuaSupport.listLuaScript;
import static com.apiece.coupon.infrastructure.cache.RedisLuaSupport.longLuaScript;
import static com.apiece.coupon.infrastructure.cache.RedisLuaSupport.runForLong;
import static com.apiece.coupon.infrastructure.cache.RedisLuaSupport.runForStrings;

import com.apiece.coupon.application.CacheMetrics;
import com.apiece.coupon.application.CouponIssuePolicy;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

@Repository
public class CouponCacheRepository {

    private static final int MAX_RETRIES = 50;
    private static final long WAIT_BACKOFF_MS = 20L;
    private static final long LOCK_TTL_MS = 3_000L;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;
    private final CacheMetrics cacheMetrics;

    @SuppressWarnings("rawtypes")
    private final RedisScript<List> singleFlightScript = listLuaScript("lua/cache-single-flight.lua");
    private final RedisScript<Long> releaseLockScript = longLuaScript("lua/release-lock.lua");

    public CouponCacheRepository(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            CacheProperties cacheProperties,
            CacheMetrics cacheMetrics
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.cacheProperties = cacheProperties;
        this.cacheMetrics = cacheMetrics;
    }

    public CouponIssuePolicy getIssuePolicyOrLoad(long id, Supplier<CouponIssuePolicy> loader) {
        return getOrLoad(
                "coupon:" + id + ":issue-policy",
                "coupon:" + id + ":issue-policy:lock",
                loader
        );
    }

    private CouponIssuePolicy getOrLoad(
            String cacheKey,
            String lockKey,
            Supplier<CouponIssuePolicy> loader
    ) {
        String token = UUID.randomUUID().toString();
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            List<String> result = runForStrings(
                    redisTemplate,
                    singleFlightScript,
                    List.of(cacheKey, lockKey),
                    token, LOCK_TTL_MS
            );

            switch (result.get(0)) {
                case "HIT" -> {
                    cacheMetrics.incrementCouponCacheHit();
                    return objectMapper.readValue(result.get(1), CouponIssuePolicy.class);
                }
                case "LOAD" -> {
                    try {
                        cacheMetrics.incrementCouponDbRead();
                        CouponIssuePolicy response = loader.get();
                        String json = objectMapper.writeValueAsString(response);
                        redisTemplate.opsForValue()
                                .set(cacheKey, json, Duration.ofMillis(cacheProperties.getTtlMs()));
                        return response;
                    } finally {
                        runForLong(redisTemplate, releaseLockScript, List.of(lockKey), token);
                    }
                }
                case "WAIT" -> waitBackoff();
                default -> {
                }
            }
        }
        throw new IllegalStateException("쿠폰 캐시 채우기 timeout (key=" + cacheKey + ")");
    }

    private void waitBackoff() {
        try {
            Thread.sleep(WAIT_BACKOFF_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
