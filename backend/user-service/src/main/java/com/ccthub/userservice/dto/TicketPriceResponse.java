package com.ccthub.userservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 票价响应DTO
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
public class TicketPriceResponse {
    
    private Long id;
    private Long ticketId;
    private String ticketName;
    private LocalDate priceDate;
    private Integer priceType;
    private String priceTypeText;
    private BigDecimal originalPrice;
    private BigDecimal sellPrice;
    private Integer inventoryTotal;
    private Integer inventoryAvailable;
    private Integer inventoryLocked;
    private Integer inventorySold;
    private Boolean isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    /**
     * 转换价格类型文本
     */
    public void setPriceTypeText() {
        this.priceTypeText = switch (this.priceType) {
            case 1 -> "成人票";
            case 2 -> "学生票";
            case 3 -> "儿童票";
            case 4 -> "老年票";
            default -> "未知";
        };
    }
    
    /**
     * 计算已售数量
     */
    public void setInventorySold() {
        this.inventorySold = this.inventoryTotal - this.inventoryAvailable - this.inventoryLocked;
    }
    
    /**
     * 计算库存占用率（百分比）
     */
    public double getOccupancyRate() {
        if (inventoryTotal == 0) {
            return 0;
        }
        return (double) (inventorySold + inventoryLocked) / inventoryTotal * 100;
    }
    
    /**
     * 判断库存是否充足
     */
    public boolean isInventoryAvailable(int quantity) {
        return inventoryAvailable >= quantity;
    }
}
