package com.ccthub.userservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动实体
 * 对应数据库表: activities
 */
@Data
@Entity
@Table(name = "activities")
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    /**
     * 活动类型:
     * 1 - 打卡任务
     * 2 - 积分奖励
     * 3 - 主题促销
     */
    @Column(nullable = false)
    private Integer type;
    
    @Column(name = "cover_image", length = 500)
    private String coverImage;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "starts_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startsAt;
    
    @Column(name = "ends_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endsAt;
    
    @Column(length = 255)
    private String location;
    
    @Column(name = "participation_limit")
    private Integer participationLimit;
    
    /**
     * 参与要求类型:
     * 1 - 无要求
     * 2 - 等级要求
     * 3 - 积分要求
     */
    @Column(name = "requirement_type")
    private Integer requirementType;
    
    @Column(name = "requirement_value")
    private Integer requirementValue;
    
    /**
     * 奖励配置 (JSON格式)
     * 示例: {"type": "points", "value": 100, "description": "完成打卡获得100积分"}
     */
    @Column(name = "reward_config", columnDefinition = "JSON", nullable = false)
    private String rewardConfig;
    
    /**
     * 审核状态:
     * 0 - 待审核
     * 1 - 审核通过
     * 2 - 审核拒绝
     */
    @Column(name = "audit_status")
    private Integer auditStatus = 0;
    
    /**
     * 活动状态:
     * 0 - 未开始
     * 1 - 进行中
     * 2 - 已结束
     * 3 - 已取消
     */
    @Column(nullable = false)
    private Integer status = 0;
    
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
