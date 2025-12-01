package org.example.demomanagementsystemcproject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon")
public class UserCouponEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    private String status = "UNUSED"; // UNUSED, USED, EXPIRED

    @Column(name = "get_time")
    private LocalDateTime getTime;

    @Column(name = "use_time")
    private LocalDateTime useTime;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "expired_time", nullable = false)
    private LocalDateTime expiredTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (getTime == null) {
            getTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getGetTime() { return getTime; }
    public void setGetTime(LocalDateTime getTime) { this.getTime = getTime; }
    public LocalDateTime getUseTime() { return useTime; }
    public void setUseTime(LocalDateTime useTime) { this.useTime = useTime; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public LocalDateTime getExpiredTime() { return expiredTime; }
    public void setExpiredTime(LocalDateTime expiredTime) { this.expiredTime = expiredTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}