package com.ccthub.userservice.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.entity.OrderItem;
import com.ccthub.userservice.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;

/**
 * 核销服务
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final OrderItemRepository orderItemRepository;

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
     * 核销电子票券
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> verifyTicket(String verificationCode, Long staffId) {
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

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "核销成功");
        result.put("visitorName", item.getVisitorName());
        result.put("verifyTime", item.getVerifyTime());

        return result;
    }

    /**
     * 批量核销
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchVerify(Long orderId, Long staffId) {
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

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "批量核销成功");
        result.put("count", items.size());

        return result;
    }
}
