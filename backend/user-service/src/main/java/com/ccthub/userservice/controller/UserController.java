package com.ccthub.userservice.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.dto.ApiResponse;
import com.ccthub.userservice.dto.ChangePasswordRequest;
import com.ccthub.userservice.dto.LoginRequest;
import com.ccthub.userservice.dto.RegisterRequest;
import com.ccthub.userservice.dto.RegisterResponse;
import com.ccthub.userservice.dto.UpdateProfileRequest;
import com.ccthub.userservice.dto.UserProfileResponse;
import com.ccthub.userservice.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Register error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<RegisterResponse> login(@RequestBody LoginRequest request) {
        try {
            RegisterResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login error", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        var user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * 获取用户详细信息
     */
    @GetMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable Long id) {
        try {
            UserProfileResponse profile = userService.getUserProfile(id);
            return ResponseEntity.ok(ApiResponse.success(profile));
        } catch (Exception e) {
            logger.error("Get profile error", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    /**
     * 更新用户个人信息
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest request) {
        try {
            UserProfileResponse profile = userService.updateProfile(id, request);
            return ResponseEntity.ok(ApiResponse.success("更新成功", profile));
        } catch (Exception e) {
            logger.error("Update profile error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 修改登录密码
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(id, request);
            return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
        } catch (Exception e) {
            logger.error("Change password error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 设置/修改支付密码
     */
    @PostMapping("/{id}/payment-password")
    public ResponseEntity<ApiResponse<Void>> setPaymentPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String paymentPassword = request.get("paymentPassword");
            if (paymentPassword == null || paymentPassword.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "支付密码不能为空"));
            }
            userService.setPaymentPassword(id, paymentPassword);
            return ResponseEntity.ok(ApiResponse.success("支付密码设置成功", null));
        } catch (Exception e) {
            logger.error("Set payment password error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 验证支付密码
     */
    @PostMapping("/{id}/verify-payment-password")
    public ResponseEntity<ApiResponse<Boolean>> verifyPaymentPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String paymentPassword = request.get("paymentPassword");
            if (paymentPassword == null || paymentPassword.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "支付密码不能为空"));
            }
            boolean isValid = userService.verifyPaymentPassword(id, paymentPassword);
            return ResponseEntity.ok(ApiResponse.success(isValid));
        } catch (Exception e) {
            logger.error("Verify payment password error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
