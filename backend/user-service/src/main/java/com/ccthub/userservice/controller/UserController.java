package com.ccthub.userservice.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.dto.ApiResponse;
import com.ccthub.userservice.dto.ChangePasswordRequest;
import com.ccthub.userservice.dto.DashboardStatsResponse;
import com.ccthub.userservice.dto.LoginRequest;
import com.ccthub.userservice.dto.PageResponse;
import com.ccthub.userservice.dto.RegisterRequest;
import com.ccthub.userservice.dto.RegisterResponse;
import com.ccthub.userservice.dto.UpdateProfileRequest;
import com.ccthub.userservice.dto.UserListResponse;
import com.ccthub.userservice.dto.UserProfileResponse;
import com.ccthub.userservice.model.User;
import com.ccthub.userservice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "用户管理", description = "用户注册、登录、资料管理、密码管理等API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Operation(summary = "用户注册", description = "使用手机号和密码注册新用户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "注册成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "注册失败,手机号已存在或参数错误")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Parameter(description = "注册请求信息", required = true) @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Register error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "用户登录", description = "使用手机号和密码登录,返回JWT token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public ResponseEntity<RegisterResponse> login(
            @Parameter(description = "登录请求信息", required = true) @RequestBody LoginRequest request) {
        try {
            RegisterResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login error", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "获取用户基本信息", description = "根据用户ID获取基本信息")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        var user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "获取用户详细资料", description = "获取用户的完整资料信息,包括会员等级、积分、余额等")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/{id}/profile")
    public ResponseEntity<com.ccthub.userservice.dto.ApiResponse<UserProfileResponse>> getUserProfile(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        try {
            UserProfileResponse profile = userService.getUserProfile(id);
            return ResponseEntity.ok(com.ccthub.userservice.dto.ApiResponse.success(profile));
        } catch (Exception e) {
            logger.error("Get profile error", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(com.ccthub.userservice.dto.ApiResponse.error(404, e.getMessage()));
        }
    }

    @Operation(summary = "更新用户资料", description = "更新用户的昵称、头像等个人信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "更新失败")
    })
    @PutMapping("/{id}/profile")
    public ResponseEntity<com.ccthub.userservice.dto.ApiResponse<UserProfileResponse>> updateProfile(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "要更新的资料信息", required = true) @RequestBody UpdateProfileRequest request) {
        try {
            UserProfileResponse profile = userService.updateProfile(id, request);
            return ResponseEntity.ok(com.ccthub.userservice.dto.ApiResponse.success("更新成功", profile));
        } catch (Exception e) {
            logger.error("Update profile error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(com.ccthub.userservice.dto.ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "修改登录密码", description = "修改用户的登录密码,需要提供旧密码验证")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "密码修改成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "旧密码错误或新密码不符合要求")
    })
    @PostMapping("/{id}/change-password")
    public ResponseEntity<com.ccthub.userservice.dto.ApiResponse<Void>> changePassword(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "旧密码和新密码", required = true) @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(id, request);
            return ResponseEntity.ok(com.ccthub.userservice.dto.ApiResponse.success("密码修改成功", null));
        } catch (Exception e) {
            logger.error("Change password error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(com.ccthub.userservice.dto.ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "设置支付密码", description = "设置或修改用户的6位数字支付密码")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "设置成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "设置失败")
    })
    @PostMapping("/{id}/payment-password")
    public ResponseEntity<com.ccthub.userservice.dto.ApiResponse<Void>> setPaymentPassword(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "支付密码(paymentPassword字段)", required = true) @RequestBody Map<String, String> request) {
        try {
            String paymentPassword = request.get("paymentPassword");
            if (paymentPassword == null || paymentPassword.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(com.ccthub.userservice.dto.ApiResponse.error(400, "支付密码不能为空"));
            }
            userService.setPaymentPassword(id, paymentPassword);
            return ResponseEntity.ok(com.ccthub.userservice.dto.ApiResponse.success("支付密码设置成功", null));
        } catch (Exception e) {
            logger.error("Set payment password error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(com.ccthub.userservice.dto.ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "验证支付密码", description = "验证用户输入的支付密码是否正确")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "验证完成,返回true/false"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "未设置支付密码或参数错误")
    })
    @PostMapping("/{id}/verify-payment-password")
    public ResponseEntity<com.ccthub.userservice.dto.ApiResponse<Boolean>> verifyPaymentPassword(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "支付密码(paymentPassword字段)", required = true) @RequestBody Map<String, String> request) {
        try {
            String paymentPassword = request.get("paymentPassword");
            if (paymentPassword == null || paymentPassword.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(com.ccthub.userservice.dto.ApiResponse.error(400, "支付密码不能为空"));
            }
            boolean isValid = userService.verifyPaymentPassword(id, paymentPassword);
            return ResponseEntity.ok(com.ccthub.userservice.dto.ApiResponse.success("验证完成", isValid));
        } catch (Exception e) {
            logger.error("Verify payment password error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(com.ccthub.userservice.dto.ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "获取仪表盘统计数据", description = "获取管理后台仪表盘的统计数据")
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        try {
            DashboardStatsResponse stats = userService.getDashboardStats();
            return ResponseEntity.ok(ApiResponse.success("获取成功", stats));
        } catch (Exception e) {
            logger.error("Get dashboard stats error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取统计数据失败"));
        }
    }

    @Operation(summary = "获取用户列表", description = "分页获取用户列表,支持按手机号和状态筛选")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<UserListResponse>>> getUserList(
            @Parameter(description = "页码(从0开始)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "手机号筛选") @RequestParam(required = false) String phone,
            @Parameter(description = "状态筛选(ACTIVE/INACTIVE)") @RequestParam(required = false) String status) {
        try {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "registerTime"));
            Page<User> userPage = userService.getUserList(phone, status, pageable);

            PageResponse<UserListResponse> response = userService.convertToPageResponse(userPage);
            return ResponseEntity.ok(ApiResponse.success("获取成功", response));
        } catch (Exception e) {
            logger.error("Get user list error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取用户列表失败"));
        }
    }

    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "新状态(ACTIVE/INACTIVE)", required = true) @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");
            if (newStatus == null || newStatus.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "状态不能为空"));
            }
            userService.updateUserStatus(id, newStatus);
            return ResponseEntity.ok(ApiResponse.success("状态更新成功", null));
        } catch (Exception e) {
            logger.error("Update user status error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
