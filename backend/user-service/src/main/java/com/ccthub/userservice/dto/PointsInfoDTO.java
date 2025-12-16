package com.ccthub.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * 当前可用积分
     */
    private Integer availablePoints;

    /**
     * 累计获得积分
     */
    private Integer totalEarned;

    /**
     * 累计消耗积分
     */
    private Integer totalConsumed;

    /**
     * 即将过期积分（30天内）
     */
    private Integer expiringPoints;
}
