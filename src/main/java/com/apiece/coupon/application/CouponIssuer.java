package com.apiece.coupon.application;

import com.apiece.coupon.infrastructure.cache.IssuanceRedisRepository;
import com.apiece.coupon.support.AlreadyIssuedException;
import com.apiece.coupon.support.SoldOutException;
import org.springframework.stereotype.Component;

@Component
public class CouponIssuer {

    private final IssuanceRedisRepository issuanceRedisRepository;

    public CouponIssuer(IssuanceRedisRepository issuanceRedisRepository) {
        this.issuanceRedisRepository = issuanceRedisRepository;
    }

    public void tryIssue(long couponId, long userId) {
        long result = issuanceRedisRepository.tryIssue(couponId, userId);
        if (result == 1L) {
            return;
        }

        if (result == 0L) {
            throw new SoldOutException();
        }

        if (result == -1L) {
            throw new AlreadyIssuedException();
        }

        throw new IllegalStateException("예상치 못한 Lua 결과");
    }

    public void initStock(long couponId, int totalQuantity) {
        issuanceRedisRepository.initStock(couponId, totalQuantity);
    }
}
