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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.dto.payment.PaymentRequest;
import com.ccthub.userservice.dto.payment.PaymentResponse;
import com.ccthub.userservice.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 支付Controller（已迁移到新订单系统）
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(@Valid @RequestBody PaymentRequest request) {
        try {
            PaymentResponse response = paymentService.createPayment(request);
            return ResponseEntity.ok(success("支付订单创建成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 支付回调处理
     */
    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> paymentCallback(
            @RequestParam String paymentNo,
            @RequestParam Integer status,
            @RequestParam(required = false) String thirdPartyNo) {
        try {
            paymentService.updatePaymentStatus(paymentNo, status, thirdPartyNo);
            return ResponseEntity.ok(success("支付状态更新成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 查询支付信息
     */
    @GetMapping("/{paymentNo}")
    public ResponseEntity<Map<String, Object>> queryPayment(@PathVariable String paymentNo) {
        try {
            PaymentResponse response = paymentService.queryPayment(paymentNo);
            return ResponseEntity.ok(success("查询成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 根据订单号查询支付信息
     */
    @GetMapping("/order/{orderNo}")
    public ResponseEntity<Map<String, Object>> queryPaymentByOrderNo(@PathVariable String orderNo) {
        try {
            PaymentResponse response = paymentService.queryPaymentByOrderNo(orderNo);
            return ResponseEntity.ok(success("查询成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 关闭支付订单
     */
    @PostMapping("/{paymentNo}/close")
    public ResponseEntity<Map<String, Object>> closePayment(@PathVariable String paymentNo) {
        try {
            paymentService.closePayment(paymentNo);
            return ResponseEntity.ok(success("支付订单已关闭", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 分页查询支付列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPayments(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            Pageable pageable) {
        try {
            Page<PaymentResponse> page = paymentService.getPayments(orderNo, paymentType, status, startTime, endTime,
                    pageable);

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
     * 获取支付统计
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> stats = paymentService.getStatistics();
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
