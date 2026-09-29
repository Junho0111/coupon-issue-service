package com.apiece.coupon.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "issuance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_issuance_user_coupon",
                        columnNames = {"user_id", "coupon_id"}
                )
        },
        indexes = {
                @Index(name = "idx_issuance_status", columnList = "status"),
                @Index(name = "idx_issuance_coupon", columnList = "coupon_id")
        }
)
public class Issuance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "coupon_id", nullable = false)
    private long couponId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private IssuanceStatus status;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    protected Issuance() {
    }

    public Issuance(long userId, long couponId, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        this(userId, couponId, IssuanceStatus.ISSUED, issuedAt, expiresAt, null, null);
    }

    public Issuance(
            long userId,
            long couponId,
            IssuanceStatus status,
            LocalDateTime issuedAt,
            LocalDateTime expiresAt,
            LocalDateTime usedAt,
            Long id
    ) {
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.id = id;
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(expiresAt);
    }

    public void markUsed(LocalDateTime now) {
        this.status = IssuanceStatus.USED;
        this.usedAt = now;
    }

    public void markExpired() {
        this.status = IssuanceStatus.EXPIRED;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCouponId() {
        return couponId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
    }

    public IssuanceStatus getStatus() {
        return status;
    }

    public void setStatus(IssuanceStatus status) {
        this.status = status;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
