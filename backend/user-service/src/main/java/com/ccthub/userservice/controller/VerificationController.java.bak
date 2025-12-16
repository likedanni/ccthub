package com.ccthub.userservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.service.VerificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 核销控制器
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Tag(name = "票券核销", description = "电子票券核销接口")
@RestController
@RequestMapping("/api/verifications")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @Operation(summary = "查询核销码信息", description = "根据核销码查询票券信息")
    @GetMapping("/{verificationCode}")
    public ResponseEntity<Map<String, Object>> getVerificationInfo(@PathVariable String verificationCode) {
        try {
            Map<String, Object> info = verificationService.getVerificationInfo(verificationCode);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", info);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "核销票券", description = "核销电子票券")
    @PostMapping("/{verificationCode}/verify")
    public ResponseEntity<Map<String, Object>> verifyTicket(
            @PathVariable String verificationCode,
            @RequestParam Long staffId) {
        try {
            Map<String, Object> result = verificationService.verifyTicket(verificationCode, staffId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "批量核销", description = "批量核销订单中的所有票券")
    @PostMapping("/batch/{orderId}")
    public ResponseEntity<Map<String, Object>> batchVerify(
            @PathVariable Long orderId,
            @RequestParam Long staffId) {
        try {
            Map<String, Object> result = verificationService.batchVerify(orderId, staffId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
