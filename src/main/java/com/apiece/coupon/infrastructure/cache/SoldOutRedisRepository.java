package com.apiece.coupon.infrastructure.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SoldOutRedisRepository {

    private final StringRedisTemplate redisTemplate;

    public SoldOutRedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isFlagged(long couponId) {
        return redisTemplate.hasKey("coupon:" + couponId + ":sold_out");
    }
}
