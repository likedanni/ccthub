package com.ccthub.userservice.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付信息响应
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
public class PaymentResponse {

    /**
     * 支付ID
     */
    private Long id;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付类型
     */
    private String paymentType;

    /**
     * 支付类型文本
     */
    private String paymentTypeText;

    /**
     * 支付渠道
     */
    private String paymentChannel;

    /**
     * 支付渠道文本
     */
    private String paymentChannelText;

    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 支付状态: 0-待支付, 1-成功, 2-失败, 3-关闭, 4-处理中
     */
    private Integer status;

    /**
     * 支付状态文本
     */
    private String statusText;

    /**
     * 第三方支付流水号
     */
    private String thirdPartyNo;

    /**
     * 支付方标识（如微信openid、用户ID等）
     */
    private String payerId;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 回调时间
     */
    private LocalDateTime callbackTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
