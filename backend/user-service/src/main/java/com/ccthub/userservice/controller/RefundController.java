package com.ccthub.userservice.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.dto.payment.RefundAuditRequest;
import com.ccthub.userservice.dto.payment.RefundRequest;
import com.ccthub.userservice.dto.payment.RefundResponse;
import com.ccthub.userservice.service.RefundService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 退款Controller（已迁移到新订单系统）
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    /**
     * 创建退款申请
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRefund(@Valid @RequestBody RefundRequest request) {
        try {
            RefundResponse response = refundService.createRefund(request);
            return ResponseEntity.ok(success("退款申请创建成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 审核退款申请
     */
    @PutMapping("/audit")
    public ResponseEntity<Map<String, Object>> auditRefund(
            @Valid @RequestBody RefundAuditRequest request) {
        try {
            RefundResponse response = refundService.auditRefund(request);
            return ResponseEntity.ok(success("退款审核成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 处理退款
     */
    @PostMapping("/{refundNo}/process")
    public ResponseEntity<Map<String, Object>> processRefund(@PathVariable String refundNo) {
        try {
            refundService.processRefund(refundNo);
            return ResponseEntity.ok(success("退款处理成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 查询退款详情
     */
    @GetMapping("/{refundNo}")
    public ResponseEntity<Map<String, Object>> queryRefund(@PathVariable String refundNo) {
        try {
            RefundResponse response = refundService.queryRefund(refundNo);
            return ResponseEntity.ok(success("查询成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 分页查询退款列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRefunds(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            Pageable pageable) {
        try {
            Page<RefundResponse> page = refundService.getRefunds(orderNo, userId, status, startTime, endTime, pageable);

            // 转换为统一的分页格式
            Map<String, Object> data = new HashMap<>();
            data.put("records", page.getContent());
            data.put("total", page.getTotalElements());
            data.put("size", page.getSize());
            data.put("current", page.getNumber() + 1);
            data.put("pages", page.getTotalPages());

            return ResponseEntity.ok(success("查询成功", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 获取退款统计
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> stats = refundService.getStatistics();
            return ResponseEntity.ok(success("查询成功", stats));
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
