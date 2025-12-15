package com.ccthub.userservice.dto.payment;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 退款申请请求
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
public class RefundRequest {

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /**
     * 退款类型: 1-全额退款, 2-部分退款
     */
    @NotNull(message = "退款类型不能为空")
    private Integer refundType;

    /**
     * 退款金额
     */
    @NotNull(message = "退款金额不能为空")
    @Positive(message = "退款金额必须大于0")
    private BigDecimal refundAmount;

    /**
     * 退款数量（部分退款时必填）
     */
    private Integer refundQuantity;

    /**
     * 退款原因
     */
    @NotBlank(message = "退款原因不能为空")
    private String refundReason;

    /**
     * 退款凭证（图片URL，多个用逗号分隔）
     */
    private String refundEvidence;

    /**
     * 申请用户ID
     */
    @NotNull(message = "申请用户ID不能为空")
    private Long userId;
}
