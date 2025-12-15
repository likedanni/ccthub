package com.ccthub.userservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 订单退款实体
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
@Entity
@Table(name = "order_refunds")
public class OrderRefund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refund_no", unique = true, nullable = false, length = 32)
    private String refundNo;

    @Column(name = "order_no", nullable = false, length = 32)
    private String orderNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "refund_type", nullable = false)
    private Integer refundType; // 1-全额退款, 2-部分退款

    @Column(name = "refund_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_reason", nullable = false, length = 200)
    private String refundReason;

    @Column(name = "refund_evidence", columnDefinition = "JSON")
    private String refundEvidence; // JSON格式的退款凭证

    @Column(name = "status")
    private Integer status; // 0-待审核, 1-审核通过, 2-审核拒绝, 3-退款中, 4-成功, 5-失败

    @Column(name = "auditor_id")
    private Long auditorId;

    @Column(name = "audited_at")
    private LocalDateTime auditedAt;

    @Column(name = "audit_note", length = 500)
    private String auditNote;

    @Column(name = "payment_refund_no", length = 64)
    private String paymentRefundNo; // 支付渠道退款流水号

    @Column(name = "payment_refund_at")
    private LocalDateTime paymentRefundAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (refundNo == null) {
            refundNo = "REFUND" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
        }
        if (status == null) {
            status = STATUS_PENDING_AUDIT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 状态常量
    public static final int STATUS_PENDING_AUDIT = 0; // 待审核
    public static final int STATUS_APPROVED = 1; // 审核通过
    public static final int STATUS_REJECTED = 2; // 审核拒绝
    public static final int STATUS_REFUNDING = 3; // 退款中
    public static final int STATUS_SUCCESS = 4; // 成功
    public static final int STATUS_FAILED = 5; // 失败

    // 退款类型常量
    public static final int TYPE_FULL = 1; // 全额退款
    public static final int TYPE_PARTIAL = 2; // 部分退款
}
