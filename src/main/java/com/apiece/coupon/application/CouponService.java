package com.apiece.coupon.application;

import com.apiece.coupon.api.dto.CreateCouponRequest;
import com.apiece.coupon.domain.Coupon;
import com.apiece.coupon.domain.CouponRepository;
import com.apiece.coupon.domain.Issuance;
import com.apiece.coupon.infrastructure.cache.CacheProperties;
import com.apiece.coupon.infrastructure.messaging.IssuanceRequestProducer;
import com.apiece.coupon.infrastructure.messaging.IssuanceRequested;
import com.apiece.coupon.support.CouponNotFoundException;
import com.apiece.coupon.support.NotStartedException;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CacheProperties cacheProperties;
    private final CacheMetrics cacheMetrics;
    private final CouponIssuer couponIssuer;
    private final IssuanceRequestProducer issuanceRequestProducer;

    public CouponService(
            CouponRepository couponRepository,
            CacheProperties cacheProperties,
            CacheMetrics cacheMetrics,
            CouponIssuer couponIssuer,
            IssuanceRequestProducer issuanceRequestProducer
    ) {
        this.couponRepository = couponRepository;
        this.cacheProperties = cacheProperties;
        this.cacheMetrics = cacheMetrics;
        this.couponIssuer = couponIssuer;
        this.issuanceRequestProducer = issuanceRequestProducer;
    }

    @Transactional
    public Coupon createCoupon(CreateCouponRequest request) {
        Coupon coupon = couponRepository.save(
                new Coupon(
                        request.getName(),
                        request.getTotalQuantity(),
                        request.getValidityDays(),
                        request.getStartsAt()
                )
        );
        couponIssuer.initStock(Objects.requireNonNull(coupon.getId()), coupon.getTotalQuantity());
        return coupon;
    }

    public Issuance issue(long couponId, long userId) {
        cacheMetrics.incrementCouponDbRead(); //디비를 읽기전에 한번 기록해주기 위해

        simulateLoadLatency(); // 조회하고 응답하는 속도를 일부로 늦추기 위해 DB가 로컬에 있어서 너무 빠름

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(CouponNotFoundException::new);

        LocalDateTime now = LocalDateTime.now();
        if (!coupon.isBookingOpen(now)) {
            throw new NotStartedException();
        }

        couponIssuer.tryIssue(couponId, userId);

        LocalDateTime expiresAt = now.plusDays(coupon.getValidityDays());

        issuanceRequestProducer.publish(
                new IssuanceRequested(
                        couponId,
                        userId,
                        now,
                        expiresAt
                )
        );

        return new Issuance(
                userId,
                couponId,
                now,
                expiresAt
        );
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
