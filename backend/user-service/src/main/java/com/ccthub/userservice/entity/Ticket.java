package com.ccthub.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 票种实体 - 定义景区的门票产品
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的景区ID
     */
    @Column(name = "scenic_spot_id", nullable = false)
    private Long scenicSpotId;

    /**
     * 票种名称
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * 票种类型：1-单票，2-联票，3-套票
     */
    @Column(nullable = false)
    private Integer type;

    /**
     * 票种描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 有效期类型：1-指定日期，2-有效天数
     */
    @Column(name = "validity_type", nullable = false)
    private Integer validityType;

    /**
     * 有效天数（当validityType=2时使用）
     */
    @Column(name = "valid_days")
    private Integer validDays;

    /**
     * 可提前预订天数
     */
    @Column(name = "advance_days")
    private Integer advanceDays;

    /**
     * 退款规则配置（JSON格式）
     */
    @Column(name = "refund_policy", columnDefinition = "JSON", nullable = false)
    private String refundPolicy;

    /**
     * 改签规则配置（JSON格式）
     */
    @Column(name = "change_policy", columnDefinition = "JSON", nullable = false)
    private String changePolicy;

    /**
     * 每用户限购数量
     */
    @Column(name = "limit_per_user")
    private Integer limitPerUser;

    /**
     * 每订单限购数量
     */
    @Column(name = "limit_per_order")
    private Integer limitPerOrder;

    /**
     * 每日限购数量
     */
    @Column(name = "limit_per_day")
    private Integer limitPerDay;

    /**
     * 是否需要实名
     */
    @Column(name = "require_real_name")
    private Boolean requireRealName;

    /**
     * 是否需要身份证
     */
    @Column(name = "require_id_card")
    private Boolean requireIdCard;

    /**
     * 核验方式：1-二维码，2-人脸，3-身份证
     */
    @Column(name = "verification_mode")
    private Integer verificationMode;

    /**
     * 状态：1-上架，0-下架
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 票种类型常量
     */
    public static class Type {
        public static final int SINGLE = 1; // 单票
        public static final int COMBO = 2; // 联票
        public static final int PACKAGE = 3; // 套票
    }

    /**
     * 有效期类型常量
     */
    public static class ValidityType {
        public static final int FIXED_DATE = 1; // 指定日期
        public static final int VALID_DAYS = 2; // 有效天数
    }

    /**
     * 状态常量
     */
    public static class Status {
        public static final int INACTIVE = 0; // 下架
        public static final int ACTIVE = 1; // 上架
    }
}
