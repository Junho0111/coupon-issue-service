package com.apiece.coupon.infrastructure.messaging;

import com.apiece.coupon.domain.CouponRepository;
import com.apiece.coupon.domain.Issuance;
import com.apiece.coupon.domain.IssuanceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class IssuanceTransactionalWriter {

    private final IssuanceRepository issuanceRepository;
    private final CouponRepository couponRepository;

    public IssuanceTransactionalWriter(
            IssuanceRepository issuanceRepository,
            CouponRepository couponRepository
    ) {
        this.issuanceRepository = issuanceRepository;
        this.couponRepository = couponRepository;
    }

    @Transactional
    public void insertAndIncrement(IssuanceRequested event) {
        issuanceRepository.save(
                new Issuance(
                        event.getUserId(),
                        event.getCouponId(),
                        event.getIssuedAt(),
                        event.getExpiresAt()
                )
        );
        couponRepository.incrementIssuedQuantity(event.getCouponId());
    }
}
