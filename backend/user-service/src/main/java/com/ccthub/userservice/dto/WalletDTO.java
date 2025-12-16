package com.ccthub.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 钱包信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {

    /**
     * 钱包ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 可用余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal frozenBalance;

    /**
     * 累计充值
     */
    private BigDecimal totalDeposit;

    /**
     * 累计消费
     */
    private BigDecimal totalConsumption;

    /**
     * 安全等级
     */
    private Integer securityLevel;

    /**
     * 是否设置支付密码
     */
    private Boolean hasPayPassword;

    /**
     * 状态: 1-正常, 0-冻结
     */
    private Integer status;
}
