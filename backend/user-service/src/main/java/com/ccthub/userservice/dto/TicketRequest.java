package com.ccthub.userservice.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.util.Map;

/**
 * 票种请求DTO
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
public class TicketRequest {

    @NotNull(message = "景区ID不能为空")
    private Long scenicSpotId;

    @NotBlank(message = "票种名称不能为空")
    @Size(max = 200, message = "票种名称最多200个字符")
    private String name;

    @NotNull(message = "票种类型不能为空")
    @Min(value = 1, message = "票种类型必须为1-单票、2-联票、3-套票")
    @Max(value = 3, message = "票种类型必须为1-单票、2-联票、3-套票")
    private Integer type;

    private String description;

    @NotNull(message = "有效期类型不能为空")
    @Min(value = 1, message = "有效期类型必须为1-指定日期、2-有效天数")
    @Max(value = 2, message = "有效期类型必须为1-指定日期、2-有效天数")
    private Integer validityType;

    @Min(value = 1, message = "有效天数必须大于0")
    private Integer validDays;

    @Min(value = 0, message = "提前预订天数不能为负数")
    private Integer advanceDays;

    /**
     * 退款规则（JSON对象）
     * 示例：{"within_24h": 0.5, "within_48h": 0.7, "over_48h": 1.0}
     */
    @NotNull(message = "退款规则不能为空")
    private Map<String, Object> refundPolicy;

    /**
     * 改签规则（JSON对象）
     * 示例：{"allowed": true, "fee": 10.0, "advance_hours": 24}
     */
    @NotNull(message = "改签规则不能为空")
    private Map<String, Object> changePolicy;

    @Min(value = 1, message = "每用户限购数量必须大于0")
    private Integer limitPerUser;

    @Min(value = 1, message = "每订单限购数量必须大于0")
    private Integer limitPerOrder;

    @Min(value = 1, message = "每日限购数量必须大于0")
    private Integer limitPerDay;

    private Boolean requireRealName = false;

    private Boolean requireIdCard = false;

    @Min(value = 1, message = "核验方式必须为1-二维码、2-人脸、3-身份证")
    @Max(value = 3, message = "核验方式必须为1-二维码、2-人脸、3-身份证")
    private Integer verificationMode = 1;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态必须为0-下架、1-上架")
    @Max(value = 1, message = "状态必须为0-下架、1-上架")
    private Integer status;
}
