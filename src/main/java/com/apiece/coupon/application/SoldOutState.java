package com.apiece.coupon.application;

import com.apiece.coupon.infrastructure.cache.SoldOutProperties;
import com.apiece.coupon.infrastructure.cache.SoldOutRedisRepository;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.stereotype.Component;

import java.time.Duration;

// 매진 플래그를 in-process 로 캐싱해 Redis EXISTS 를 인스턴스 당 초당 1회로 줄임.
// fast-path TTL 동안 실제 매진을 아직 모르는 요청은 Lua 가 0 으로 최종 거절해서 잡아낸다.
@Component
public class SoldOutState {

    private static final long MAX_TRACKED_COUPONS = 1_000L;

    private final CacheMetrics cacheMetrics;
    private final LoadingCache<Long, Boolean> soldOutCache;

    public SoldOutState(
            SoldOutRedisRepository soldOutRedisRepository,
            CacheMetrics cacheMetrics,
            SoldOutProperties soldOutProperties
    ) {
        this.cacheMetrics = cacheMetrics;
        this.soldOutCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMillis(soldOutProperties.getFastPathTtlMs()))
                .maximumSize(MAX_TRACKED_COUPONS)
                .build(couponId -> {
                    cacheMetrics.incrementSoldOutRedisExists();
                    return soldOutRedisRepository.isFlagged(couponId);
                });
    }

    public boolean isSoldOut(long couponId) {
        Boolean cached = soldOutCache.get(couponId);
        boolean soldOut = cached != null && cached;
        if (soldOut) {
            cacheMetrics.incrementSoldOutFastPathHit();
        }
        return soldOut;
    }
}
