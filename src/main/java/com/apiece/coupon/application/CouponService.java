package com.apiece.coupon.application;

import com.apiece.coupon.api.dto.CreateCouponRequest;
import com.apiece.coupon.domain.Coupon;
import com.apiece.coupon.domain.CouponRepository;
import com.apiece.coupon.domain.Issuance;
import com.apiece.coupon.domain.IssuanceRepository;
import com.apiece.coupon.support.AlreadyIssuedException;
import com.apiece.coupon.support.CouponNotFoundException;
import com.apiece.coupon.support.NotStartedException;
import com.apiece.coupon.support.SoldOutException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuanceRepository issuanceRepository;

    public CouponService(CouponRepository couponRepository, IssuanceRepository issuanceRepository) {
        this.couponRepository = couponRepository;
        this.issuanceRepository = issuanceRepository;
    }

    @Transactional
    public Coupon createCoupon(CreateCouponRequest request) {
        Coupon coupon = new Coupon(
                request.getName(),
                request.getTotalQuantity(),
                request.getValidityDays(),
                request.getStartsAt()
        );
        return couponRepository.save(coupon);
    }

    // v0: 동시성 방어 의도적 제외. v1(02-coupon-concurrency-design.md)에서 해결.
    @Transactional
    public Issuance issue(long couponId, long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(CouponNotFoundException::new);

        LocalDateTime now = LocalDateTime.now();

        if (!coupon.isBookingOpen(now)) {
            throw new NotStartedException();
        }
        if (coupon.isSoldOut()) {
            throw new SoldOutException();
        }
        if (issuanceRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new AlreadyIssuedException();
        }

        coupon.setIssuedQuantity(coupon.getIssuedQuantity() + 1);

        return issuanceRepository.save(
                new Issuance(
                        userId,
                        couponId,
                        now,
                        now.plusDays(coupon.getValidityDays())
                )
        );
    }
}
