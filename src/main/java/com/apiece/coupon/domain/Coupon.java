package com.apiece.coupon.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "issued_quantity", nullable = false)
    private int issuedQuantity;

    @Column(name = "validity_days", nullable = false)
    private int validityDays;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    protected Coupon() {
    }

    public Coupon(String name, int totalQuantity, int validityDays, LocalDateTime startsAt) {
        this(name, totalQuantity, 0, validityDays, startsAt, LocalDateTime.now(), null);
    }

    public Coupon(
            String name,
            int totalQuantity,
            int issuedQuantity,
            int validityDays,
            LocalDateTime startsAt,
            LocalDateTime createdAt,
            Long id
    ) {
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = issuedQuantity;
        this.validityDays = validityDays;
        this.startsAt = startsAt;
        this.createdAt = createdAt;
        this.id = id;
    }

    public boolean isBookingOpen(LocalDateTime now) {
        return startsAt == null || !now.isBefore(startsAt);
    }

    public boolean isSoldOut() {
        return issuedQuantity >= totalQuantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getIssuedQuantity() {
        return issuedQuantity;
    }

    public void setIssuedQuantity(int issuedQuantity) {
        this.issuedQuantity = issuedQuantity;
    }

    public int getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(int validityDays) {
        this.validityDays = validityDays;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
