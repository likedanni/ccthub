package com.ccthub.userservice.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 退款审核请求
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
public class RefundAuditRequest {

    /**
     * 退款单号
     */
    @NotBlank(message = "退款单号不能为空")
    private String refundNo;

    /**
     * 审核状态: 1-审核通过, 2-审核拒绝
     */
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    /**
     * 审核备注
     */
    private String auditNote;

    /**
     * 审核人ID
     */
    @NotNull(message = "审核人ID不能为空")
    private Long auditorId;
}
