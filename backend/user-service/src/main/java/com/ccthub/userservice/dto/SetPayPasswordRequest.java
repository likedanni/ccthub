package com.ccthub.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设置支付密码请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetPayPasswordRequest {

    /**
     * 支付密码（6位数字）
     */
    @NotBlank(message = "支付密码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "支付密码必须为6位数字")
    private String payPassword;

    /**
     * 确认支付密码
     */
    @NotBlank(message = "确认支付密码不能为空")
    private String confirmPassword;

    /**
     * 短信验证码（验证用户身份）
     */
    @NotBlank(message = "短信验证码不能为空")
    private String smsCode;
}
