package com.ccthub.userservice.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.payment.PaymentRequest;
import com.ccthub.userservice.dto.payment.PaymentResponse;
import com.ccthub.userservice.entity.Order;
import com.ccthub.userservice.entity.Payment;
import com.ccthub.userservice.repository.OrderRepository;
import com.ccthub.userservice.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付服务
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    /**
     * 创建支付订单
     */
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("创建支付订单，orderNo={}, paymentType={}", request.getOrderNo(), request.getPaymentType());

        // 校验订单是否已有成功支付
        paymentRepository.findByOrderNoAndStatus(request.getOrderNo(), Payment.STATUS_SUCCESS)
                .ifPresent(p -> {
                    throw new IllegalStateException("订单已支付成功");
                });

        // 创建支付记录
        Payment payment = new Payment();
        payment.setOrderNo(request.getOrderNo());
        payment.setPaymentType(request.getPaymentType());
        payment.setPaymentChannel(request.getPaymentChannel());
        payment.setPaymentAmount(request.getPaymentAmount());
        payment.setPayerId(request.getPayerId());
        payment.setStatus(Payment.STATUS_PENDING);

        Payment saved = paymentRepository.save(payment);
        log.info("支付订单创建成功，paymentNo={}", saved.getPaymentNo());

        return convertToResponse(saved);
    }

    /**
     * 更新支付状态（支付回调处理）
     */
    @Transactional
    public void updatePaymentStatus(String paymentNo, Integer status, String thirdPartyNo) {
        Payment payment = paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new IllegalArgumentException("支付订单不存在"));

        payment.setStatus(status);
        payment.setThirdPartyNo(thirdPartyNo);

        if (status.equals(Payment.STATUS_SUCCESS)) {
            payment.setPaymentTime(LocalDateTime.now());
            // 支付成功，异步更新订单状态
            updateOrderStatusAsync(payment.getOrderNo());
        }

        payment.setCallbackTime(LocalDateTime.now());
        paymentRepository.save(payment);

        log.info("支付状态更新成功，paymentNo={}, status={}", paymentNo, status);
    }

    /**
     * 异步更新订单状态（支付成功后）
     */
    @Async
    @Transactional
    public void updateOrderStatusAsync(String orderNo) {
        try {
            log.info("开始更新订单状态，orderNo={}", orderNo);
            
            Order order = orderRepository.findByOrderNo(orderNo)
                    .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

            // 只有待支付订单才能更新为已支付
            if (!Order.OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
                log.warn("订单状态不是待支付，无法更新，orderNo={}, currentStatus={}", orderNo, order.getStatus());
                return;
            }

            // 更新订单状态为已支付
            order.setStatus(Order.OrderStatus.PAID);
            order.setPayTime(LocalDateTime.now());
            orderRepository.save(order);

            log.info("订单状态更新成功，orderNo={}, status={}", orderNo, Order.OrderStatus.PAID);
        } catch (Exception e) {
            log.error("更新订单状态失败，orderNo={}, error={}", orderNo, e.getMessage(), e);
            // 这里可以发送到消息队列重试
            throw e;
        }
    }

    /**
     * 查询支付信息
     */
    public PaymentResponse queryPayment(String paymentNo) {
        Payment payment = paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new IllegalArgumentException("支付订单不存在"));
        return convertToResponse(payment);
    }

    /**
     * 关闭支付订单
     */
    @Transactional
    public void closePayment(String paymentNo) {
        Payment payment = paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new IllegalArgumentException("支付订单不存在"));

        if (!payment.getStatus().equals(Payment.STATUS_PENDING)) {
            throw new IllegalStateException("只能关闭待支付订单");
        }

        payment.setStatus(Payment.STATUS_CLOSED);
        paymentRepository.save(payment);

        log.info("支付订单已关闭，paymentNo={}", paymentNo);
    }

    /**
     * 分页查询支付列表
     */
    public Page<PaymentResponse> getPayments(String orderNo, String paymentType, Integer status,
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Page<Payment> page = paymentRepository.findByFilters(orderNo, paymentType, status, startTime, endTime,
                pageable);
        return page.map(this::convertToResponse);
    }

    /**
     * 获取支付统计
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", paymentRepository.countByStatus(Payment.STATUS_PENDING));
        stats.put("success", paymentRepository.countByStatus(Payment.STATUS_SUCCESS));
        stats.put("failed", paymentRepository.countByStatus(Payment.STATUS_FAILED));
        stats.put("processing", paymentRepository.countByStatus(Payment.STATUS_PROCESSING));
        return stats;
    }

    /**
     * 转换为响应对象
     */
    private PaymentResponse convertToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentNo(payment.getPaymentNo());
        response.setOrderNo(payment.getOrderNo());
        response.setPaymentType(payment.getPaymentType());
        response.setPaymentTypeText(getPaymentTypeText(payment.getPaymentType()));
        response.setPaymentChannel(payment.getPaymentChannel());
        response.setPaymentChannelText(getPaymentChannelText(payment.getPaymentChannel()));
        response.setPaymentAmount(payment.getPaymentAmount());
        response.setStatus(payment.getStatus());
        response.setStatusText(getStatusText(payment.getStatus()));
        response.setThirdPartyNo(payment.getThirdPartyNo());
        response.setPayerId(payment.getPayerId());
        response.setPaymentTime(payment.getPaymentTime());
        response.setCallbackTime(payment.getCallbackTime());
        response.setCreateTime(payment.getCreateTime());
        return response;
    }

    private String getPaymentTypeText(String type) {
        return switch (type) {
            case Payment.TYPE_WECHAT -> "微信支付";
            case Payment.TYPE_ALIPAY -> "支付宝";
            case Payment.TYPE_BALANCE -> "余额支付";
            default -> type;
        };
    }

    private String getPaymentChannelText(String channel) {
        return switch (channel) {
            case Payment.CHANNEL_MINIAPP -> "小程序";
            case Payment.CHANNEL_APP -> "APP";
            case Payment.CHANNEL_H5 -> "H5";
            case Payment.CHANNEL_NATIVE -> "扫码";
            default -> channel;
        };
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case Payment.STATUS_PENDING -> "待支付";
            case Payment.STATUS_SUCCESS -> "支付成功";
            case Payment.STATUS_FAILED -> "支付失败";
            case Payment.STATUS_CLOSED -> "已关闭";
            case Payment.STATUS_PROCESSING -> "处理中";
            default -> "未知";
        };
    }
}
