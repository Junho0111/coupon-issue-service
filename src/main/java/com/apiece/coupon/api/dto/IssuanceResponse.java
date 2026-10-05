package com.apiece.coupon.api.dto;

import com.apiece.coupon.domain.Issuance;
import com.apiece.coupon.domain.IssuanceStatus;
import java.time.LocalDateTime;

public class IssuanceResponse {

    private final Long id;
    private final long userId;
    private final long couponId;
    private final IssuanceStatus status;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private final LocalDateTime usedAt;

    public IssuanceResponse(
            Long id,
            long userId,
            long couponId,
            IssuanceStatus status,
            LocalDateTime issuedAt,
            LocalDateTime expiresAt,
            LocalDateTime usedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
    }

    public static IssuanceResponse from(Issuance issuance) {
        return new IssuanceResponse(
                issuance.getId(),
                issuance.getUserId(),
                issuance.getCouponId(),
                issuance.getStatus(),
                issuance.getIssuedAt(),
                issuance.getExpiresAt(),
                issuance.getUsedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getCouponId() {
        return couponId;
    }

    public IssuanceStatus getStatus() {
        return status;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }
}
