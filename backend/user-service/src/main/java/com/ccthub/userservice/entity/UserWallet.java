package com.ccthub.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户钱包实体
 * 对应表：user_wallet
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_wallet")
public class UserWallet {

    /**
     * 钱包ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID，唯一
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * 可用余额
     */
    @Column(name = "balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    /**
     * 冻结金额(如提现中、支付中)
     */
    @Column(name = "frozen_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal frozenBalance;

    /**
     * 累计充值
     */
    @Column(name = "total_deposit", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDeposit;

    /**
     * 累计消费
     */
    @Column(name = "total_consumption", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalConsumption;

    /**
     * 安全等级
     */
    @Column(name = "security_level", nullable = false)
    private Integer securityLevel;

    /**
     * 支付密码哈希
     */
    @Column(name = "pay_password_hash", length = 128)
    private String payPasswordHash;

    /**
     * 状态: 1-正常, 0-冻结
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 乐观锁版本号
     */
    @Version
    @Column(name = "version")
    private Integer version;

    /**
     * 钱包状态常量
     */
    public static class Status {
        public static final Integer FROZEN = 0;  // 冻结
        public static final Integer NORMAL = 1;  // 正常
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        
        // 设置默认值
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
        if (this.frozenBalance == null) {
            this.frozenBalance = BigDecimal.ZERO;
        }
        if (this.totalDeposit == null) {
            this.totalDeposit = BigDecimal.ZERO;
        }
        if (this.totalConsumption == null) {
            this.totalConsumption = BigDecimal.ZERO;
        }
        if (this.securityLevel == null) {
            this.securityLevel = 1;
        }
        if (this.status == null) {
            this.status = Status.NORMAL;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
