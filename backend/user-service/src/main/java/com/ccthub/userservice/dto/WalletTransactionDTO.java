package com.ccthub.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包流水DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionDTO {

    /**
     * 流水ID
     */
    private Long id;

    /**
     * 流水号
     */
    private String transactionNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 交易类型: 1-充值, 2-消费, 3-退款, 4-提现, 5-冻结, 6-解冻
     */
    private Integer transactionType;

    /**
     * 交易类型描述
     */
    private String transactionTypeDesc;

    /**
     * 变动金额
     */
    private BigDecimal amount;

    /**
     * 变动后余额
     */
    private BigDecimal balanceAfter;

    /**
     * 关联订单号
     */
    private String orderNo;

    /**
     * 关联支付流水号
     */
    private String paymentNo;

    /**
     * 关联退款流水号
     */
    private String refundNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态: 1-成功, 0-失败, 2-处理中
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
