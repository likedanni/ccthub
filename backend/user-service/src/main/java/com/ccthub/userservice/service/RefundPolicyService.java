package com.ccthub.userservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.ccthub.userservice.entity.Order;
import com.ccthub.userservice.entity.Ticket;
import com.ccthub.userservice.repository.OrderRepository;
import com.ccthub.userservice.repository.TicketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 退款规则计算服务
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundPolicyService {

    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final ObjectMapper objectMapper;

    /**
     * 退款规则计算结果
     */
    public static class RefundCalculationResult {
        private BigDecimal refundAmount; // 退款金额
        private BigDecimal refundFee; // 退款手续费
        private BigDecimal actualRefund; // 实际到账金额
        private int refundRate; // 退款比例（百分比）
        private boolean canRefund; // 是否允许退款
        private String reason; // 不允许退款的原因

        // Getters and Setters
        public BigDecimal getRefundAmount() {
            return refundAmount;
        }

        public void setRefundAmount(BigDecimal refundAmount) {
            this.refundAmount = refundAmount;
        }

        public BigDecimal getRefundFee() {
            return refundFee;
        }

        public void setRefundFee(BigDecimal refundFee) {
            this.refundFee = refundFee;
        }

        public BigDecimal getActualRefund() {
            return actualRefund;
        }

        public void setActualRefund(BigDecimal actualRefund) {
            this.actualRefund = actualRefund;
        }

        public int getRefundRate() {
            return refundRate;
        }

        public void setRefundRate(int refundRate) {
            this.refundRate = refundRate;
        }

        public boolean isCanRefund() {
            return canRefund;
        }

        public void setCanRefund(boolean canRefund) {
            this.canRefund = canRefund;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    /**
     * 计算订单退款金额
     * 
     * @param orderNo 订单号
     * @return 退款计算结果
     */
    public RefundCalculationResult calculateRefund(String orderNo) {
        RefundCalculationResult result = new RefundCalculationResult();

        // 查询订单
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        // 查询票种
        Ticket ticket = ticketRepository.findById(order.getTicketId())
                .orElseThrow(() -> new IllegalArgumentException("票种不存在"));

        // 检查订单状态
        if (order.getStatus() != Order.STATUS_PAID) {
            result.setCanRefund(false);
            result.setReason("只有已支付订单才能退款");
            return result;
        }

        // 检查是否已核销
        if (order.getVerifyStatus() == Order.VERIFY_STATUS_VERIFIED) {
            result.setCanRefund(false);
            result.setReason("已核销订单不可退款");
            return result;
        }

        // 解析退款规则
        try {
            JsonNode refundPolicy = objectMapper.readTree(ticket.getRefundPolicy());

            // 计算距离使用日期的天数
            LocalDate useDate = order.getUseDate();
            LocalDate today = LocalDate.now();
            long daysBeforeUse = ChronoUnit.DAYS.between(today, useDate);

            // 根据退款规则计算退款比例
            int refundRate = calculateRefundRate(refundPolicy, daysBeforeUse);

            if (refundRate == 0) {
                result.setCanRefund(false);
                result.setReason("根据退款规则，当前时间不支持退款");
                return result;
            }

            // 计算退款金额
            BigDecimal orderAmount = order.getPayAmount();
            BigDecimal refundAmount = orderAmount.multiply(BigDecimal.valueOf(refundRate))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);

            // 计算退款手续费
            BigDecimal refundFee = calculateRefundFee(refundPolicy, refundAmount, daysBeforeUse);

            // 实际到账金额
            BigDecimal actualRefund = refundAmount.subtract(refundFee);

            result.setCanRefund(true);
            result.setRefundAmount(refundAmount);
            result.setRefundFee(refundFee);
            result.setActualRefund(actualRefund);
            result.setRefundRate(refundRate);

            log.info("退款计算完成，订单号={}, 退款金额={}, 手续费={}, 实际到账={}",
                    orderNo, refundAmount, refundFee, actualRefund);

        } catch (Exception e) {
            log.error("解析退款规则失败", e);
            result.setCanRefund(false);
            result.setReason("退款规则配置错误");
        }

        return result;
    }

    /**
     * 计算部分退款金额
     * 
     * @param orderNo  订单号
     * @param quantity 退款数量
     * @return 退款计算结果
     */
    public RefundCalculationResult calculatePartialRefund(String orderNo, int quantity) {
        RefundCalculationResult result = new RefundCalculationResult();

        // 查询订单
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        // 检查退款数量
        if (quantity <= 0 || quantity >= order.getQuantity()) {
            result.setCanRefund(false);
            result.setReason("退款数量必须大于0且小于订单总数");
            return result;
        }

        // 查询票种
        Ticket ticket = ticketRepository.findById(order.getTicketId())
                .orElseThrow(() -> new IllegalArgumentException("票种不存在"));

        // 检查订单状态
        if (order.getStatus() != Order.STATUS_PAID) {
            result.setCanRefund(false);
            result.setReason("只有已支付订单才能退款");
            return result;
        }

        // 解析退款规则
        try {
            JsonNode refundPolicy = objectMapper.readTree(ticket.getRefundPolicy());

            // 计算距离使用日期的天数
            LocalDate useDate = order.getUseDate();
            LocalDate today = LocalDate.now();
            long daysBeforeUse = ChronoUnit.DAYS.between(today, useDate);

            // 根据退款规则计算退款比例
            int refundRate = calculateRefundRate(refundPolicy, daysBeforeUse);

            if (refundRate == 0) {
                result.setCanRefund(false);
                result.setReason("根据退款规则，当前时间不支持退款");
                return result;
            }

            // 计算单张票价
            BigDecimal unitPrice = order.getPayAmount().divide(
                    BigDecimal.valueOf(order.getQuantity()), 2, RoundingMode.DOWN);

            // 计算部分退款金额
            BigDecimal partialAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal refundAmount = partialAmount.multiply(BigDecimal.valueOf(refundRate))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);

            // 计算退款手续费
            BigDecimal refundFee = calculateRefundFee(refundPolicy, refundAmount, daysBeforeUse);

            // 实际到账金额
            BigDecimal actualRefund = refundAmount.subtract(refundFee);

            result.setCanRefund(true);
            result.setRefundAmount(refundAmount);
            result.setRefundFee(refundFee);
            result.setActualRefund(actualRefund);
            result.setRefundRate(refundRate);

            log.info("部分退款计算完成，订单号={}, 退款数量={}, 退款金额={}, 手续费={}, 实际到账={}",
                    orderNo, quantity, refundAmount, refundFee, actualRefund);

        } catch (Exception e) {
            log.error("解析退款规则失败", e);
            result.setCanRefund(false);
            result.setReason("退款规则配置错误");
        }

        return result;
    }

    /**
     * 根据退款规则计算退款比例
     * 
     * @param refundPolicy  退款规则JSON
     * @param daysBeforeUse 距离使用日期的天数
     * @return 退款比例（0-100）
     */
    private int calculateRefundRate(JsonNode refundPolicy, long daysBeforeUse) {
        // 示例退款规则格式：
        // {
        // "rules": [
        // {"days": 7, "rate": 100}, // 7天前退款，全额退款
        // {"days": 3, "rate": 50}, // 3-7天退款，退50%
        // {"days": 1, "rate": 30}, // 1-3天退款，退30%
        // {"days": 0, "rate": 0} // 1天内不可退款
        // ]
        // }

        if (refundPolicy == null || !refundPolicy.has("rules")) {
            // 默认规则：3天前全额退款
            return daysBeforeUse >= 3 ? 100 : 0;
        }

        JsonNode rules = refundPolicy.get("rules");
        for (JsonNode rule : rules) {
            int days = rule.get("days").asInt();
            if (daysBeforeUse >= days) {
                return rule.get("rate").asInt();
            }
        }

        return 0;
    }

    /**
     * 计算退款手续费
     * 
     * @param refundPolicy  退款规则JSON
     * @param refundAmount  退款金额
     * @param daysBeforeUse 距离使用日期的天数
     * @return 手续费金额
     */
    private BigDecimal calculateRefundFee(JsonNode refundPolicy, BigDecimal refundAmount, long daysBeforeUse) {
        // 示例手续费规则格式：
        // {
        // "feeRules": [
        // {"days": 7, "feeRate": 0}, // 7天前退款，免手续费
        // {"days": 3, "feeRate": 0.05}, // 3-7天退款，5%手续费
        // {"days": 1, "feeRate": 0.10} // 1-3天退款，10%手续费
        // ]
        // }

        if (refundPolicy == null || !refundPolicy.has("feeRules")) {
            // 默认规则：3天前免手续费，3天内5%手续费
            BigDecimal feeRate = daysBeforeUse >= 3 ? BigDecimal.ZERO : BigDecimal.valueOf(0.05);
            return refundAmount.multiply(feeRate).setScale(2, RoundingMode.UP);
        }

        JsonNode feeRules = refundPolicy.get("feeRules");
        for (JsonNode rule : feeRules) {
            int days = rule.get("days").asInt();
            if (daysBeforeUse >= days) {
                double feeRate = rule.get("feeRate").asDouble();
                return refundAmount.multiply(BigDecimal.valueOf(feeRate))
                        .setScale(2, RoundingMode.UP);
            }
        }

        return BigDecimal.ZERO;
    }
}
