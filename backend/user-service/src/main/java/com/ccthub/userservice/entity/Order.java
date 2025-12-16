package com.ccthub.userservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 订单实体（通用订单，支持门票/商品/活动）
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_no", length = 32)
    private String orderNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "order_type", nullable = false)
    private Integer orderType;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "pay_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal payAmount;

    @Column(name = "point_amount", precision = 10, scale = 2)
    private BigDecimal pointAmount = BigDecimal.ZERO;

    @Column(name = "point_earned")
    private Integer pointEarned = 0;

    @Column(name = "platform_fee", precision = 10, scale = 2)
    private BigDecimal platformFee = BigDecimal.ZERO;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_status")
    private Integer paymentStatus = PaymentStatus.PENDING;

    @Column(name = "order_status")
    private Integer orderStatus = OrderStatus.PENDING_PAYMENT;

    @Column(name = "refund_status")
    private Integer refundStatus = RefundStatus.NO_REFUND;

    @Column(name = "outer_order_no", length = 64)
    private String outerOrderNo;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 生成订单号
     */
    @PrePersist
    public void generateOrderNo() {
        if (this.orderNo == null || this.orderNo.isEmpty()) {
            this.orderNo = "ORDER" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
        }
    }

    /**
     * 订单类型常量
     */
    public static class OrderType {
        public static final Integer TICKET = 1;        // 门票
        public static final Integer PRODUCT = 2;       // 实物商品
        public static final Integer ACTIVITY = 3;      // 活动
    }

    /**
     * 支付状态常量
     */
    public static class PaymentStatus {
        public static final Integer PENDING = 0;       // 待支付
        public static final Integer SUCCESS = 1;       // 支付成功
        public static final Integer FAILED = 2;        // 支付失败
        public static final Integer REFUNDED = 3;      // 已退款
        public static final Integer PROCESSING = 4;    // 处理中
    }

    /**
     * 订单状态常量
     */
    public static class OrderStatus {
        public static final Integer PENDING_PAYMENT = 0;  // 待付款
        public static final Integer PENDING_USE = 1;      // 待使用
        public static final Integer COMPLETED = 2;        // 已完成
        public static final Integer CANCELLED = 3;        // 已取消
        public static final Integer REFUNDING = 4;        // 退款中
    }

    /**
     * 退款状态常量
     */
    public static class RefundStatus {
        public static final Integer NO_REFUND = 0;     // 无退款
        public static final Integer REFUNDING = 1;     // 退款中
        public static final Integer SUCCESS = 2;       // 退款成功
        public static final Integer FAILED = 3;        // 退款失败
    }
}
