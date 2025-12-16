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
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.dto.OrderRequest;
import com.ccthub.userservice.dto.OrderResponse;
import com.ccthub.userservice.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 订单控制器
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Tag(name = "订单管理", description = "订单相关接口")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单", description = "用户下单购票")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody OrderRequest request) {
        try {
            OrderResponse order = orderService.createOrder(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "订单创建成功");
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "支付订单", description = "支付订单")
    @PostMapping("/{id}/pay")
    public ResponseEntity<Map<String, Object>> payOrder(@PathVariable Long id) {
        try {
            OrderResponse order = orderService.payOrder(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "支付成功");
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "取消订单", description = "取消未支付的订单")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "订单已取消");
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
            OrderResponse order = orderService.getOrderDetail(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "根据订单号查询", description = "根据订单号查询订单")
    @GetMapping("/by-no/{orderNo}")
    public ResponseEntity<Map<String, Object>> getOrderByOrderNo(@PathVariable String orderNo) {
        try {
            OrderResponse order = orderService.getOrderByOrderNo(orderNo);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "查询用户订单", description = "查询用户的所有订单")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserOrders(@PathVariable Long userId) {
        try {
            List<OrderResponse> orders = orderService.getUserOrders(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "查询所有订单", description = "管理后台查询所有订单")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        try {
            List<OrderResponse> orders = orderService.getAllOrders();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
