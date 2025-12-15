package com.ccthub.userservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.entity.Order;
import com.ccthub.userservice.entity.Payment;
import com.ccthub.userservice.repository.OrderRepository;
import com.ccthub.userservice.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付超时任务服务
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTimeoutService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    private static final long PAYMENT_TIMEOUT_MINUTES = 30; // 支付超时时间：30分钟

    /**
     * 定时任务：关闭超时未支付订单
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    public void closeTimeoutOrders() {
        log.info("开始执行支付超时订单关闭任务");

        try {
            // 1. 查询超时未支付的支付记录
            LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(PAYMENT_TIMEOUT_MINUTES);
            List<Payment> timeoutPayments = paymentRepository.findTimeoutPendingPayments(
                    Payment.STATUS_PENDING, timeoutTime);

            if (timeoutPayments.isEmpty()) {
                log.info("无超时未支付订单");
                return;
            }

            log.info("发现{}笔超时未支付订单", timeoutPayments.size());

            int closedCount = 0;
            for (Payment payment : timeoutPayments) {
                try {
                    // 2. 关闭支付订单
                    payment.setStatus(Payment.STATUS_CLOSED);
                    paymentRepository.save(payment);

                    // 3. 查询关联订单
                    Order order = orderRepository.findByOrderNo(payment.getOrderNo())
                            .orElse(null);

                    if (order != null && Order.OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
                        // 4. 取消订单
                        order.setStatus(Order.OrderStatus.CANCELLED);
                        order.setCancelTime(LocalDateTime.now());
                        orderRepository.save(order);

                        // 5. 释放库存（TODO: 调用库存服务）
                        releaseStock(order);

                        closedCount++;
                        log.info("订单已超时取消，orderNo={}, paymentNo={}", 
                                order.getOrderNo(), payment.getPaymentNo());
                    }
                } catch (Exception e) {
                    log.error("处理超时订单失败，paymentNo={}, error={}", 
                            payment.getPaymentNo(), e.getMessage(), e);
                    // 继续处理其他订单
                }
            }

            log.info("支付超时订单关闭任务完成，处理{}笔", closedCount);
        } catch (Exception e) {
            log.error("执行支付超时订单关闭任务失败", e);
        }
    }

    /**
     * 释放订单库存
     */
    private void releaseStock(Order order) {
        try {
            log.info("释放订单库存，orderNo={}, ticketId={}, quantity={}", 
                    order.getOrderNo(), order.getTicketId(), order.getVisitorCount());

            // TODO: 调用TicketService释放库存
            // ticketService.releaseStock(order.getTicketId(), order.getVisitorCount());

        } catch (Exception e) {
            log.error("释放库存失败，orderNo={}, error={}", order.getOrderNo(), e.getMessage(), e);
        }
    }

    /**
     * 手动关闭超时订单（管理员操作）
     */
    @Transactional
    public void manualCloseTimeoutOrder(String orderNo) {
        log.info("手动关闭超时订单，orderNo={}", orderNo);

        // 1. 查询订单
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        if (!Order.OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new IllegalStateException("只能关闭待支付订单");
        }

        // 2. 查询支付记录
        Payment payment = paymentRepository.findByOrderNo(orderNo, Payment.STATUS_PENDING)
                .orElseThrow(() -> new IllegalArgumentException("支付记录不存在"));

        // 3. 关闭支付订单
        payment.setStatus(Payment.STATUS_CLOSED);
        paymentRepository.save(payment);

        // 4. 取消订单
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        orderRepository.save(order);

        // 5. 释放库存
        releaseStock(order);

        log.info("订单已手动关闭，orderNo={}", orderNo);
    }
}
