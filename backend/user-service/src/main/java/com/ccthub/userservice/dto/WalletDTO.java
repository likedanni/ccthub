package com.ccthub.userservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * 用户手机号
     */
    private String phone;

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

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
