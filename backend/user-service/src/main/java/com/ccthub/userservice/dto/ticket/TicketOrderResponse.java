package com.ccthub.userservice.dto.ticket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 门票订单响应DTO
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Data
public class TicketOrderResponse {

    private Long id;
    private String orderNo;
    private Long userId;
    private Long scenicSpotId;
    private Long merchantId;
    private LocalDate visitDate;
    private Integer visitorCount;
    private String contactName;
    private String contactPhone;

    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private BigDecimal actualAmount;
    private BigDecimal pointAmount;
    private Integer pointEarned;

    private String paymentMethod;
    private Integer paymentStatus;
    private String paymentStatusText;
    private Integer orderStatus;
    private String orderStatusText;
    private String status;

    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<TicketItemResponse> tickets;

    @Data
    public static class TicketItemResponse {
        private Long id;
        private String productName;
        private BigDecimal unitPrice;
        private String visitorName;
        private String visitorIdCard;
        private String visitorPhone;
        private String verificationCode;
        private Integer verificationStatus;
        private String verificationStatusText;
        private LocalDate ticketDate;
    }
}
