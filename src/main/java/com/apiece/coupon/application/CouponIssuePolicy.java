package com.apiece.coupon.application;

import com.apiece.coupon.domain.Coupon;
import java.time.LocalDateTime;

public class CouponIssuePolicy {

    private final LocalDateTime startsAt;
    private final int validityDays;

    public CouponIssuePolicy(LocalDateTime startsAt, int validityDays) {
        this.startsAt = startsAt;
        this.validityDays = validityDays;
    }

    public static CouponIssuePolicy from(Coupon coupon) {
        return new CouponIssuePolicy(
                coupon.getStartsAt(),
                coupon.getValidityDays()
        );
    }

    public boolean isBookingOpen(LocalDateTime now) {
        return startsAt == null || !now.isBefore(startsAt);
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public int getValidityDays() {
        return validityDays;
    }
}
