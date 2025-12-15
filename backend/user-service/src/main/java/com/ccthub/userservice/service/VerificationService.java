package com.ccthub.userservice.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.entity.OrderItem;
import com.ccthub.userservice.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 核销服务
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final OrderItemRepository orderItemRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String VERIFICATION_LOCK_PREFIX = "verification:lock:";
    private static final long LOCK_EXPIRE_SECONDS = 30; // 防重复锁过期时间

    /**
     * 查询核销码信息
     */
    public Map<String, Object> getVerificationInfo(String verificationCode) {
        OrderItem item = orderItemRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new RuntimeException("核销码不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", item.getId());
        result.put("orderId", item.getOrderId());
        result.put("visitorName", item.getVisitorName());
        result.put("visitorIdCard", item.getVisitorIdCard());
        result.put("price", item.getPrice());
        result.put("isVerified", item.getIsVerified());
        result.put("verifyTime", item.getVerifyTime());
        result.put("createTime", item.getCreateTime());

        return result;
    }

    /**
     * 核销电子票券（增加Redis防重复）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> verifyTicket(String verificationCode, Long staffId) {
        log.info("开始核销票券，verificationCode={}, staffId={}", verificationCode, staffId);

        // 1. 防重复核销检查（使用Redis分布式锁）
        String lockKey = VERIFICATION_LOCK_PREFIX + verificationCode;
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(
                lockKey, "1", LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(lockAcquired)) {
            log.warn("票券正在核销中，请勿重复操作，verificationCode={}", verificationCode);
            throw new RuntimeException("票券正在核销中，请勿重复操作");
        }

        try {
            OrderItem item = orderItemRepository.findByVerificationCode(verificationCode)
                    .orElseThrow(() -> new RuntimeException("核销码不存在"));

            if (item.getIsVerified()) {
                throw new RuntimeException("该票券已核销，核销时间：" + item.getVerifyTime());
            }

            // 更新核销状态
            item.setIsVerified(true);
            item.setVerifyTime(LocalDateTime.now());
            item.setVerifyStaffId(staffId);
            orderItemRepository.save(item);

            log.info("核销成功，verificationCode={}, visitorName={}", verificationCode, item.getVisitorName());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "核销成功");
            result.put("visitorName", item.getVisitorName());
            result.put("verifyTime", item.getVerifyTime());

            return result;
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 批量核销
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchVerify(Long orderId, Long staffId) {
        log.info("开始批量核销，orderId={}, staffId={}", orderId, staffId);

        // 防重复核销检查
        String lockKey = VERIFICATION_LOCK_PREFIX + "order:" + orderId;
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(
                lockKey, "1", LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(lockAcquired)) {
            log.warn("订单正在批量核销中，请勿重复操作，orderId={}", orderId);
            throw new RuntimeException("订单正在批量核销中，请勿重复操作");
        }

        try {
            var items = orderItemRepository.findByOrderIdAndIsVerified(orderId, false);

            if (items.isEmpty()) {
                throw new RuntimeException("该订单所有票券已核销");
            }

            for (OrderItem item : items) {
                item.setIsVerified(true);
                item.setVerifyTime(LocalDateTime.now());
                item.setVerifyStaffId(staffId);
            }

            orderItemRepository.saveAll(items);

            log.info("批量核销成功，orderId={}, count={}", orderId, items.size());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "批量核销成功");
            result.put("count", items.size());

            return result;
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 获取核销统计
     */
    public Map<String, Object> getStatistics(Long staffId) {
        log.info("查询核销统计，staffId={}", staffId);

        // 总核销数
        long totalCount = orderItemRepository.countByVerifyStaffIdAndIsVerified(staffId, true);

        // 今日核销数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todayCount = orderItemRepository.countByVerifyStaffIdAndIsVerifiedAndVerifyTimeAfter(
                staffId, true, todayStart);

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("todayCount", todayCount);
        result.put("staffId", staffId);

        return result;
    }
}
