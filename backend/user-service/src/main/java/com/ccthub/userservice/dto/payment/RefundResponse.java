package com.ccthub.userservice.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款信息响应
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
public class RefundResponse {

    /**
     * 退款ID
     */
    private Long id;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 申请用户ID
     */
    private Long userId;

    /**
     * 退款类型: 1-全额退款, 2-部分退款
     */
    private Integer refundType;

    /**
     * 退款类型文本
     */
    private String refundTypeText;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款凭证
     */
    private String refundEvidence;

    /**
     * 退款状态: 0-待审核, 1-审核通过, 2-审核拒绝, 3-退款中, 4-成功, 5-失败
     */
    private Integer status;

    /**
     * 退款状态文本
     */
    private String statusText;

    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 审核时间
     */
    private LocalDateTime auditedAt;

    /**
     * 审核备注
     */
    private String auditNote;

    /**
     * 支付渠道退款单号
     */
    private String paymentRefundNo;

    /**
     * 支付渠道退款时间
     */
    private LocalDateTime paymentRefundAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
