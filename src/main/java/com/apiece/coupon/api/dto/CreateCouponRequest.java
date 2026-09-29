package com.apiece.coupon.api.dto;

import java.time.LocalDateTime;

public class CreateCouponRequest {

    private final String name;
    private final int totalQuantity;
    private final int validityDays;
    private final LocalDateTime startsAt;

    /**
     * 생략된 필드는 기본값으로 채운다. 래퍼 타입으로 받는 이유는 "생략(null)"과 "0을 명시"를 구분하기 위해서다.
     */
    public CreateCouponRequest(
            String name,
            Integer totalQuantity,
            Integer validityDays,
            LocalDateTime startsAt
    ) {
        this.name = name;
        this.totalQuantity = totalQuantity != null ? totalQuantity : 5000;
        this.validityDays = validityDays != null ? validityDays : 7;
        this.startsAt = startsAt;
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
