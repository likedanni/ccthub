package com.ccthub.userservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 商户实体
 * 对应数据库表: merchants
 */
@Data
@Entity
@Table(name = "merchants")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    /**
     * 商户类型:
     * 1 - 景区
     * 2 - 餐饮
     * 3 - 文创
     * 4 - 生鲜便利
     */
    @Column(nullable = false)
    private Integer type;

    /**
     * 合作类型:
     * 1 - 直营
     * 2 - 联营
     * 3 - 加盟
     */
    @Column(name = "cooperation_type", nullable = false)
    private Integer cooperationType;

    @Column(name = "contact_person", nullable = false, length = 50)
    private String contactPerson;

    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Column(name = "business_license", length = 100)
    private String businessLicense;

    @Column(nullable = false, length = 50)
    private String province;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(length = 50)
    private String district;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;

    /**
     * 平台结算费率
     */
    @Column(name = "settlement_rate", precision = 5, scale = 4)
    private BigDecimal settlementRate;

    /**
     * 审核状态:
     * 0 - 待审核
     * 1 - 已通过
     * 2 - 已拒绝
     */
    @Column(name = "audit_status")
    private Integer auditStatus = 0;

    /**
     * 状态:
     * 1 - 正常
     * 0 - 停用
     */
    @Column
    private Integer status = 1;

    /**
     * 商户等级
     */
    @Column
    private Integer level = 1;

    /**
     * 商户评分 (1-5分)
     */
    @Column(precision = 3, scale = 2)
    private BigDecimal score = new BigDecimal("5.00");

    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (auditStatus == null) {
            auditStatus = 0;
        }
        if (status == null) {
            status = 1;
        }
        if (level == null) {
            level = 1;
        }
        if (score == null) {
            score = new BigDecimal("5.00");
        }
    }
}
