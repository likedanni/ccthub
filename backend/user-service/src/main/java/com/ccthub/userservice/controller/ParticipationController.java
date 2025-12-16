package com.ccthub.userservice.controller;

import com.ccthub.userservice.entity.ActivityParticipation;
import com.ccthub.userservice.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        @RequestParam Long userId
    ) {
        ActivityParticipation participation = participationService.joinActivity(activityId, userId);
        return ResponseEntity.ok(participation);
    }
    
    /**
     * 更新进度（打卡）
     */
    @PutMapping("/{id}/progress")
    public ResponseEntity<ActivityParticipation> updateProgress(
        @PathVariable Long id,
        @RequestParam String checkpointId
    ) {
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
        @RequestParam(defaultValue = "10") int size
    ) {
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
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<ActivityParticipation> participations = participationService.getActivityParticipations(activityId, page, size);
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
