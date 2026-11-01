package com.apiece.coupon.application;

import com.apiece.coupon.api.dto.CreateCouponRequest;
import com.apiece.coupon.domain.Coupon;
import com.apiece.coupon.domain.CouponRepository;
import com.apiece.coupon.domain.Issuance;
import com.apiece.coupon.infrastructure.messaging.IssuanceRequestProducer;
import com.apiece.coupon.infrastructure.messaging.IssuanceRequested;
import com.apiece.coupon.support.NotStartedException;
import java.time.LocalDateTime;
import java.util.Objects;

import com.apiece.coupon.support.SoldOutException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssuePolicyReader couponIssuePolicyReader;
    private final CouponIssuer couponIssuer;
    private final IssuanceRequestProducer issuanceRequestProducer;
    private final SoldOutState soldOutState;

    public CouponService(
            CouponRepository couponRepository,
            CouponIssuePolicyReader couponIssuePolicyReader,
            CouponIssuer couponIssuer,
            IssuanceRequestProducer issuanceRequestProducer,
            SoldOutState soldOutState
    ) {
        this.couponRepository = couponRepository;
        this.couponIssuePolicyReader = couponIssuePolicyReader;
        this.couponIssuer = couponIssuer;
        this.issuanceRequestProducer = issuanceRequestProducer;
        this.soldOutState = soldOutState;
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
        if (soldOutState.isSoldOut(couponId)) {
            throw new SoldOutException();
        }

        CouponIssuePolicy policy = couponIssuePolicyReader.get(couponId);

        LocalDateTime now = LocalDateTime.now();
        if (!policy.isBookingOpen(now)) {
            throw new NotStartedException();
        }

        couponIssuer.tryIssue(couponId, userId);

        LocalDateTime expiresAt = now.plusDays(policy.getValidityDays());
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
}
