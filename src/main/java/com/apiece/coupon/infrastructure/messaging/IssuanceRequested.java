package com.apiece.coupon.infrastructure.messaging;

import java.time.LocalDateTime;

public class IssuanceRequested {

    private final long couponId;
    private final long userId;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;


    public IssuanceRequested(long couponId, long userId, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        this.couponId = couponId;
        this.userId = userId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public long getCouponId() {
        return couponId;
    }

    public long getUserId() {
        return userId;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
