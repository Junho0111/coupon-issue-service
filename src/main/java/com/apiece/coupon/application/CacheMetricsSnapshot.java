package com.apiece.coupon.application;

public class CacheMetricsSnapshot {

    private final long couponDbReads;
    private final long couponCacheHits;
    private final long soldOutRedisExists;
    private final long soldOutFastPathHits;

    public CacheMetricsSnapshot(
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
