package com.ccthub.userservice.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

/**
 * 活动参与记录实体
 * 对应数据库表: activity_participations
 */
@Data
@Entity
@Table(name = "activity_participations", uniqueConstraints = @UniqueConstraint(columnNames = { "activity_id",
        "user_id" }))
public class ActivityParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 参与状态:
     * 1 - 进行中
     * 2 - 已完成
     * 3 - 已放弃
     */
    @Column(name = "participation_status")
    private Integer participationStatus = 1;

    /**
     * 奖励发放状态:
     * 0 - 未发放
     * 1 - 已发放
     * 2 - 发放失败
     */
    @Column(name = "reward_status")
    private Integer rewardStatus = 0;

    /**
     * 奖励详情 (JSON格式)
     * 示例: {"points": 100, "granted_at": "2025-12-16 10:00:00"}
     */
    @Column(name = "reward_detail", columnDefinition = "JSON")
    private String rewardDetail;

    /**
     * 打卡点完成进度 (JSON格式)
     * 示例: {"checkpoints": [{"id": 1, "name": "景区入口", "completed": true,
     * "completed_at": "2025-12-16 10:00:00"}]}
     */
    @Column(name = "checkpoints_progress", columnDefinition = "JSON")
    private String checkpointsProgress;

    @Column(name = "joined_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinedAt;

    @Column(name = "completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
