package com.ccthub.userservice.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.payment.RefundAuditRequest;
import com.ccthub.userservice.dto.payment.RefundRequest;
import com.ccthub.userservice.dto.payment.RefundResponse;
import com.ccthub.userservice.entity.OrderRefund;
import com.ccthub.userservice.repository.OrderRefundRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 退款服务
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final OrderRefundRepository refundRepository;
    private final RefundPolicyService refundPolicyService;

    /**
     * 创建退款申请
     */
    @Transactional
    public RefundResponse createRefund(RefundRequest request) {
        log.info("创建退款申请，orderNo={}, refundAmount={}", request.getOrderNo(), request.getRefundAmount());

        // 校验订单是否已有待处理的退款
        refundRepository.findByOrderNo(request.getOrderNo()).stream()
                .filter(r -> r.getStatus() < OrderRefund.STATUS_SUCCESS)
                .findFirst()
                .ifPresent(r -> {
                    throw new IllegalStateException("订单已有待处理的退款申请");
                });

        // 使用退款规则计算退款金额
        RefundPolicyService.RefundCalculationResult calculation;
        if (request.getRefundType() == OrderRefund.TYPE_PARTIAL) {
            // 部分退款，需要传入退款数量
            Integer quantity = request.getRefundQuantity();
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("部分退款必须指定退款数量");
            }
            calculation = refundPolicyService.calculatePartialRefund(request.getOrderNo(), quantity);
        } else {
            // 全额退款
            calculation = refundPolicyService.calculateRefund(request.getOrderNo());
        }

        // 检查是否允许退款
        if (!calculation.isCanRefund()) {
            throw new IllegalStateException(calculation.getReason());
        }

        // 创建退款记录
        OrderRefund refund = new OrderRefund();
        refund.setOrderNo(request.getOrderNo());
        refund.setUserId(request.getUserId());
        refund.setRefundType(request.getRefundType());
        refund.setRefundAmount(calculation.getRefundAmount()); // 使用计算出的退款金额
        refund.setRefundFee(calculation.getRefundFee()); // 设置退款手续费
        refund.setActualRefund(calculation.getActualRefund()); // 设置实际到账金额
        refund.setRefundReason(request.getRefundReason());
        refund.setRefundEvidence(request.getRefundEvidence());
        refund.setStatus(OrderRefund.STATUS_PENDING_AUDIT);

        OrderRefund saved = refundRepository.save(refund);
        log.info("退款申请创建成功，refundNo={}, refundAmount={}, refundFee={}, actualRefund={}",
                saved.getRefundNo(), saved.getRefundAmount(), saved.getRefundFee(), saved.getActualRefund());

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
            throw new IllegalStateException("只能审核待审核的退款申请");
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
