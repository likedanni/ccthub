package com.ccthub.userservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 钱包充值请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletRechargeRequest {

    /**
     * 充值金额
     */
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0")
    private BigDecimal amount;

    /**
     * 支付方式: wechat, alipay
     */
    @NotNull(message = "支付方式不能为空")
    private String paymentType;

    /**
     * 支付渠道: miniapp, app, h5
     */
    @NotNull(message = "支付渠道不能为空")
    private String paymentChannel;
}
