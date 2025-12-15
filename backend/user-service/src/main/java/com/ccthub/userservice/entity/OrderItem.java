package com.ccthub.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 订单项实体（电子票券）
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "ticket_price_id", nullable = false)
    private Long ticketPriceId;

    @Column(name = "visitor_name", nullable = false, length = 50)
    private String visitorName;

    @Column(name = "visitor_id_card", length = 18)
    private String visitorIdCard;

    @Column(name = "visitor_phone", length = 20)
    private String visitorPhone;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "verification_code", unique = true, nullable = false, length = 64)
    private String verificationCode;

    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "verify_time")
    private LocalDateTime verifyTime;

    @Column(name = "verify_staff_id")
    private Long verifyStaffId;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /**
     * 生成核销码（UUID）
     */
    @PrePersist
    public void generateVerificationCode() {
        if (this.verificationCode == null || this.verificationCode.isEmpty()) {
            this.verificationCode = UUID.randomUUID().toString().replace("-", "");
        }
    }
}
