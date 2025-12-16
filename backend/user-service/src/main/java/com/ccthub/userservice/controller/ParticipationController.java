package com.ccthub.userservice.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.entity.ActivityParticipation;
import com.ccthub.userservice.service.ParticipationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 活动参与管理控制器
 * 提供用户参与活动、打卡、完成、放弃和奖励发放等功能
 */
@Tag(name = "活动参与管理", description = "用户参与活动相关接口，包括参与活动、更新进度、完成活动、放弃活动和奖励发放等功能")
@RestController
@RequestMapping("/api/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * 参与活动
     */
    @PostMapping
    public ResponseEntity<ActivityParticipation> joinActivity(
            @RequestParam Long activityId,
            @RequestParam Long userId) {
        ActivityParticipation participation = participationService.joinActivity(activityId, userId);
        return ResponseEntity.ok(participation);
    }

    /**
     * 更新进度（打卡）
     */
    @PutMapping("/{id}/progress")
    public ResponseEntity<ActivityParticipation> updateProgress(
            @PathVariable Long id,
            @RequestParam String checkpointId) {
        ActivityParticipation participation = participationService.updateProgress(id, checkpointId);
        return ResponseEntity.ok(participation);
    }

    /**
     * 完成活动
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<ActivityParticipation> completeActivity(@PathVariable Long id) {
        ActivityParticipation participation = participationService.completeActivity(id);
        return ResponseEntity.ok(participation);
    }

    /**
     * 放弃活动
     */
    @PutMapping("/{id}/abandon")
    public ResponseEntity<ActivityParticipation> abandonActivity(@PathVariable Long id) {
        ActivityParticipation participation = participationService.abandonActivity(id);
        return ResponseEntity.ok(participation);
    }

    /**
     * 发放奖励
     */
    @PostMapping("/{id}/grant-reward")
    public ResponseEntity<Void> grantReward(@PathVariable Long id) {
        participationService.grantReward(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 查询用户的参与记录
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ActivityParticipation>> getMyParticipations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ActivityParticipation> participations = participationService.getMyParticipations(userId, page, size);
        return ResponseEntity.ok(participations);
    }

    /**
     * 查询活动的参与记录
     */
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Page<ActivityParticipation>> getActivityParticipations(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ActivityParticipation> participations = participationService.getActivityParticipations(activityId, page,
                size);
        return ResponseEntity.ok(participations);
    }

    /**
     * 查询用户在进行中的活动
     */
    @GetMapping("/user/{userId}/ongoing")
    public ResponseEntity<List<ActivityParticipation>> getOngoingParticipations(@PathVariable Long userId) {
        List<ActivityParticipation> participations = participationService.getOngoingParticipations(userId);
        return ResponseEntity.ok(participations);
    }

    /**
     * 统计活动参与人数
     */
    @GetMapping("/activity/{activityId}/count")
    public ResponseEntity<Long> countParticipants(@PathVariable Long activityId) {
        Long count = participationService.countParticipants(activityId);
        return ResponseEntity.ok(count);
    }
}
