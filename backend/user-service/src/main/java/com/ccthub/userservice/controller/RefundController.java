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

import com.ccthub.userservice.dto.payment.RefundAuditRequest;
import com.ccthub.userservice.dto.payment.RefundRequest;
import com.ccthub.userservice.dto.payment.RefundResponse;
import com.ccthub.userservice.service.RefundService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 退款管理Controller
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Slf4j
@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
@Tag(name = "退款管理", description = "订单退款管理接口")
public class RefundController {

    private final RefundService refundService;

    /**
     * 创建退款申请
     */
    @PostMapping
    @Operation(summary = "创建退款申请", description = "用户提交退款申请")
    public ResponseEntity<RefundResponse> createRefund(@Valid @RequestBody RefundRequest request) {
        RefundResponse response = refundService.createRefund(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 审核退款申请
     */
    @PutMapping("/audit")
    @Operation(summary = "审核退款申请", description = "管理员审核退款申请")
    public ResponseEntity<RefundResponse> auditRefund(@Valid @RequestBody RefundAuditRequest request) {
        RefundResponse response = refundService.auditRefund(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 查询退款信息
     */
    @GetMapping("/{refundNo}")
    @Operation(summary = "查询退款信息", description = "根据退款单号查询退款信息")
    public ResponseEntity<RefundResponse> getRefund(@PathVariable String refundNo) {
        RefundResponse response = refundService.queryRefund(refundNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 分页查询退款列表
     */
    @GetMapping
    @Operation(summary = "查询退款列表", description = "分页查询退款列表，支持多条件筛选")
    public ResponseEntity<Page<RefundResponse>> getRefunds(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RefundResponse> page = refundService.getRefunds(orderNo, userId, status, startTime, endTime, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 获取退款统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "退款统计", description = "获取退款统计数据")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = refundService.getStatistics();
        return ResponseEntity.ok(stats);
    }
}
