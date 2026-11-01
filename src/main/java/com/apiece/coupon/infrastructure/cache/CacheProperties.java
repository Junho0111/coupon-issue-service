package com.apiece.coupon.infrastructure.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "coupon.cache")
public class CacheProperties {

    private final long ttlMs;
    private final long freshMs;
    private final long simulatedLoadLatencyMs;

    public CacheProperties(long ttlMs, long freshMs, long simulatedLoadLatencyMs) {
        this.ttlMs = ttlMs;
        this.freshMs = freshMs;
        this.simulatedLoadLatencyMs = simulatedLoadLatencyMs;
    }

    public long getTtlMs() {
        return ttlMs;
    }

    public long getFreshMs() {
        return freshMs;
    }

    public long getSimulatedLoadLatencyMs() {
        return simulatedLoadLatencyMs;
    }
}
