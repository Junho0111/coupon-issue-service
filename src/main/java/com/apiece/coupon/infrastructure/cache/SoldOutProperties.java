package com.apiece.coupon.infrastructure.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "coupon.sold-out")
public class SoldOutProperties {

    private final long ttlSeconds;
    private final long fastPathTtlMs;

    public SoldOutProperties(long ttlSeconds, long fastPathTtlMs) {
        this.ttlSeconds = ttlSeconds;
        this.fastPathTtlMs = fastPathTtlMs;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public long getFastPathTtlMs() {
        return fastPathTtlMs;
    }
}
