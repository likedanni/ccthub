package com.ccthub.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 票种响应DTO
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Data
public class TicketResponse {
    
    private Long id;
    private Long scenicSpotId;
    private String scenicSpotName;
    private String name;
    private Integer type;
    private String typeText;
    private String description;
    private Integer validityType;
    private String validityTypeText;
    private Integer validDays;
    private Integer advanceDays;
    private Map<String, Object> refundPolicy;
    private Map<String, Object> changePolicy;
    private Integer limitPerUser;
    private Integer limitPerOrder;
    private Integer limitPerDay;
    private Boolean requireRealName;
    private Boolean requireIdCard;
    private Integer verificationMode;
    private String verificationModeText;
    private Integer status;
    private String statusText;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    /**
     * 转换票种类型文本
     */
    public void setTypeText() {
        this.typeText = switch (this.type) {
            case 1 -> "单票";
            case 2 -> "联票";
            case 3 -> "套票";
            default -> "未知";
        };
    }
    
    /**
     * 转换有效期类型文本
     */
    public void setValidityTypeText() {
        this.validityTypeText = switch (this.validityType) {
            case 1 -> "指定日期";
            case 2 -> "有效天数";
            default -> "未知";
        };
    }
    
    /**
     * 转换核验方式文本
     */
    public void setVerificationModeText() {
        this.verificationModeText = switch (this.verificationMode) {
            case 1 -> "二维码";
            case 2 -> "人脸识别";
            case 3 -> "身份证";
            default -> "未知";
        };
    }
    
    /**
     * 转换状态文本
     */
    public void setStatusText() {
        this.statusText = switch (this.status) {
            case 0 -> "已下架";
            case 1 -> "已上架";
            default -> "未知";
        };
    }
}
