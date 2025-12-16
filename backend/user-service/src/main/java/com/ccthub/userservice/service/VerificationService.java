package com.ccthub.userservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.entity.OrderItem;
import com.ccthub.userservice.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 核销服务（已迁移到新订单系统）
 * 
 * @author CCTHub
 * @date 2025-12-16
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
        result.put("orderNo", item.getOrderNo());
        result.put("productName", item.getProductName());
        result.put("visitorName", item.getVisitorName());
        result.put("unitPrice", item.getUnitPrice());
        result.put("verificationStatus", item.getVerificationStatus());
        result.put("verificationStatusText", getVerificationStatusText(item.getVerificationStatus()));
        result.put("ticketDate", item.getTicketDate());

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

            // 检查核销状态
            if (OrderItem.VerificationStatus.VERIFIED.equals(item.getVerificationStatus())) {
                throw new RuntimeException("该票券已核销");
            }

            if (OrderItem.VerificationStatus.EXPIRED.equals(item.getVerificationStatus())) {
                throw new RuntimeException("该票券已过期");
            }

            // 更新核销状态
            item.setVerificationStatus(OrderItem.VerificationStatus.VERIFIED);
            orderItemRepository.save(item);

            log.info("核销成功，verificationCode={}, visitorName={}", verificationCode, item.getVisitorName());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "核销成功");
            result.put("visitorName", item.getVisitorName());
            result.put("productName", item.getProductName());
            result.put("ticketDate", item.getTicketDate());

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
    public Map<String, Object> batchVerify(List<String> verificationCodes, Long staffId) {
        log.info("开始批量核销，数量={}, staffId={}", verificationCodes.size(), staffId);

        int successCount = 0;
        int failCount = 0;
        Map<String, String> failReasons = new HashMap<>();

        for (String code : verificationCodes) {
            try {
                verifyTicket(code, staffId);
                successCount++;
            } catch (Exception e) {
                failCount++;
                failReasons.put(code, e.getMessage());
                log.warn("核销失败，verificationCode={}, reason={}", code, e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", verificationCodes.size());
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failReasons", failReasons);

        log.info("批量核销完成，total={}, success={}, fail={}", verificationCodes.size(), successCount, failCount);
        return result;
    }

    /**
     * 查询订单核销统计
     */
    public Map<String, Object> getOrderVerificationStats(String orderNo) {
        List<OrderItem> items = orderItemRepository.findByOrderNo(orderNo);

        long totalCount = items.size();
        long verifiedCount = items.stream()
                .filter(item -> OrderItem.VerificationStatus.VERIFIED.equals(item.getVerificationStatus()))
                .count();
        long notVerifiedCount = items.stream()
                .filter(item -> OrderItem.VerificationStatus.NOT_VERIFIED.equals(item.getVerificationStatus()))
                .count();
        long expiredCount = items.stream()
                .filter(item -> OrderItem.VerificationStatus.EXPIRED.equals(item.getVerificationStatus()))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", totalCount);
        stats.put("verifiedCount", verifiedCount);
        stats.put("notVerifiedCount", notVerifiedCount);
        stats.put("expiredCount", expiredCount);
        stats.put("verificationRate", totalCount > 0 ? (double) verifiedCount / totalCount : 0.0);

        return stats;
    }

    /**
     * 查询核销记录列表
     */
    public List<Map<String, Object>> getVerificationRecords(String orderNo) {
        List<OrderItem> items = orderItemRepository.findByOrderNo(orderNo);

        return items.stream()
                .map(item -> {
                    Map<String, Object> record = new HashMap<>();
                    record.put("id", item.getId());
                    record.put("verificationCode", item.getVerificationCode());
                    record.put("productName", item.getProductName());
                    record.put("visitorName", item.getVisitorName());
                    record.put("verificationStatus", item.getVerificationStatus());
                    record.put("verificationStatusText", getVerificationStatusText(item.getVerificationStatus()));
                    record.put("ticketDate", item.getTicketDate());
                    return record;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取核销状态文本
     */
    private String getVerificationStatusText(Integer status) {
        if (status == null)
            return "未知";
        switch (status) {
            case 0:
                return "未核销";
            case 1:
                return "已核销";
            case 2:
                return "已过期";
            default:
                return "未知";
        }
    }
}
