package com.ccthub.userservice.controller;

import com.ccthub.userservice.entity.Activity;
import com.ccthub.userservice.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    
    private final ActivityService activityService;
    
    /**
     * 创建活动
     */
    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        Activity created = activityService.createActivity(activity);
        return ResponseEntity.ok(created);
    }
    
    /**
     * 更新活动
     */
    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(
        @PathVariable Long id,
        @RequestBody Activity activity
    ) {
        Activity updated = activityService.updateActivity(id, activity);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * 审核活动
     */
    @PutMapping("/{id}/audit")
    public ResponseEntity<Activity> auditActivity(
        @PathVariable Long id,
        @RequestParam Integer auditStatus
    ) {
        Activity audited = activityService.auditActivity(id, auditStatus);
        return ResponseEntity.ok(audited);
    }
    
    /**
     * 修改活动状态（上下线）
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Activity> toggleStatus(
        @PathVariable Long id,
        @RequestParam Integer status
    ) {
        Activity updated = activityService.toggleStatus(id, status);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityDetail(@PathVariable Long id) {
        Activity activity = activityService.getActivityDetail(id);
        return ResponseEntity.ok(activity);
    }
    
    /**
     * 分页查询活动列表
     */
    @GetMapping
    public ResponseEntity<Page<Activity>> getActivityList(
        @RequestParam(required = false) Long merchantId,
        @RequestParam(required = false) Integer type,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) Integer auditStatus,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<Activity> activities = activityService.getActivityList(
            merchantId, type, status, auditStatus, keyword, page, size
        );
        return ResponseEntity.ok(activities);
    }
    
    /**
     * 查询进行中的活动
     */
    @GetMapping("/ongoing")
    public ResponseEntity<List<Activity>> getOngoingActivities() {
        List<Activity> activities = activityService.getOngoingActivities();
        return ResponseEntity.ok(activities);
    }
    
    /**
     * 查询热门活动
     */
    @GetMapping("/hot")
    public ResponseEntity<List<Activity>> getHotActivities(
        @RequestParam(defaultValue = "10") int limit
    ) {
        List<Activity> activities = activityService.getHotActivities(limit);
        return ResponseEntity.ok(activities);
    }
}
