package com.ccthub.userservice.dto.payment;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 支付订单创建请求
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
public class PaymentRequest {

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /**
     * 支付类型: wechat-微信, alipay-支付宝, balance-余额
     */
    @NotBlank(message = "支付类型不能为空")
    private String paymentType;

    /**
     * 支付渠道: miniapp-小程序, app-APP, h5-H5, native-扫码
     */
    @NotBlank(message = "支付渠道不能为空")
    private String paymentChannel;

    /**
     * 支付金额
     */
    @NotNull(message = "支付金额不能为空")
    @Positive(message = "支付金额必须大于0")
    private BigDecimal paymentAmount;

    /**
     * 支付方标识（如微信openid、用户ID等）
     */
    @NotBlank(message = "支付方标识不能为空")
    private String payerId;

    /**
     * 幂等性令牌（防重复提交）
     */
    private String nonce;
}
