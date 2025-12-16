package com.ccthub.userservice.controller;

import com.ccthub.userservice.dto.ticket.TicketOrderCreateRequest;
import com.ccthub.userservice.dto.ticket.TicketOrderResponse;
import com.ccthub.userservice.service.TicketOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门票订单Controller
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@RestController
@RequestMapping("/api/ticket-orders")
@RequiredArgsConstructor
public class TicketOrderController {

    private final TicketOrderService ticketOrderService;

    /**
     * 创建门票订单
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTicketOrder(
            @Valid @RequestBody TicketOrderCreateRequest request) {
        try {
            TicketOrderResponse response = ticketOrderService.createTicketOrder(request);
            return ResponseEntity.ok(success("订单创建成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 查询门票订单详情
     */
    @GetMapping("/{orderNo}")
    public ResponseEntity<Map<String, Object>> getTicketOrder(@PathVariable String orderNo) {
        try {
            TicketOrderResponse response = ticketOrderService.getTicketOrderByOrderNo(orderNo);
            return ResponseEntity.ok(success("查询成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 查询用户门票订单列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserTicketOrders(@PathVariable Long userId) {
        try {
            List<TicketOrderResponse> responses = ticketOrderService.getUserTicketOrders(userId);
            return ResponseEntity.ok(success("查询成功", responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 支付门票订单
     */
    @PostMapping("/{orderNo}/pay")
    public ResponseEntity<Map<String, Object>> payTicketOrder(
            @PathVariable String orderNo,
            @RequestParam String paymentMethod) {
        try {
            TicketOrderResponse response = ticketOrderService.payTicketOrder(orderNo, paymentMethod);
            return ResponseEntity.ok(success("支付成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /**
     * 取消门票订单
     */
    @PostMapping("/{orderNo}/cancel")
    public ResponseEntity<Map<String, Object>> cancelTicketOrder(@PathVariable String orderNo) {
        try {
            ticketOrderService.cancelTicketOrder(orderNo);
            return ResponseEntity.ok(success("订单已取消", null));
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
