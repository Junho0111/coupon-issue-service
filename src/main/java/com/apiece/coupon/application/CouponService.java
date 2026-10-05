package com.apiece.coupon.application;

import com.apiece.coupon.api.dto.CreateCouponRequest;
import com.apiece.coupon.domain.Coupon;
import com.apiece.coupon.domain.CouponRepository;
import com.apiece.coupon.domain.Issuance;
import com.apiece.coupon.domain.IssuanceRepository;
import com.apiece.coupon.infrastructure.messaging.InMemoryIssuanceQueue;
import com.apiece.coupon.infrastructure.messaging.IssuanceRequested;
import com.apiece.coupon.support.CouponNotFoundException;
import com.apiece.coupon.support.NotStartedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssuer couponIssuer;
    private final InMemoryIssuanceQueue issuanceQueue;


    public CouponService(
            CouponRepository couponRepository,
            CouponIssuer couponIssuer,
            InMemoryIssuanceQueue issuanceQueue
    ) {
        this.couponRepository = couponRepository;
        this.couponIssuer = couponIssuer;
        this.issuanceQueue = issuanceQueue;
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
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(CouponNotFoundException::new);

        LocalDateTime now = LocalDateTime.now();
        if (!coupon.isBookingOpen(now)) {
            throw new NotStartedException();
        }

        couponIssuer.tryIssue(couponId, userId);

        LocalDateTime expiresAt = now.plusDays(coupon.getValidityDays());
        //메시지 발행
        issuanceQueue.enqueue(
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
}
