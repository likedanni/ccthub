package com.ccthub.userservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 订单响应
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
public class OrderResponse {

    private Long id;
    private String orderNo;
    private Long userId;
    private Long scenicSpotId;
    private Long ticketId;
    private LocalDate visitDate;
    private Integer visitorCount;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private String status;
    private String contactName;
    private String contactPhone;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
    private LocalDateTime refundTime;

    /**
     * 关联信息（可选，详情接口返回）
     */
    private String scenicSpotName;
    private String ticketName;
    private List<OrderItemResponse> items;

    @Data
    public static class OrderItemResponse {
        private Long id;
        private Long orderId;
        private Long ticketPriceId;
        private String visitorName;
        private String visitorIdCard;
        private String visitorPhone;
        private BigDecimal price;
        private String verificationCode;
        private String qrCodeUrl;
        private Boolean isVerified;
        private LocalDateTime verifyTime;
        private Long verifyStaffId;
        private LocalDateTime createTime;

        /**
         * 票价类型名称
         */
        private String priceTypeName;
    }
}
