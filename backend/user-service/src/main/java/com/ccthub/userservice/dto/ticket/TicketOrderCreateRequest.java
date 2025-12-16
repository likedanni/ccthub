package com.ccthub.userservice.dto.ticket;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 门票订单创建请求DTO
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Data
public class TicketOrderCreateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "景区ID不能为空")
    private Long scenicSpotId;

    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    @NotNull(message = "游玩日期不能为空")
    private LocalDate visitDate;

    @NotNull(message = "联系人姓名不能为空")
    private String contactName;

    @NotNull(message = "联系电话不能为空")
    private String contactPhone;

    @NotNull(message = "门票列表不能为空")
    @Min(value = 1, message = "至少需要一张门票")
    private List<TicketItem> tickets;

    private String remark;

    @Data
    public static class TicketItem {
        @NotNull(message = "票价ID不能为空")
        private Long ticketPriceId;

        @NotNull(message = "游客姓名不能为空")
        private String visitorName;

        private String visitorIdCard;

        private String visitorPhone;

        @NotNull(message = "票价不能为空")
        private BigDecimal price;

        @NotNull(message = "商品名称不能为空")
        private String productName;
    }
}
