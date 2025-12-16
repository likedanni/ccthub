package com.ccthub.userservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 用户优惠券实体
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Data
@Entity
@Table(name = "user_coupons")
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 优惠券ID
     */
    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    /**
     * 唯一券码
     */
    @Column(name = "coupon_code", unique = true, nullable = false, length = 50)
    private String couponCode;

    /**
     * 状态: 0-未使用, 1-已使用, 2-已过期
     */
    @Column(nullable = false)
    private Integer status = 0;

    /**
     * 领取时间
     */
    @CreationTimestamp
    @Column(name = "received_at", updatable = false)
    private LocalDateTime receivedAt;

    /**
     * 使用时间
     */
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    /**
     * 使用的订单号
     */
    @Column(name = "order_no", length = 32)
    private String orderNo;

    /**
     * 该张券的实际过期时间
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 用户优惠券状态常量
     */
    public static class Status {
        public static final int UNUSED = 0; // 未使用
        public static final int USED = 1; // 已使用
        public static final int EXPIRED = 2; // 已过期
    }
}
