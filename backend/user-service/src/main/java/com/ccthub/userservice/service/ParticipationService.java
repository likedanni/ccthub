package com.ccthub.userservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.entity.Activity;
import com.ccthub.userservice.entity.ActivityParticipation;
import com.ccthub.userservice.model.User;
import com.ccthub.userservice.repository.ActivityParticipationRepository;
import com.ccthub.userservice.repository.ActivityRepository;
import com.ccthub.userservice.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ActivityParticipationRepository participationRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * 参与活动
     */
    @Transactional
    public ActivityParticipation joinActivity(Long activityId, Long userId) {
        // 检查活动是否存在
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("活动不存在"));

        // 检查活动状态
        if (activity.getStatus() != 1) {
            throw new RuntimeException("活动未开始或已结束");
        }

        // 检查活动时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartsAt()) || now.isAfter(activity.getEndsAt())) {
            throw new RuntimeException("不在活动时间范围内");
        }

        // 检查是否已参与
        if (participationRepository.findByActivityIdAndUserId(activityId, userId).isPresent()) {
            throw new RuntimeException("您已参与过该活动");
        }

        // 检查参与资格
        checkEligibility(activity, userId);

        // 检查参与人数限制
        if (activity.getParticipationLimit() != null) {
            Long participantCount = participationRepository.countByActivityId(activityId);
            if (participantCount >= activity.getParticipationLimit()) {
                throw new RuntimeException("活动参与人数已满");
            }
        }

        // 创建参与记录
        ActivityParticipation participation = new ActivityParticipation();
        participation.setActivityId(activityId);
        participation.setUserId(userId);
        participation.setParticipationStatus(1); // 进行中
        participation.setRewardStatus(0); // 未发放

        // 初始化打卡进度（如果是打卡活动）
        if (activity.getType() == 1) {
            initCheckpointsProgress(participation);
        }

        return participationRepository.save(participation);
    }

    /**
     * 检查参与资格
     */
    private void checkEligibility(Activity activity, Long userId) {
        if (activity.getRequirementType() == null || activity.getRequirementType() == 1) {
            return; // 无要求
        }

        // 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (activity.getRequirementType() == 2) {
            // 等级要求 - 使用memberLevel
            Integer userLevel = user.getMemberLevel();
            if (userLevel == null || userLevel < activity.getRequirementValue()) {
                throw new RuntimeException("用户等级不足，无法参与");
            }
        } else if (activity.getRequirementType() == 3) {
            // 积分要求 - 使用growthValue作为积分
            Integer userPoints = user.getGrowthValue();
            if (userPoints == null || userPoints < activity.getRequirementValue()) {
                throw new RuntimeException("用户积分不足，无法参与");
            }
        }
    }

    /**
     * 初始化打卡进度
     */
    private void initCheckpointsProgress(ActivityParticipation participation) {
        ObjectNode progress = objectMapper.createObjectNode();
        progress.putArray("completed");
        participation.setCheckpointsProgress(progress.toString());
    }

    /**
     * 更新进度（打卡）
     */
    @Transactional
    public ActivityParticipation updateProgress(Long participationId, String checkpointId) {
        ActivityParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("参与记录不存在"));

        if (participation.getParticipationStatus() != 1) {
            throw new RuntimeException("活动已完成或已放弃");
        }

        try {
            // 解析打卡进度
            JsonNode progress = objectMapper.readTree(participation.getCheckpointsProgress());
            ObjectNode progressNode = (ObjectNode) progress;

            // 添加已完成的打卡点
            progressNode.withArray("completed").add(checkpointId);

            participation.setCheckpointsProgress(progressNode.toString());

            // 检查是否全部完成
            // 这里简化处理，实际应该从活动配置中获取总打卡点数量

            return participationRepository.save(participation);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("更新进度失败", e);
        }
    }

    /**
     * 完成活动
     */
    @Transactional
    public ActivityParticipation completeActivity(Long participationId) {
        ActivityParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("参与记录不存在"));

        participation.setParticipationStatus(2); // 已完成
        participation.setCompletedAt(LocalDateTime.now());

        return participationRepository.save(participation);
    }

    /**
     * 放弃活动
     */
    @Transactional
    public ActivityParticipation abandonActivity(Long participationId) {
        ActivityParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("参与记录不存在"));

        if (participation.getParticipationStatus() != 1) {
            throw new RuntimeException("活动已完成或已放弃");
        }

        participation.setParticipationStatus(3); // 已放弃

        return participationRepository.save(participation);
    }

    /**
     * 发放奖励
     */
    @Transactional
    public void grantReward(Long participationId) {
        ActivityParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("参与记录不存在"));

        if (participation.getParticipationStatus() != 2) {
            throw new RuntimeException("活动未完成");
        }

        if (participation.getRewardStatus() == 1) {
            throw new RuntimeException("奖励已发放");
        }

        Activity activity = activityRepository.findById(participation.getActivityId())
                .orElseThrow(() -> new RuntimeException("活动不存在"));

        try {
            // 解析奖励配置
            JsonNode rewardConfig = objectMapper.readTree(activity.getRewardConfig());
            String rewardType = rewardConfig.get("type").asText();
            int rewardValue = rewardConfig.get("value").asInt();

            // 发放奖励（这里应该调用积分服务）
            if ("points".equals(rewardType)) {
                // TODO: 调用积分服务增加积分
                User user = userRepository.findById(participation.getUserId())
                        .orElseThrow(() -> new RuntimeException("用户不存在"));
                // 增加成长值作为积分
                user.setGrowthValue((user.getGrowthValue() == null ? 0 : user.getGrowthValue()) + rewardValue);
                userRepository.save(user);
            }

            // 记录奖励详情
            ObjectNode rewardDetail = objectMapper.createObjectNode();
            rewardDetail.put("type", rewardType);
            rewardDetail.put("value", rewardValue);
            rewardDetail.put("granted_at", LocalDateTime.now().toString());

            participation.setRewardDetail(rewardDetail.toString());
            participation.setRewardStatus(1); // 已发放

            participationRepository.save(participation);
        } catch (Exception e) {
            participation.setRewardStatus(2); // 发放失败
            participationRepository.save(participation);
            throw new RuntimeException("奖励发放失败", e);
        }
    }

    /**
     * 查询用户的参与记录
     */
    public Page<ActivityParticipation> getMyParticipations(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "joinedAt"));
        return participationRepository.findByUserId(userId, pageable);
    }

    /**
     * 查询活动的参与记录
     */
    public Page<ActivityParticipation> getActivityParticipations(Long activityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "joinedAt"));
        return participationRepository.findByActivityId(activityId, pageable);
    }

    /**
     * 查询用户在进行中的活动
     */
    public List<ActivityParticipation> getOngoingParticipations(Long userId) {
        return participationRepository.findOngoingByUserId(userId);
    }

    /**
     * 统计活动参与人数
     */
    public Long countParticipants(Long activityId) {
        return participationRepository.countByActivityId(activityId);
    }

    /**
     * 统计活动完成人数
     */
    public Long countCompletedParticipants(Long activityId) {
        return participationRepository.countByActivityIdAndParticipationStatus(activityId, 2);
    }
}
