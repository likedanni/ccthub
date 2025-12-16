package com.ccthub.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户积分流水实体
 * 对应表：user_points
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_points")
public class UserPoints {

    /**
     * 流水ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 变动类型: 1-增加, 2-减少
     */
    @Column(name = "change_type", nullable = false)
    private Integer changeType;

    /**
     * 积分来源/用途
     */
    @Column(name = "source", nullable = false, length = 50)
    private String source;

    /**
     * 变动积分数额
     */
    @Column(name = "points", nullable = false)
    private Integer points;

    /**
     * 变动后实时余额
     */
    @Column(name = "current_balance", nullable = false)
    private Integer currentBalance;

    /**
     * 关联订单号
     */
    @Column(name = "order_no", length = 32)
    private String orderNo;

    /**
     * 关联活动ID
     */
    @Column(name = "activity_id")
    private Long activityId;

    /**
     * 积分过期时间
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * 状态: 1-有效, 0-无效
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;

    /**
     * 变动类型常量
     */
    public static class ChangeType {
        public static final Integer INCREASE = 1;  // 增加
        public static final Integer DECREASE = 2;  // 减少
    }

    /**
     * 积分来源常量
     */
    public static class Source {
        public static final String ORDER_PAY = "order_pay";           // 订单消费
        public static final String DAILY_CHECKIN = "daily_checkin";   // 每日签到
        public static final String INVITE = "invite";                 // 邀请好友
        public static final String EXCHANGE = "exchange";             // 积分兑换
        public static final String ACTIVITY = "activity";             // 活动奖励
        public static final String SHARE = "share";                   // 分享获得
        public static final String DEDUCT = "deduct";                 // 积分抵扣
        public static final String EXPIRE = "expire";                 // 积分过期
    }

    /**
     * 状态常量
     */
    public static class Status {
        public static final Integer INVALID = 0;  // 无效
        public static final Integer VALID = 1;    // 有效
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = Status.VALID;
        }
    }
}
