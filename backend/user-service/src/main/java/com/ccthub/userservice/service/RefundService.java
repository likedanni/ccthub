package com.ccthub.userservice.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.payment.RefundAuditRequest;
import com.ccthub.userservice.dto.payment.RefundRequest;
import com.ccthub.userservice.dto.payment.RefundResponse;
import com.ccthub.userservice.entity.Order;
import com.ccthub.userservice.entity.OrderRefund;
import com.ccthub.userservice.repository.OrderRefundRepository;
import com.ccthub.userservice.repository.OrderRepository;
import com.ccthub.userservice.service.RefundPolicyService.RefundCalculationResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 退款服务（已迁移到新订单系统）
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final OrderRepository orderRepository;
    private final OrderRefundRepository refundRepository;
    private final RefundPolicyService refundPolicyService;

    /**
     * 创建退款申请
     */
    @Transactional
    public RefundResponse createRefund(RefundRequest request) {
        // 查询订单
        Order order = orderRepository.findByOrderNo(request.getOrderNo())
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        // 检查订单状态
        if (order.getPaymentStatus() != Order.PaymentStatus.SUCCESS) {
            throw new IllegalStateException("只有已支付订单才能退款");
        }

        if (order.getOrderStatus() == Order.OrderStatus.CANCELLED
                || order.getOrderStatus() == Order.OrderStatus.REFUNDING
                || order.getOrderStatus() == Order.OrderStatus.COMPLETED) {
            throw new IllegalStateException("订单状态不允许退款");
        }

        // 检查是否已有未完成的退款申请
        long pendingCount = refundRepository.countByOrderNoAndStatus(request.getOrderNo(), OrderRefund.STATUS_PENDING_AUDIT);
        if (pendingCount > 0) {
            throw new IllegalStateException("已存在待审核的退款申请");
        }

        // 计算退款金额
        RefundCalculationResult calculation;
        if (request.getRefundType().equals(OrderRefund.TYPE_FULL)) {
            calculation = refundPolicyService.calculateRefund(request.getOrderNo());
        } else {
            calculation = refundPolicyService.calculatePartialRefund(request.getOrderNo(), request.getRefundQuantity());
        }

        if (!calculation.isCanRefund()) {
            throw new IllegalStateException(calculation.getReason());
        }

        // 创建退款申请
        OrderRefund refund = new OrderRefund();
        refund.setRefundNo("R" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        refund.setOrderNo(request.getOrderNo());
        refund.setUserId(request.getUserId());
        refund.setRefundType(request.getRefundType());
        refund.setRefundAmount(calculation.getActualRefund());
        refund.setRefundFee(calculation.getRefundFee());
        refund.setRefundReason(request.getRefundReason());
        refund.setRefundEvidence(request.getRefundEvidence());
        refund.setStatus(OrderRefund.STATUS_PENDING_AUDIT);

        OrderRefund saved = refundRepository.save(refund);

        // 更新订单状态为退款中
        order.setOrderStatus(Order.OrderStatus.REFUNDING);
        orderRepository.save(order);

        log.info("退款申请创建成功，refundNo={}, orderNo={}, refundAmount={}",
                saved.getRefundNo(), request.getOrderNo(), calculation.getActualRefund());

        return convertToResponse(saved);
    }

    /**
     * 审核退款申请
     */
    @Transactional
    public RefundResponse auditRefund(RefundAuditRequest request) {
        OrderRefund refund = refundRepository.findByRefundNo(request.getRefundNo())
                .orElseThrow(() -> new IllegalArgumentException("退款申请不存在"));

        if (!refund.getStatus().equals(OrderRefund.STATUS_PENDING_AUDIT)) {
            throw new IllegalStateException("只能审核待审核状态的退款申请");
        }

        refund.setStatus(request.getAuditStatus());
        refund.setAuditorId(request.getAuditorId());
        refund.setAuditedAt(LocalDateTime.now());
        refund.setAuditNote(request.getAuditNote());

        OrderRefund saved = refundRepository.save(refund);
        log.info("退款审核完成，refundNo={}, auditStatus={}", request.getRefundNo(), request.getAuditStatus());

        // 审核通过，触发退款处理
        if (request.getAuditStatus().equals(OrderRefund.STATUS_APPROVED)) {
            processRefund(saved.getRefundNo());
        } else if (request.getAuditStatus().equals(OrderRefund.STATUS_REJECTED)) {
            // 审核拒绝，恢复订单状态
            Order order = orderRepository.findByOrderNo(refund.getOrderNo())
                    .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
            order.setOrderStatus(Order.OrderStatus.PENDING_USE);
            orderRepository.save(order);
        }

        return convertToResponse(saved);
    }

    /**
     * 处理退款（调用支付渠道退款接口）
     */
    @Transactional
    public void processRefund(String refundNo) {
        OrderRefund refund = refundRepository.findByRefundNo(refundNo)
                .orElseThrow(() -> new IllegalArgumentException("退款申请不存在"));

        if (!refund.getStatus().equals(OrderRefund.STATUS_APPROVED)) {
            throw new IllegalStateException("只能处理审核通过的退款申请");
        }

        refund.setStatus(OrderRefund.STATUS_REFUNDING);
        refundRepository.save(refund);

        log.info("开始处理退款，refundNo={}", refundNo);

        // TODO: 调用微信/支付宝退款接口
        // 这里模拟退款成功
        updateRefundStatus(refundNo, OrderRefund.STATUS_SUCCESS, "REFUND_" + System.currentTimeMillis());
    }

    /**
     * 更新退款状态
     */
    @Transactional
    public void updateRefundStatus(String refundNo, Integer status, String paymentRefundNo) {
        OrderRefund refund = refundRepository.findByRefundNo(refundNo)
                .orElseThrow(() -> new IllegalArgumentException("退款申请不存在"));

        refund.setStatus(status);
        refund.setPaymentRefundNo(paymentRefundNo);

        if (status.equals(OrderRefund.STATUS_SUCCESS)) {
            refund.setPaymentRefundAt(LocalDateTime.now());
            
            // 更新订单状态为已完成
            Order order = orderRepository.findByOrderNo(refund.getOrderNo())
                    .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
            order.setOrderStatus(Order.OrderStatus.COMPLETED);
            orderRepository.save(order);
        } else if (status.equals(OrderRefund.STATUS_FAILED)) {
            // 退款失败，恢复订单状态
            Order order = orderRepository.findByOrderNo(refund.getOrderNo())
                    .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
            order.setOrderStatus(Order.OrderStatus.PENDING_USE);
            orderRepository.save(order);
        }

        refundRepository.save(refund);

        log.info("退款状态更新成功，refundNo={}, status={}", refundNo, status);
    }

    /**
     * 查询退款信息
     */
    public RefundResponse queryRefund(String refundNo) {
        OrderRefund refund = refundRepository.findByRefundNo(refundNo)
                .orElseThrow(() -> new IllegalArgumentException("退款申请不存在"));
        return convertToResponse(refund);
    }

    /**
     * 分页查询退款列表
     */
    public Page<RefundResponse> getRefunds(String orderNo, Long userId, Integer status,
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Page<OrderRefund> page = refundRepository.findByFilters(orderNo, userId, status, startTime, endTime, pageable);
        return page.map(this::convertToResponse);
    }

    /**
     * 获取退款统计
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingAudit", refundRepository.countByStatus(OrderRefund.STATUS_PENDING_AUDIT));
        stats.put("approved", refundRepository.countByStatus(OrderRefund.STATUS_APPROVED));
        stats.put("rejected", refundRepository.countByStatus(OrderRefund.STATUS_REJECTED));
        stats.put("refunding", refundRepository.countByStatus(OrderRefund.STATUS_REFUNDING));
        stats.put("success", refundRepository.countByStatus(OrderRefund.STATUS_SUCCESS));
        stats.put("failed", refundRepository.countByStatus(OrderRefund.STATUS_FAILED));
        return stats;
    }

    /**
     * 转换为响应对象
     */
    private RefundResponse convertToResponse(OrderRefund refund) {
        RefundResponse response = new RefundResponse();
        response.setId(refund.getId());
        response.setRefundNo(refund.getRefundNo());
        response.setOrderNo(refund.getOrderNo());
        response.setUserId(refund.getUserId());
        response.setRefundType(refund.getRefundType());
        response.setRefundTypeText(getRefundTypeText(refund.getRefundType()));
        response.setRefundAmount(refund.getRefundAmount());
        response.setRefundReason(refund.getRefundReason());
        response.setRefundEvidence(refund.getRefundEvidence());
        response.setStatus(refund.getStatus());
        response.setStatusText(getStatusText(refund.getStatus()));
        response.setAuditorId(refund.getAuditorId());
        response.setAuditedAt(refund.getAuditedAt());
        response.setAuditNote(refund.getAuditNote());
        response.setPaymentRefundNo(refund.getPaymentRefundNo());
        response.setPaymentRefundAt(refund.getPaymentRefundAt());
        response.setCreatedAt(refund.getCreatedAt());
        response.setUpdatedAt(refund.getUpdatedAt());
        return response;
    }

    private String getRefundTypeText(Integer type) {
        return switch (type) {
            case OrderRefund.TYPE_FULL -> "全额退款";
            case OrderRefund.TYPE_PARTIAL -> "部分退款";
            default -> "未知";
        };
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case OrderRefund.STATUS_PENDING_AUDIT -> "待审核";
            case OrderRefund.STATUS_APPROVED -> "审核通过";
            case OrderRefund.STATUS_REJECTED -> "审核拒绝";
            case OrderRefund.STATUS_REFUNDING -> "退款中";
            case OrderRefund.STATUS_SUCCESS -> "退款成功";
            case OrderRefund.STATUS_FAILED -> "退款失败";
            default -> "未知";
        };
    }
}
