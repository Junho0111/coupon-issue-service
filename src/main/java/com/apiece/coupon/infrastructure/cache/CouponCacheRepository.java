package com.apiece.coupon.infrastructure.cache;

import com.apiece.coupon.application.CacheMetrics;
import com.apiece.coupon.application.CouponIssuePolicy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.function.Supplier;

@Repository
public class CouponCacheRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;
    private final CacheMetrics cacheMetrics;

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
        return getOrLoad("coupon:" + id + ":issue-policy", loader);
    }

    private CouponIssuePolicy getOrLoad(String key, Supplier<CouponIssuePolicy> loader) {
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            cacheMetrics.incrementCouponCacheHit();
            return objectMapper.readValue(cached, CouponIssuePolicy.class);
        }
        cacheMetrics.incrementCouponDbRead();
        CouponIssuePolicy response = loader.get();
        redisTemplate.opsForValue().set(
                key,
                objectMapper.writeValueAsString(response),
                Duration.ofMillis(cacheProperties.getTtlMs())
        );
        return response;
    }
}
