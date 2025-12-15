package com.ccthub.userservice.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import com.ccthub.userservice.dto.payment.PaymentRequest;
import com.ccthub.userservice.dto.payment.PaymentResponse;
import com.ccthub.userservice.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付管理Controller
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "支付管理", description = "支付订单管理接口")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping
    @Operation(summary = "创建支付订单", description = "创建新的支付订单")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 查询支付信息
     */
    @GetMapping("/{paymentNo}")
    @Operation(summary = "查询支付信息", description = "根据支付单号查询支付信息")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentNo) {
        PaymentResponse response = paymentService.queryPayment(paymentNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 关闭支付订单
     */
    @PutMapping("/{paymentNo}/close")
    @Operation(summary = "关闭支付订单", description = "关闭待支付订单")
    public ResponseEntity<Void> closePayment(@PathVariable String paymentNo) {
        paymentService.closePayment(paymentNo);
        return ResponseEntity.ok().build();
    }

    /**
     * 分页查询支付列表
     */
    @GetMapping
    @Operation(summary = "查询支付列表", description = "分页查询支付列表，支持多条件筛选")
    public ResponseEntity<Page<PaymentResponse>> getPayments(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @PageableDefault(size = 10, sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PaymentResponse> page = paymentService.getPayments(orderNo, paymentType, status, startTime, endTime,
                pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 获取支付统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "支付统计", description = "获取支付统计数据")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = paymentService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * 支付回调（第三方支付平台回调）
     */
    @PostMapping("/callback/{paymentNo}")
    @Operation(summary = "支付回调", description = "第三方支付平台回调接口")
    public ResponseEntity<String> paymentCallback(
            @PathVariable String paymentNo,
            @RequestParam Integer status,
            @RequestParam(required = false) String thirdPartyNo) {
        try {
            paymentService.updatePaymentStatus(paymentNo, status, thirdPartyNo);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            log.error("支付回调处理失败", e);
            return ResponseEntity.ok("FAIL");
        }
    }
}
