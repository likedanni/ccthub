package com.ccthub.userservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.entity.Coupon;
import com.ccthub.userservice.entity.UserCoupon;
import com.ccthub.userservice.service.CouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 优惠券Controller
 */
@Tag(name = "优惠券管理", description = "优惠券相关接口，包括优惠券列表、创建、发放、使用、查询和统计等功能")
@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 获取优惠券列表
     * 
     * @param status 状态筛选（可选）: 0-未开始, 1-发放中, 2-已结束, 3-停用
     * @param type   类型筛选（可选）: 1-满减券, 2-折扣券, 3-代金券
     * @param page   页码（从0开始）
     * @param size   每页大小
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getCouponList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Coupon> couponPage = couponService.getCouponList(status, type, page, size);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("records", couponPage.getContent());
        data.put("total", couponPage.getTotalElements());
        data.put("size", couponPage.getSize());
        data.put("current", couponPage.getNumber() + 1);
        data.put("pages", couponPage.getTotalPages());

        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取优惠券详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCouponById(@PathVariable Long id) {
        try {
            Coupon coupon = couponService.getCouponById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", coupon);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 创建优惠券
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCoupon(@RequestBody Coupon coupon) {
        try {
            Coupon created = couponService.createCoupon(coupon);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", created);
            response.put("message", "优惠券创建成功");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新优惠券
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCoupon(
            @PathVariable Long id,
            @RequestBody Coupon coupon) {
        try {
            Coupon updated = couponService.updateCoupon(id, coupon);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updated);
            response.put("message", "优惠券更新成功");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新优惠券状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateCouponStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            Coupon updated = couponService.updateCouponStatus(id, status);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updated);
            response.put("message", "状态更新成功");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取用户优惠券列表
     * 
     * @param userId 用户ID（可选，管理后台可查询所有用户）
     * @param status 状态筛选（可选）: 0-未使用, 1-已使用, 2-已过期
     * @param page   页码（从0开始）
     * @param size   每页大小
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserCoupons(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserCoupon> userCouponPage = couponService.getUserCoupons(userId, status, page, size);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("records", userCouponPage.getContent());
        data.put("total", userCouponPage.getTotalElements());
        data.put("size", userCouponPage.getSize());
        data.put("current", userCouponPage.getNumber() + 1);
        data.put("pages", userCouponPage.getTotalPages());

        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    /**
     * 发放优惠券给用户
     */
    @PostMapping("/{id}/grant")
    public ResponseEntity<Map<String, Object>> grantCoupon(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            UserCoupon userCoupon = couponService.grantCoupon(id, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", userCoupon);
            response.put("message", "优惠券发放成功");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 使用优惠券
     */
    @PostMapping("/user/{id}/use")
    public ResponseEntity<Map<String, Object>> useCoupon(
            @PathVariable Long id,
            @RequestParam String orderNo) {
        try {
            UserCoupon userCoupon = couponService.useCoupon(id, orderNo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", userCoupon);
            response.put("message", "优惠券使用成功");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
