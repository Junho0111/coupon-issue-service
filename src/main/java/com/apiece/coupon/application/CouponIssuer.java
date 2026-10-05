package com.apiece.coupon.application;

import com.apiece.coupon.support.AlreadyIssuedException;
import com.apiece.coupon.support.SoldOutException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponIssuer {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> script = RedisScript.of(
            new ClassPathResource("lua/issue.lua"),
            Long.class
    );

    public CouponIssuer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void tryIssue(long couponId, long userId) {
        Long raw = redisTemplate.execute(
                script,
                List.of(stockKey(couponId), usersKey(couponId)),
                String.valueOf(userId)
        );

        if (raw == null) {
            throw new IllegalStateException("Lua 스크립트 결과가 null");
        }

        if (raw == 1L) {
            return;
        }

        if (raw == 0L) {
            throw new SoldOutException();
        }

        if (raw == -1L) {
            throw new AlreadyIssuedException(); // 유저가 이미 발급을 해서 더 이상 발급할 수 없다는 예외
        }
        throw new IllegalStateException("예상치 못한 Lua 결과: " + raw);
    }

    private String usersKey(long couponId) {
        return "coupon:" + couponId + ":users";
    }

    public void initStock(long couponId, int totalQuantity) {
        redisTemplate.opsForValue().set(stockKey(couponId), String.valueOf(totalQuantity));
    }

    private String stockKey(long couponId) {
        return "coupon:" + couponId + ":stock";
    }
}
