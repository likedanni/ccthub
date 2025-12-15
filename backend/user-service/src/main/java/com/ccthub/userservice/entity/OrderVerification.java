package com.ccthub.userservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 订单核销记录实体
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
@Entity
@Table(name = "order_verifications")
public class OrderVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, length = 32)
    private String orderNo;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "operator_id", nullable = false)
    private Long operatorId;

    @Column(name = "verified_quantity")
    private Integer verifiedQuantity = 1; // 本次核销数量，默认1张

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (verifiedAt == null) {
            verifiedAt = LocalDateTime.now();
        }
        if (verifiedQuantity == null) {
            verifiedQuantity = 1;
        }
    }
}
