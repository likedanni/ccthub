package com.ccthub.userservice.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.dto.SetPayPasswordRequest;
import com.ccthub.userservice.dto.WalletDTO;
import com.ccthub.userservice.dto.WalletRechargeRequest;
import com.ccthub.userservice.dto.WalletTransactionDTO;
import com.ccthub.userservice.service.WalletService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 钱包管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@Tag(name = "钱包管理", description = "用户钱包相关接口")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/info")
    @Operation(summary = "获取钱包信息", description = "获取当前用户的钱包信息")
    public ResponseEntity<Map<String, Object>> getWalletInfo(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        try {
            WalletDTO wallet = walletService.getWallet(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", wallet));
        } catch (Exception e) {
            log.error("获取钱包信息失败: userId={}", userId, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PostMapping("/recharge")
    @Operation(summary = "钱包充值", description = "创建充值订单，返回支付流水号")
    public ResponseEntity<Map<String, Object>> recharge(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Valid @RequestBody WalletRechargeRequest request) {
        try {
            String transactionNo = walletService.recharge(userId, request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "transactionNo", transactionNo,
                            "amount", request.getAmount()),
                    "message", "充值订单创建成功"));
        } catch (Exception e) {
            log.error("钱包充值失败: userId={}, request={}", userId, request, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @GetMapping("/transactions")
    @Operation(summary = "查询钱包流水", description = "分页查询用户的钱包流水记录")
    public ResponseEntity<Map<String, Object>> getTransactions(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "交易类型: 1-充值, 2-消费, 3-退款") @RequestParam(required = false) Integer transactionType,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        try {
            Page<WalletTransactionDTO> transactions = walletService.getTransactions(
                    userId, transactionType, startTime, endTime, page, size);

            Map<String, Object> result = new HashMap<>();
            result.put("content", transactions.getContent());
            result.put("totalElements", transactions.getTotalElements());
            result.put("totalPages", transactions.getTotalPages());
            result.put("currentPage", transactions.getNumber());
            result.put("pageSize", transactions.getSize());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result));
        } catch (Exception e) {
            log.error("查询钱包流水失败: userId={}", userId, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PostMapping("/pay-password/set")
    @Operation(summary = "设置支付密码", description = "首次设置支付密码")
    public ResponseEntity<Map<String, Object>> setPayPassword(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Valid @RequestBody SetPayPasswordRequest request) {
        try {
            walletService.setPayPassword(userId, request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "支付密码设置成功"));
        } catch (Exception e) {
            log.error("设置支付密码失败: userId={}", userId, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PostMapping("/pay-password/change")
    @Operation(summary = "修改支付密码", description = "修改已设置的支付密码")
    public ResponseEntity<Map<String, Object>> changePayPassword(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "原支付密码", required = true) @RequestParam String oldPassword,
            @Valid @RequestBody SetPayPasswordRequest request) {
        try {
            walletService.changePayPassword(userId, oldPassword, request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "支付密码修改成功"));
        } catch (Exception e) {
            log.error("修改支付密码失败: userId={}", userId, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PostMapping("/pay-password/verify")
    @Operation(summary = "验证支付密码", description = "验证用户输入的支付密码是否正确")
    public ResponseEntity<Map<String, Object>> verifyPayPassword(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "支付密码", required = true) @RequestParam String payPassword) {
        try {
            boolean valid = walletService.verifyPayPassword(userId, payPassword);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("valid", valid)));
        } catch (Exception e) {
            log.error("验证支付密码失败: userId={}", userId, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取钱包列表", description = "分页查询钱包列表,支持筛选")
    public ResponseEntity<Map<String, Object>> getWalletList(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "手机号") @RequestParam(required = false) String phone,
            @Parameter(description = "钱包状态: 0-冻结, 1-正常") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        try {
            Page<WalletDTO> wallets = walletService.getWalletList(userId, phone, status, page, size);

            Map<String, Object> result = new HashMap<>();
            result.put("content", wallets.getContent());
            result.put("totalElements", wallets.getTotalElements());
            result.put("totalPages", wallets.getTotalPages());
            result.put("currentPage", wallets.getNumber());
            result.put("pageSize", wallets.getSize());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result));
        } catch (Exception e) {
            log.error("查询钱包列表失败", e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }
}
