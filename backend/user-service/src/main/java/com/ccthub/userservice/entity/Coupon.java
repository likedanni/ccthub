package com.ccthub.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券模板实体
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Data
@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 优惠券名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 券类型: 1-满减券, 2-折扣券, 3-代金券
     */
    @Column(nullable = false)
    private Integer type;

    /**
     * 优惠值: 满减金额、折扣(0.9)或固定金额
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    /**
     * 最低消费金额门槛
     */
    @Column(name = "min_spend", precision = 10, scale = 2)
    private BigDecimal minSpend;

    /**
     * 适用范围: 1-全平台, 2-指定商户, 3-指定商品
     */
    @Column(name = "applicable_type", nullable = false)
    private Integer applicableType;

    /**
     * 适用的商户或商品ID列表，JSON数组格式
     */
    @Column(name = "applicable_ids", columnDefinition = "json")
    private String applicableIds;

    /**
     * 有效期类型: 1-固定时段, 2-领取后生效
     */
    @Column(name = "validity_type", nullable = false)
    private Integer validityType;

    /**
     * 领取后有效天数（当validity_type=2时）
     */
    @Column(name = "valid_days")
    private Integer validDays;

    /**
     * 有效期开始时间
     */
    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    /**
     * 有效期结束时间
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * 发放总张数，null表示不限量
     */
    @Column(name = "total_quantity")
    private Integer totalQuantity;

    /**
     * 剩余可发放张数
     */
    @Column(name = "remaining_quantity")
    private Integer remainingQuantity;

    /**
     * 每人限领张数
     */
    @Column(name = "limit_per_user")
    private Integer limitPerUser = 1;

    /**
     * 状态: 0-未开始, 1-发放中, 2-已结束, 3-停用
     */
    @Column(nullable = false)
    private Integer status = 0;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 优惠券类型常量
     */
    public static class CouponType {
        public static final int FULL_DISCOUNT = 1; // 满减券
        public static final int PERCENT_OFF = 2;   // 折扣券
        public static final int CASH_VOUCHER = 3;  // 代金券
    }

    /**
     * 适用范围常量
     */
    public static class ApplicableType {
        public static final int ALL_PLATFORM = 1;     // 全平台
        public static final int SPECIFIC_MERCHANT = 2; // 指定商户
        public static final int SPECIFIC_PRODUCT = 3;  // 指定商品
    }

    /**
     * 有效期类型常量
     */
    public static class ValidityType {
        public static final int FIXED_PERIOD = 1;  // 固定时段
        public static final int AFTER_RECEIVE = 2; // 领取后生效
    }

    /**
     * 优惠券状态常量
     */
    public static class Status {
        public static final int NOT_STARTED = 0;  // 未开始
        public static final int IN_PROGRESS = 1;  // 发放中
        public static final int ENDED = 2;        // 已结束
        public static final int DISABLED = 3;     // 停用
    }
}
