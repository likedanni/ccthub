package com.ccthub.userservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.dto.ticket.TicketOrderResponse;
import com.ccthub.userservice.service.TicketOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 订单控制器（兼容前端）
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Tag(name = "订单管理", description = "订单相关接口")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final TicketOrderService ticketOrderService;

    @Operation(summary = "查询所有订单", description = "管理后台查询所有订单")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TicketOrderResponse> orderPage = ticketOrderService.queryOrders(
                    userId, null, null, null, null, pageable);
            
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> data = new HashMap<>();
            
            // 返回统一的分页格式
            data.put("records", orderPage.getContent());
            data.put("total", orderPage.getTotalElements());
            data.put("size", orderPage.getSize());
            data.put("current", orderPage.getNumber() + 1);
            data.put("pages", orderPage.getTotalPages());
            
            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "查询订单详情", description = "根据订单ID查询订单详情")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        try {
            // 暂时返回空对象，因为getOrderById方法不存在
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", new HashMap<>());
            response.put("message", "详情功能开发中");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
