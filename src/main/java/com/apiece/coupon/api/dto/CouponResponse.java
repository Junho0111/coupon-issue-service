package com.apiece.coupon.api.dto;

import com.apiece.coupon.domain.Coupon;
import java.time.LocalDateTime;
import java.util.Objects;

public class CouponResponse {

    private final long id;
    private final String name;
    private final int totalQuantity;
    private final int issuedQuantity;
    private final int validityDays;
    private final LocalDateTime startsAt;
    private final LocalDateTime createdAt;

    public CouponResponse(
            long id,
            String name,
            int totalQuantity,
            int issuedQuantity,
            int validityDays,
            LocalDateTime startsAt,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = issuedQuantity;
        this.validityDays = validityDays;
        this.startsAt = startsAt;
        this.createdAt = createdAt;
    }

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                Objects.requireNonNull(coupon.getId()),
                coupon.getName(),
                coupon.getTotalQuantity(),
                coupon.getIssuedQuantity(),
                coupon.getValidityDays(),
                coupon.getStartsAt(),
                coupon.getCreatedAt()
        );
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getIssuedQuantity() {
        return issuedQuantity;
    }

    public int getValidityDays() {
        return validityDays;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
