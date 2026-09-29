package com.apiece.coupon.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :id")
    Coupon findByIdForUpdate(@Param("id") Long id);
}

// forUpdate라고 @Modifying붙는게 아니라 수정 목적으로 조회를 하는것임을 알려주기위해 이름을 저렇게 지은거고
// 때문에 @Lock(LockModeType.PESSIMISTIC_WRITE)을 붙여 이름대로 비관적 락을 수행하게 해주며,
// 이는 실제 SQL 뒤에 FOR UPDATE를 붙여줌

// 추가
// 쿠폰 발급 수량을 업데이트 할 것이기때문에 PESSIMISTIC_WRITE을 붙임
