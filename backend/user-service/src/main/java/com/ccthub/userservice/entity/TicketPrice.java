package com.ccthub.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 票价实体 - 定义门票的价格和库存日历
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
@Entity
@Table(name = "ticket_prices", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ticket_id", "price_date", "price_type"})
})
public class TicketPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关联的票种ID
     */
    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;
    
    /**
     * 价格日期
     */
    @Column(name = "price_date", nullable = false)
    private LocalDate priceDate;
    
    /**
     * 价格类型：1-成人票，2-学生票，3-儿童票，4-老年票
     */
    @Column(name = "price_type", nullable = false)
    private Integer priceType;
    
    /**
     * 原价
     */
    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    /**
     * 售价
     */
    @Column(name = "sell_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellPrice;
    
    /**
     * 总库存
     */
    @Column(name = "inventory_total", nullable = false)
    private Integer inventoryTotal;
    
    /**
     * 可用库存
     */
    @Column(name = "inventory_available", nullable = false)
    private Integer inventoryAvailable;
    
    /**
     * 锁定库存（待支付订单占用）
     */
    @Column(name = "inventory_locked", nullable = false)
    private Integer inventoryLocked = 0;
    
    /**
     * 是否启用
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    /**
     * 乐观锁版本号（防止超卖）
     */
    @Version
    @Column(nullable = false)
    private Integer version = 0;
    
    /**
     * 价格类型常量
     */
    public static class PriceType {
        public static final int ADULT = 1;    // 成人票
        public static final int STUDENT = 2;  // 学生票
        public static final int CHILD = 3;    // 儿童票
        public static final int SENIOR = 4;   // 老年票
    }
    
    /**
     * 检查库存是否充足
     */
    public boolean hasEnoughInventory(int quantity) {
        return inventoryAvailable >= quantity;
    }
    
    /**
     * 锁定库存（下单时调用）
     */
    public void lockInventory(int quantity) {
        if (!hasEnoughInventory(quantity)) {
            throw new IllegalStateException("库存不足");
        }
        this.inventoryAvailable -= quantity;
        this.inventoryLocked += quantity;
    }
    
    /**
     * 释放库存（取消订单/超时未支付）
     */
    public void releaseInventory(int quantity) {
        if (this.inventoryLocked < quantity) {
            throw new IllegalStateException("锁定库存不足");
        }
        this.inventoryLocked -= quantity;
        this.inventoryAvailable += quantity;
    }
    
    /**
     * 扣减库存（支付成功后调用）
     */
    public void deductInventory(int quantity) {
        if (this.inventoryLocked < quantity) {
            throw new IllegalStateException("锁定库存不足");
        }
        this.inventoryLocked -= quantity;
        this.inventoryTotal -= quantity;
    }
}
