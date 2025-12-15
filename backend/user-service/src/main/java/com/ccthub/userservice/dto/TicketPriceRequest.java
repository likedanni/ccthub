package com.ccthub.userservice.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 票价请求DTO
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
public class TicketPriceRequest {
    
    @NotNull(message = "票种ID不能为空")
    private Long ticketId;
    
    @NotNull(message = "价格日期不能为空")
    private LocalDate priceDate;
    
    @NotNull(message = "价格类型不能为空")
    @Min(value = 1, message = "价格类型必须为1-成人票、2-学生票、3-儿童票、4-老年票")
    @Max(value = 4, message = "价格类型必须为1-成人票、2-学生票、3-儿童票、4-老年票")
    private Integer priceType;
    
    @NotNull(message = "原价不能为空")
    @DecimalMin(value = "0.01", message = "原价必须大于0")
    private BigDecimal originalPrice;
    
    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于0")
    private BigDecimal sellPrice;
    
    @NotNull(message = "总库存不能为空")
    @Min(value = 0, message = "总库存不能为负数")
    private Integer inventoryTotal;
    
    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;
}
