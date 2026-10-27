package com.apiece.coupon.application;

import com.apiece.coupon.domain.CouponRepository;
import com.apiece.coupon.infrastructure.cache.CacheProperties;
import com.apiece.coupon.infrastructure.cache.CouponCacheRepository;
import com.apiece.coupon.support.CouponNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CouponIssuePolicyReader {

    private final CouponRepository couponRepository;
    private final CouponCacheRepository couponCacheRepository;
    private final CacheProperties cacheProperties;

    public CouponIssuePolicyReader(
            CouponRepository couponRepository,
            CouponCacheRepository couponCacheRepository,
            CacheProperties cacheProperties
    ) {
        this.couponRepository = couponRepository;
        this.couponCacheRepository = couponCacheRepository;
        this.cacheProperties = cacheProperties;
    }

    public CouponIssuePolicy get(long id) {
        return couponCacheRepository.getIssuePolicyOrLoad(id, () -> {
            simulateLoadLatency();
            return CouponIssuePolicy.from(
                    couponRepository.findById(id).orElseThrow(CouponNotFoundException::new)
            );
        });
    }

    // DB 조회 지연 시뮬레이션. 인터럽트되면 플래그를 복구하고 중단한다.
    private void simulateLoadLatency() {
        try {
            Thread.sleep(cacheProperties.getSimulatedLoadLatencyMs());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
