package com.ccthub.userservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 创建订单请求
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
public class OrderRequest {

    private Long userId;
    private Long scenicSpotId;
    private Long ticketId;
    private LocalDate visitDate;
    private String contactName;
    private String contactPhone;
    private String remark;

    /**
     * 游客信息列表
     */
    private List<VisitorInfo> visitors;

    @Data
    public static class VisitorInfo {
        private Long ticketPriceId; // 票价ID
        private String visitorName; // 游客姓名
        private String visitorIdCard; // 游客身份证
        private String visitorPhone; // 游客手机
        private BigDecimal price; // 票价
    }
}
