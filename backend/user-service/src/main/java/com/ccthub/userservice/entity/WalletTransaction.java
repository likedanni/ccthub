package com.ccthub.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包流水实体
 * 对应表：wallet_transactions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    /**
     * 流水ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 流水号，唯一
     */
    @Column(name = "transaction_no", nullable = false, unique = true, length = 32)
    private String transactionNo;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 钱包ID
     */
    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    /**
     * 交易类型: 1-充值, 2-消费, 3-退款, 4-提现, 5-冻结, 6-解冻
     */
    @Column(name = "transaction_type", nullable = false)
    private Integer transactionType;

    /**
     * 变动金额（正数表示增加，负数表示减少）
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 变动后余额
     */
    @Column(name = "balance_after", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAfter;

    /**
     * 关联订单号
     */
    @Column(name = "order_no", length = 32)
    private String orderNo;

    /**
     * 关联支付流水号
     */
    @Column(name = "payment_no", length = 32)
    private String paymentNo;

    /**
     * 关联退款流水号
     */
    @Column(name = "refund_no", length = 32)
    private String refundNo;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;

    /**
     * 状态: 1-成功, 0-失败, 2-处理中
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 交易类型常量
     */
    public static class Type {
        public static final Integer RECHARGE = 1;      // 充值
        public static final Integer CONSUMPTION = 2;   // 消费
        public static final Integer REFUND = 3;        // 退款
        public static final Integer WITHDRAW = 4;      // 提现
        public static final Integer FREEZE = 5;        // 冻结
        public static final Integer UNFREEZE = 6;      // 解冻
    }

    /**
     * 状态常量
     */
    public static class Status {
        public static final Integer FAILED = 0;      // 失败
        public static final Integer SUCCESS = 1;     // 成功
        public static final Integer PROCESSING = 2;  // 处理中
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = Status.PROCESSING;
        }
    }
}
