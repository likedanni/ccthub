package com.ccthub.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户积分信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsInfoDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 当前可用积分
     */
    private Integer availablePoints;

    /**
     * 当前余额（最新流水记录的余额）
     */
    private Integer currentBalance;

    /**
     * 累计获得积分
     */
    private Integer totalEarned;

    /**
     * 累计消耗积分
     */
    private Integer totalSpent;

    /**
     * 累计消耗积分（别名）
     */
    private Integer totalConsumed;

    /**
     * 即将过期积分（30天内）
     */
    private Integer expiringPoints;

    /**
     * 最近获得积分时间
     */
    private LocalDateTime lastEarnTime;
}
