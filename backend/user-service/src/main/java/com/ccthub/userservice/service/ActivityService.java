package com.ccthub.userservice.service;

import com.ccthub.userservice.entity.Activity;
import com.ccthub.userservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {
    
    private final ActivityRepository activityRepository;
    
    /**
     * 创建活动
     */
    @Transactional
    public Activity createActivity(Activity activity) {
        // 设置默认状态
        if (activity.getAuditStatus() == null) {
            activity.setAuditStatus(0); // 待审核
        }
        if (activity.getStatus() == null) {
            activity.setStatus(0); // 未开始
        }
        return activityRepository.save(activity);
    }
    
    /**
     * 更新活动
     */
    @Transactional
    public Activity updateActivity(Long id, Activity activity) {
        Activity existing = activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));
        
        // 只允许更新部分字段
        if (activity.getName() != null) {
            existing.setName(activity.getName());
        }
        if (activity.getCoverImage() != null) {
            existing.setCoverImage(activity.getCoverImage());
        }
        if (activity.getDescription() != null) {
            existing.setDescription(activity.getDescription());
        }
        if (activity.getStartsAt() != null) {
            existing.setStartsAt(activity.getStartsAt());
        }
        if (activity.getEndsAt() != null) {
            existing.setEndsAt(activity.getEndsAt());
        }
        if (activity.getLocation() != null) {
            existing.setLocation(activity.getLocation());
        }
        if (activity.getParticipationLimit() != null) {
            existing.setParticipationLimit(activity.getParticipationLimit());
        }
        if (activity.getRewardConfig() != null) {
            existing.setRewardConfig(activity.getRewardConfig());
        }
        
        return activityRepository.save(existing);
    }
    
    /**
     * 审核活动
     */
    @Transactional
    public Activity auditActivity(Long id, Integer auditStatus) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));
        
        if (auditStatus < 0 || auditStatus > 2) {
            throw new RuntimeException("审核状态无效");
        }
        
        activity.setAuditStatus(auditStatus);
        return activityRepository.save(activity);
    }
    
    /**
     * 修改活动状态（上下线）
     */
    @Transactional
    public Activity toggleStatus(Long id, Integer status) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));
        
        // 检查审核状态
        if (status == 1 && activity.getAuditStatus() != 1) {
            throw new RuntimeException("活动未通过审核，无法上线");
        }
        
        activity.setStatus(status);
        return activityRepository.save(activity);
    }
    
    /**
     * 删除活动
     */
    @Transactional
    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));
        
        // 只允许删除未开始或已取消的活动
        if (activity.getStatus() == 1) {
            throw new RuntimeException("活动进行中，无法删除");
        }
        
        activityRepository.deleteById(id);
    }
    
    /**
     * 获取活动详情
     */
    public Activity getActivityDetail(Long id) {
        return activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));
    }
    
    /**
     * 分页查询活动列表
     */
    public Page<Activity> getActivityList(
        Long merchantId,
        Integer type,
        Integer status,
        Integer auditStatus,
        String keyword,
        int page,
        int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return activityRepository.findByConditions(merchantId, type, status, auditStatus, keyword, pageable);
    }
    
    /**
     * 查询进行中的活动
     */
    public List<Activity> getOngoingActivities() {
        return activityRepository.findOngoingActivities(LocalDateTime.now());
    }
    
    /**
     * 查询热门活动
     */
    public List<Activity> getHotActivities(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return activityRepository.findHotActivities(pageable);
    }
    
    /**
     * 根据商户ID查询活动
     */
    public Page<Activity> getActivitiesByMerchant(Long merchantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return activityRepository.findByMerchantId(merchantId, pageable);
    }
}
