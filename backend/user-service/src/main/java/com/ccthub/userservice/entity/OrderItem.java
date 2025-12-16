package com.ccthub.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 订单明细实体（通用订单明细）
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Data
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, length = 32)
    private String orderNo;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "sku_id")
    private Long skuId;

    @Column(name = "sku_specs", length = 500)
    private String skuSpecs;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "verification_code", unique = true, length = 50)
    private String verificationCode;

    @Column(name = "verification_status")
    private Integer verificationStatus = VerificationStatus.NOT_VERIFIED;

    @Column(name = "ticket_date")
    private LocalDate ticketDate;

    @Column(name = "visitor_name", length = 50)
    private String visitorName;

    @Column(name = "visitor_name_encrypted", length = 128)
    private String visitorNameEncrypted;

    /**
     * 生成核销码（UUID）
     */
    @PrePersist
    public void generateVerificationCode() {
        if (this.verificationCode == null || this.verificationCode.isEmpty()) {
            this.verificationCode = UUID.randomUUID().toString().replace("-", "");
        }
    }

    /**
     * 核销状态常量
     */
    public static class VerificationStatus {
        public static final Integer NOT_VERIFIED = 0;  // 未核销
        public static final Integer VERIFIED = 1;      // 已核销
        public static final Integer EXPIRED = 2;       // 已过期
    }
}
