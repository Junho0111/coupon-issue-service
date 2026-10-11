package com.apiece.coupon.api.dto;

import com.apiece.coupon.application.CacheMetricsSnapshot;

public class CacheMetricsResponse {

    private final long couponDbReads;
    private final long couponCacheHits;
    private final long soldOutRedisExists; // Redis
    private final long soldOutFastPathHits; // L1 Cache

    public CacheMetricsResponse(
            long couponDbReads,
            long couponCacheHits,
            long soldOutRedisExists,
            long soldOutFastPathHits
    ) {
        this.couponDbReads = couponDbReads;
        this.couponCacheHits = couponCacheHits;
        this.soldOutRedisExists = soldOutRedisExists;
        this.soldOutFastPathHits = soldOutFastPathHits;
    }

    public static CacheMetricsResponse from(CacheMetricsSnapshot snapshot) {
        return new CacheMetricsResponse(
                snapshot.getCouponDbReads(),
                snapshot.getCouponCacheHits(),
                snapshot.getSoldOutRedisExists(),
                snapshot.getSoldOutFastPathHits()
        );
    }

    public long getCouponDbReads() {
        return couponDbReads;
    }

    public long getCouponCacheHits() {
        return couponCacheHits;
    }

    public long getSoldOutRedisExists() {
        return soldOutRedisExists;
    }

    public long getSoldOutFastPathHits() {
        return soldOutFastPathHits;
    }
}
