package com.ccthub.userservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.service.VerificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 核销Controller（已迁移到新订单系统）
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Tag(name = "核销管理", description = "电子票券核销相关接口，包括核销码查询、单个核销、批量核销和核销记录查询等功能")
@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    /**
     * 查询核销码信息
     */
    @GetMapping("/info/{verificationCode}")
    public ResponseEntity<Map<String, Object>> getVerificationInfo(@PathVariable String verificationCode) {
        try {
            Map<String, Object> info = verificationService.getVerificationInfo(verificationCode);
            return ResponseEntity.ok(success("查询成功", info));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 核销电子票券
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyTicket(
            @RequestParam String verificationCode,
            @RequestParam Long staffId) {
        try {
            Map<String, Object> result = verificationService.verifyTicket(verificationCode, staffId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 批量核销
     */
    @PostMapping("/batch-verify")
    public ResponseEntity<Map<String, Object>> batchVerify(
            @RequestBody List<String> verificationCodes,
            @RequestParam Long staffId) {
        try {
            Map<String, Object> result = verificationService.batchVerify(verificationCodes, staffId);
            return ResponseEntity.ok(success("批量核销完成", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 查询订单核销统计
     */
    @GetMapping("/stats/{orderNo}")
    public ResponseEntity<Map<String, Object>> getOrderVerificationStats(@PathVariable String orderNo) {
        try {
            Map<String, Object> stats = verificationService.getOrderVerificationStats(orderNo);
            return ResponseEntity.ok(success("查询成功", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 查询核销记录列表
     */
    @GetMapping("/records/{orderNo}")
    public ResponseEntity<Map<String, Object>> getVerificationRecords(@PathVariable String orderNo) {
        try {
            List<Map<String, Object>> records = verificationService.getVerificationRecords(orderNo);
            return ResponseEntity.ok(success("查询成功", records));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 成功响应
     */
    private Map<String, Object> success(String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("data", data);
        return result;
    }

    /**
     * 错误响应
     */
    private Map<String, Object> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        result.put("data", null);
        return result;
    }
}
