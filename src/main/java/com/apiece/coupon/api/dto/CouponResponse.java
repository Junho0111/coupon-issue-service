package com.apiece.coupon.api.dto;

import com.apiece.coupon.domain.Coupon;
import java.time.LocalDateTime;
import java.util.Objects;

// 캐시할 정적 필드만. 실시간 재고는 Redis stock, 발급량은 DB 에서 직접.
public class CouponResponse {

    private final long id;
    private final String name;
    private final int totalQuantity;
    private final int validityDays;
    private final LocalDateTime startsAt;

    public CouponResponse(
            long id,
            String name,
            int totalQuantity,
            int validityDays,
            LocalDateTime startsAt
    ) {
        this.id = id;
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.validityDays = validityDays;
        this.startsAt = startsAt;
    }

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                Objects.requireNonNull(coupon.getId()),
                coupon.getName(),
                coupon.getTotalQuantity(),
                coupon.getValidityDays(),
                coupon.getStartsAt()
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

    public int getValidityDays() {
        return validityDays;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }
}
