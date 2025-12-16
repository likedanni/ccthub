package com.ccthub.userservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 积分流水DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPointsDTO {

    /**
     * 流水ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 变动类型: 1-增加, 2-减少
     */
    private Integer changeType;

    /**
     * 变动类型描述
     */
    private String changeTypeDesc;

    /**
     * 积分来源/用途
     */
    private String source;

    /**
     * 来源描述
     */
    private String sourceDesc;

    /**
     * 变动积分数额
     */
    private Integer points;

    /**
     * 变动后实时余额
     */
    private Integer currentBalance;

    /**
     * 关联订单号
     */
    private String orderNo;

    /**
     * 关联活动ID
     */
    private Long activityId;

    /**
     * 积分过期时间
     */
    private LocalDateTime expiresAt;

    /**
     * 状态: 1-有效, 0-无效
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

    /**
     * 备注
     */
    private String remark;
}
