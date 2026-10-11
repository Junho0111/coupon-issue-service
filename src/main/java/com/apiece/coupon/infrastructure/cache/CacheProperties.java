package com.apiece.coupon.infrastructure.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "coupon.cache")
public class CacheProperties {

    private final long ttlMs;
    private final long simulatedLoadLatencyMs;

    public CacheProperties(long ttlMs, long simulatedLoadLatencyMs) {
        this.ttlMs = ttlMs;
        this.simulatedLoadLatencyMs = simulatedLoadLatencyMs;
    }

    public long getTtlMs() {
        return ttlMs;
    }

    public long getSimulatedLoadLatencyMs() {
        return simulatedLoadLatencyMs;
    }
}
