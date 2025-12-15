package com.ccthub.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付流水实体
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_no", unique = true, nullable = false, length = 32)
    private String paymentNo;

    @Column(name = "order_no", nullable = false, length = 32)
    private String orderNo;

    @Column(name = "payment_type", nullable = false, length = 20)
    private String paymentType; // wechat, alipay, balance

    @Column(name = "payment_channel", nullable = false, length = 50)
    private String paymentChannel; // miniapp, app, h5, native

    @Column(name = "payment_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "status", nullable = false)
    private Integer status; // 0-待支付, 1-成功, 2-失败, 3-关闭, 4-处理中

    @Column(name = "third_party_no", length = 64)
    private String thirdPartyNo; // 第三方支付平台流水号

    @Column(name = "payer_id", length = 100)
    private String payerId; // 支付方标识（如微信openid）

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "callback_time")
    private LocalDateTime callbackTime;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (paymentNo == null) {
            paymentNo = "PAY" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
        }
    }

    // 状态枚举常量
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = 2;
    public static final int STATUS_CLOSED = 3;
    public static final int STATUS_PROCESSING = 4;

    // 支付类型常量
    public static final String TYPE_WECHAT = "wechat";
    public static final String TYPE_ALIPAY = "alipay";
    public static final String TYPE_BALANCE = "balance";

    // 支付渠道常量
    public static final String CHANNEL_MINIAPP = "miniapp";
    public static final String CHANNEL_APP = "app";
    public static final String CHANNEL_H5 = "h5";
    public static final String CHANNEL_NATIVE = "native";
}
