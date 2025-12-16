package com.ccthub.userservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.PointsInfoDTO;
import com.ccthub.userservice.dto.UserPointsDTO;
import com.ccthub.userservice.entity.UserPoints;
import com.ccthub.userservice.repository.UserPointsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 积分服务 - 积分规则引擎
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsService {

    private final UserPointsRepository pointsRepository;

    /**
     * 积分获取规则配置
     */
    private static final BigDecimal POINTS_PER_YUAN = BigDecimal.ONE; // 1元=1积分
    private static final Integer POINTS_DEDUCT_RATIO = 100; // 100积分=1元
    private static final Integer DAILY_CHECKIN_POINTS = 10; // 每日签到积分
    private static final Integer SHARE_POINTS = 5; // 分享获得积分
    private static final Integer INVITE_POINTS = 50; // 邀请好友积分
    private static final Integer POINTS_EXPIRE_DAYS = 365; // 积分有效期（天）

    /**
     * 获取用户积分信息
     */
    public PointsInfoDTO getPointsInfo(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // 查询当前可用积分
        Integer availablePoints = pointsRepository.getTotalValidPoints(userId, now);

        // 查询即将过期积分（30天内）
        LocalDateTime expireTime = now.plusDays(30);
        List<UserPoints> expiringList = pointsRepository.findExpiringPoints(userId, now, expireTime);
        Integer expiringPoints = expiringList.stream()
                .filter(p -> p.getChangeType().equals(UserPoints.ChangeType.INCREASE))
                .mapToInt(UserPoints::getPoints)
                .sum();

        // 查询累计获得和消耗
        List<UserPoints> allPoints = pointsRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .getContent();
        Integer totalEarned = allPoints.stream()
                .filter(p -> p.getChangeType().equals(UserPoints.ChangeType.INCREASE))
                .mapToInt(UserPoints::getPoints)
                .sum();
        Integer totalConsumed = allPoints.stream()
                .filter(p -> p.getChangeType().equals(UserPoints.ChangeType.DECREASE))
                .mapToInt(p -> Math.abs(p.getPoints()))
                .sum();

        return PointsInfoDTO.builder()
                .userId(userId)
                .availablePoints(availablePoints)
                .totalEarned(totalEarned)
                .totalConsumed(totalConsumed)
                .expiringPoints(expiringPoints)
                .build();
    }

    /**
     * 订单支付获取积分
     * 按消费金额比例（1元=1积分）
     */
    @Transactional
    public void earnPointsFromOrder(Long userId, String orderNo, BigDecimal orderAmount) {
        // 计算积分：1元=1积分
        int points = orderAmount.multiply(POINTS_PER_YUAN)
                .setScale(0, RoundingMode.DOWN)
                .intValue();

        if (points <= 0) {
            return;
        }

        // 计算积分过期时间（1年后）
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(POINTS_EXPIRE_DAYS);

        // 获取当前积分余额
        Integer currentBalance = getCurrentBalance(userId);

        // 创建积分流水
        UserPoints userPoints = UserPoints.builder()
                .userId(userId)
                .changeType(UserPoints.ChangeType.INCREASE)
                .source(UserPoints.Source.ORDER_PAY)
                .points(points)
                .currentBalance(currentBalance + points)
                .orderNo(orderNo)
                .expiresAt(expiresAt)
                .status(UserPoints.Status.VALID)
                .remark(String.format("订单消费获得%d积分", points))
                .build();

        pointsRepository.save(userPoints);
        log.info("用户{}订单{}消费获得积分: {}，当前余额: {}", userId, orderNo, points, currentBalance + points);
    }

    /**
     * 每日签到获取积分
     */
    @Transactional
    public void earnPointsFromCheckin(Long userId) {
        // TODO: 检查今天是否已签到

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(POINTS_EXPIRE_DAYS);
        Integer currentBalance = getCurrentBalance(userId);

        UserPoints userPoints = UserPoints.builder()
                .userId(userId)
                .changeType(UserPoints.ChangeType.INCREASE)
                .source(UserPoints.Source.DAILY_CHECKIN)
                .points(DAILY_CHECKIN_POINTS)
                .currentBalance(currentBalance + DAILY_CHECKIN_POINTS)
                .expiresAt(expiresAt)
                .status(UserPoints.Status.VALID)
                .remark("每日签到获得积分")
                .build();

        pointsRepository.save(userPoints);
        log.info("用户{}每日签到获得积分: {}，当前余额: {}", userId, DAILY_CHECKIN_POINTS, currentBalance + DAILY_CHECKIN_POINTS);
    }

    /**
     * 分享获取积分
     */
    @Transactional
    public void earnPointsFromShare(Long userId) {
        // TODO: 检查今天分享次数限制

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(POINTS_EXPIRE_DAYS);
        Integer currentBalance = getCurrentBalance(userId);

        UserPoints userPoints = UserPoints.builder()
                .userId(userId)
                .changeType(UserPoints.ChangeType.INCREASE)
                .source(UserPoints.Source.SHARE)
                .points(SHARE_POINTS)
                .currentBalance(currentBalance + SHARE_POINTS)
                .expiresAt(expiresAt)
                .status(UserPoints.Status.VALID)
                .remark("分享获得积分")
                .build();

        pointsRepository.save(userPoints);
        log.info("用户{}分享获得积分: {}，当前余额: {}", userId, SHARE_POINTS, currentBalance + SHARE_POINTS);
    }

    /**
     * 邀请好友获取积分
     */
    @Transactional
    public void earnPointsFromInvite(Long userId, Long invitedUserId) {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(POINTS_EXPIRE_DAYS);
        Integer currentBalance = getCurrentBalance(userId);

        UserPoints userPoints = UserPoints.builder()
                .userId(userId)
                .changeType(UserPoints.ChangeType.INCREASE)
                .source(UserPoints.Source.INVITE)
                .points(INVITE_POINTS)
                .currentBalance(currentBalance + INVITE_POINTS)
                .expiresAt(expiresAt)
                .status(UserPoints.Status.VALID)
                .remark(String.format("邀请用户%d获得积分", invitedUserId))
                .build();

        pointsRepository.save(userPoints);
        log.info("用户{}邀请用户{}获得积分: {}，当前余额: {}",
                userId, invitedUserId, INVITE_POINTS, currentBalance + INVITE_POINTS);
    }

    /**
     * 积分抵扣
     * 100积分=1元
     */
    @Transactional
    public void deductPoints(Long userId, String orderNo, Integer points) {
        if (points <= 0) {
            throw new RuntimeException("抵扣积分必须大于0");
        }

        // 检查积分余额
        Integer currentBalance = getCurrentBalance(userId);
        if (currentBalance < points) {
            throw new RuntimeException("积分不足");
        }

        // 创建积分扣减流水
        UserPoints userPoints = UserPoints.builder()
                .userId(userId)
                .changeType(UserPoints.ChangeType.DECREASE)
                .source(UserPoints.Source.DEDUCT)
                .points(-points)
                .currentBalance(currentBalance - points)
                .orderNo(orderNo)
                .status(UserPoints.Status.VALID)
                .remark(String.format("订单抵扣%d积分", points))
                .build();

        pointsRepository.save(userPoints);
        log.info("用户{}订单{}抵扣积分: {}，当前余额: {}", userId, orderNo, points, currentBalance - points);
    }

    /**
     * 积分兑换商品
     */
    @Transactional
    public void exchangePoints(Long userId, Long productId, Integer points) {
        if (points <= 0) {
            throw new RuntimeException("兑换积分必须大于0");
        }

        // 检查积分余额
        Integer currentBalance = getCurrentBalance(userId);
        if (currentBalance < points) {
            throw new RuntimeException("积分不足");
        }

        // 创建积分扣减流水
        UserPoints userPoints = UserPoints.builder()
                .userId(userId)
                .changeType(UserPoints.ChangeType.DECREASE)
                .source(UserPoints.Source.EXCHANGE)
                .points(-points)
                .currentBalance(currentBalance - points)
                .activityId(productId)
                .status(UserPoints.Status.VALID)
                .remark(String.format("兑换商品%d消耗%d积分", productId, points))
                .build();

        pointsRepository.save(userPoints);
        log.info("用户{}兑换商品{}消耗积分: {}，当前余额: {}", userId, productId, points, currentBalance - points);
    }

    /**
     * 查询积分流水
     */
    public Page<UserPointsDTO> getPointsHistory(Long userId, String source,
            LocalDateTime startTime, LocalDateTime endTime,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserPoints> points;

        if (source != null && startTime != null && endTime != null) {
            points = pointsRepository.findByUserIdAndDateRange(userId, startTime, endTime, pageable);
        } else if (source != null) {
            points = pointsRepository.findByUserIdAndSourceOrderByCreatedAtDesc(userId, source, pageable);
        } else if (startTime != null && endTime != null) {
            points = pointsRepository.findByUserIdAndDateRange(userId, startTime, endTime, pageable);
        } else {
            points = pointsRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        return points.map(this::toDTO);
    }

    /**
     * 定时任务：处理过期积分
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void processExpiredPoints() {
        log.info("开始处理过期积分...");
        LocalDateTime now = LocalDateTime.now();
        List<UserPoints> expiredPoints = pointsRepository.findExpiredPoints(now);

        for (UserPoints points : expiredPoints) {
            // 标记为无效
            points.setStatus(UserPoints.Status.INVALID);
            pointsRepository.save(points);

            // 创建过期扣减流水
            Integer currentBalance = getCurrentBalance(points.getUserId());
            UserPoints expireRecord = UserPoints.builder()
                    .userId(points.getUserId())
                    .changeType(UserPoints.ChangeType.DECREASE)
                    .source(UserPoints.Source.EXPIRE)
                    .points(-points.getPoints())
                    .currentBalance(currentBalance - points.getPoints())
                    .status(UserPoints.Status.VALID)
                    .remark(String.format("积分过期，扣除%d积分", points.getPoints()))
                    .build();

            pointsRepository.save(expireRecord);
            log.info("处理过期积分: 用户={}, 积分={}", points.getUserId(), points.getPoints());
        }

        log.info("过期积分处理完成，共处理{}条记录", expiredPoints.size());
    }

    /**
     * 计算积分可抵扣金额
     */
    public BigDecimal calculateDeductAmount(Integer points) {
        return BigDecimal.valueOf(points)
                .divide(BigDecimal.valueOf(POINTS_DEDUCT_RATIO), 2, RoundingMode.DOWN);
    }

    /**
     * 计算金额可获得积分
     */
    public Integer calculateEarnPoints(BigDecimal amount) {
        return amount.multiply(POINTS_PER_YUAN)
                .setScale(0, RoundingMode.DOWN)
                .intValue();
    }

    /**
     * 获取用户当前积分余额
     */
    private Integer getCurrentBalance(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return pointsRepository.getTotalValidPoints(userId, now);
    }

    /**
     * 转换为DTO
     */
    private UserPointsDTO toDTO(UserPoints points) {
        return UserPointsDTO.builder()
                .id(points.getId())
                .userId(points.getUserId())
                .changeType(points.getChangeType())
                .changeTypeDesc(getChangeTypeDesc(points.getChangeType()))
                .source(points.getSource())
                .sourceDesc(getSourceDesc(points.getSource()))
                .points(points.getPoints())
                .currentBalance(points.getCurrentBalance())
                .orderNo(points.getOrderNo())
                .activityId(points.getActivityId())
                .expiresAt(points.getExpiresAt())
                .status(points.getStatus())
                .statusDesc(getStatusDesc(points.getStatus()))
                .createdAt(points.getCreatedAt())
                .remark(points.getRemark())
                .build();
    }

    /**
     * 获取变动类型描述
     */
    private String getChangeTypeDesc(Integer changeType) {
        if (changeType == null)
            return "未知";
        return switch (changeType) {
            case 1 -> "增加";
            case 2 -> "减少";
            default -> "未知";
        };
    }

    /**
     * 获取来源描述
     */
    private String getSourceDesc(String source) {
        if (source == null)
            return "未知";
        return switch (source) {
            case "order_pay" -> "订单消费";
            case "daily_checkin" -> "每日签到";
            case "invite" -> "邀请好友";
            case "exchange" -> "积分兑换";
            case "activity" -> "活动奖励";
            case "share" -> "分享获得";
            case "deduct" -> "积分抵扣";
            case "expire" -> "积分过期";
            default -> source;
        };
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null)
            return "未知";
        return switch (status) {
            case 0 -> "无效";
            case 1 -> "有效";
            default -> "未知";
        };
    }
}
