package com.ccthub.userservice.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 订单实体
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true, nullable = false, length = 32)
    private String orderNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "scenic_spot_id", nullable = false)
    private Long scenicSpotId;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "visitor_count", nullable = false)
    private Integer visitorCount;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "actual_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualAmount;

    @Column(nullable = false, length = 20)
    private String status = OrderStatus.PENDING_PAYMENT;

    @Column(name = "contact_name", nullable = false, length = 50)
    private String contactName;

    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Column(length = 500)
    private String remark;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    @Column(name = "refund_time")
    private LocalDateTime refundTime;

    /**
     * 订单状态常量
     */
    public static class OrderStatus {
        public static final String PENDING_PAYMENT = "PENDING_PAYMENT"; // 待支付
        public static final String PAID = "PAID"; // 已支付
        public static final String PENDING_USE = "PENDING_USE"; // 待使用
        public static final String USED = "USED"; // 已使用
        public static final String COMPLETED = "COMPLETED"; // 已完成
        public static final String CANCELLED = "CANCELLED"; // 已取消
        public static final String REFUNDING = "REFUNDING"; // 退款中
        public static final String REFUNDED = "REFUNDED"; // 已退款
    }
}
