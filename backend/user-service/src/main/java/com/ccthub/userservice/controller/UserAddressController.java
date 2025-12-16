package com.ccthub.userservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.entity.UserAddress;
import com.ccthub.userservice.service.UserAddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 用户地址控制器
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Tag(name = "用户地址管理", description = "用户收货地址相关接口")
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @Operation(summary = "创建地址", description = "添加新的收货地址")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAddress(@RequestBody UserAddress address) {
        try {
            UserAddress created = userAddressService.createAddress(address);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "地址创建成功");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "更新地址", description = "修改收货地址信息")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @PathVariable Long id,
            @RequestBody UserAddress address) {
        try {
            UserAddress updated = userAddressService.updateAddress(id, address);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "地址更新成功");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "删除地址", description = "删除收货地址")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAddress(@PathVariable Long id) {
        try {
            userAddressService.deleteAddress(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "地址删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "设置默认地址", description = "将指定地址设为默认地址")
    @PutMapping("/{id}/default")
    public ResponseEntity<Map<String, Object>> setDefaultAddress(@PathVariable Long id) {
        try {
            UserAddress address = userAddressService.setDefaultAddress(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "默认地址设置成功");
            response.put("data", address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "查询用户地址列表", description = "获取用户所有收货地址")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserAddresses(@PathVariable Long userId) {
        try {
            List<UserAddress> addresses = userAddressService.getUserAddresses(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", addresses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "查询默认地址", description = "获取用户默认收货地址")
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<Map<String, Object>> getDefaultAddress(@PathVariable Long userId) {
        try {
            UserAddress address = userAddressService.getDefaultAddress(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "查询地址详情", description = "根据ID查询地址详情")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAddress(@PathVariable Long id) {
        try {
            UserAddress address = userAddressService.getAddress(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
