package com.apiece.coupon.infrastructure.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.apiece.coupon.infrastructure.cache.RedisLuaSupport.longLuaScript;
import static com.apiece.coupon.infrastructure.cache.RedisLuaSupport.runForLong;

@Repository
public class IssuanceRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private final SoldOutProperties soldOutProperties;

    private final RedisScript<Long> issueScript = longLuaScript("lua/issue.lua");

    public IssuanceRedisRepository(
            StringRedisTemplate redisTemplate,
            SoldOutProperties soldOutProperties
    ) {
        this.redisTemplate = redisTemplate;
        this.soldOutProperties = soldOutProperties;
    }

    // 반환: 1=성공, 0=매진, -1=중복 발급
    public long tryIssue(long couponId, long userId) {
        return runForLong(
                redisTemplate,
                issueScript,
                List.of(
                        "coupon:" + couponId + ":stock",
                        "coupon:" + couponId + ":users",
                        "coupon:" + couponId + ":sold_out"
                ),
                userId, soldOutProperties.getTtlSeconds()
        );
    }

    public void initStock(long couponId, int totalQuantity) {
        redisTemplate.opsForValue().set("coupon:" + couponId + ":stock", String.valueOf(totalQuantity));
    }
}
