package com.apiece.coupon.infrastructure.cache;

import static com.apiece.coupon.infrastructure.cache.RedisLuaSupport.listLuaScript;
import static com.apiece.coupon.infrastructure.cache.RedisLuaSupport.longLuaScript;
import static com.apiece.coupon.infrastructure.cache.RedisLuaSupport.runForLong;
import static com.apiece.coupon.infrastructure.cache.RedisLuaSupport.runForStrings;

import com.apiece.coupon.application.CacheMetrics;
import com.apiece.coupon.application.CouponIssuePolicy;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

@Repository
public class CouponCacheRepository {

    private static final Logger log = LoggerFactory.getLogger(CouponCacheRepository.class);

    private static final int MAX_RETRIES = 50;
    private static final long WAIT_BACKOFF_MS = 20L;
    private static final long LOCK_TTL_MS = 3_000L;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;
    private final CacheMetrics cacheMetrics;

    private final ExecutorService backgroundExecutor = Executors.newFixedThreadPool(4, runnable -> {
        Thread thread = new Thread(runnable, "coupon-cache-swr-refresh");
        thread.setDaemon(true);
        return thread;
    });

    @SuppressWarnings("rawtypes")
    private final RedisScript<List> lookupScript = listLuaScript("lua/cache-single-flight-swr.lua");
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
            long now = Instant.now().toEpochMilli();
            List<String> result = runForStrings(
                    redisTemplate,
                    lookupScript,
                    List.of(cacheKey, lockKey),
                    now, cacheProperties.getFreshMs(), token, LOCK_TTL_MS
            );

            switch (result.get(0)) {
                case "HIT" -> {
                    cacheMetrics.incrementCouponCacheHit();
                    return objectMapper.readValue(result.get(1), CouponIssuePolicy.class);
                }
                case "STALE_REFRESH" -> {
                    cacheMetrics.incrementCouponCacheHit();
                    backgroundExecutor.execute(() -> {
                        try {
                            fillCache(cacheKey, lockKey, token, loader);
                        } catch (Exception e) {
                            log.warn("백그라운드 SWR 갱신 실패 (key={}): {}", cacheKey, e.getMessage());
                        }
                    });
                    return objectMapper.readValue(result.get(1), CouponIssuePolicy.class);
                }
                case "LOAD" -> {
                    return fillCache(cacheKey, lockKey, token, loader);
                }
                case "WAIT" -> waitBackoff();
                default -> {
                }
            }
        }
        throw new IllegalStateException("쿠폰 캐시 채우기 timeout (key=" + cacheKey + ")");
    }

    private CouponIssuePolicy fillCache(
            String cacheKey,
            String lockKey,
            String token,
            Supplier<CouponIssuePolicy> loader
    ) {
        try {
            cacheMetrics.incrementCouponDbRead();
            CouponIssuePolicy response = loader.get();
            redisTemplate.<String, String>opsForHash().putAll(
                    cacheKey,
                    Map.of(
                            "value", objectMapper.writeValueAsString(response),
                            "fetchedAtMs", String.valueOf(Instant.now().toEpochMilli())
                    )
            );
            redisTemplate.expire(cacheKey, Duration.ofMillis(cacheProperties.getTtlMs()));
            return response;
        } finally {
            runForLong(redisTemplate, releaseLockScript, List.of(lockKey), token);
        }
    }

    @PreDestroy
    public void shutdown() {
        backgroundExecutor.shutdown();
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
