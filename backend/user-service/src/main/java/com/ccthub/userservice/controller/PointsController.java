package com.ccthub.userservice.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.dto.PointsInfoDTO;
import com.ccthub.userservice.dto.UserPointsDTO;
import com.ccthub.userservice.service.PointsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 积分管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
@Tag(name = "积分管理", description = "用户积分相关接口")
public class PointsController {

    private final PointsService pointsService;

    @GetMapping("/info")
    @Operation(summary = "获取积分信息", description = "获取用户的积分信息")
    public ResponseEntity<Map<String, Object>> getPointsInfo(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        try {
            PointsInfoDTO pointsInfo = pointsService.getPointsInfo(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", pointsInfo));
        } catch (Exception e) {
            log.error("获取积分信息失败: userId={}", userId, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @GetMapping("/history")
    @Operation(summary = "查询积分流水", description = "分页查询用户的积分流水记录")
    public ResponseEntity<Map<String, Object>> getPointsHistory(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "积分来源") @RequestParam(required = false) String source,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UserPointsDTO> history = pointsService.getPointsHistory(
                    userId, source, startTime, endTime, page, size);

            Map<String, Object> result = new HashMap<>();
            result.put("content", history.getContent());
            result.put("totalElements", history.getTotalElements());
            result.put("totalPages", history.getTotalPages());
            result.put("currentPage", history.getNumber());
            result.put("pageSize", history.getSize());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result));
        } catch (Exception e) {
            log.error("查询积分流水失败: userId={}", userId, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PostMapping("/checkin")
    @Operation(summary = "每日签到", description = "用户每日签到获取积分")
    public ResponseEntity<Map<String, Object>> dailyCheckin(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        try {
            pointsService.earnPointsFromCheckin(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "签到成功，获得10积分"));
        } catch (Exception e) {
            log.error("签到失败: userId={}", userId, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PostMapping("/share")
    @Operation(summary = "分享获取积分", description = "分享内容获取积分")
    public ResponseEntity<Map<String, Object>> earnFromShare(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        try {
            pointsService.earnPointsFromShare(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "分享成功，获得5积分"));
        } catch (Exception e) {
            log.error("分享获取积分失败: userId={}", userId, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PostMapping("/exchange")
    @Operation(summary = "积分兑换商品", description = "使用积分兑换商品")
    public ResponseEntity<Map<String, Object>> exchangeProduct(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "消耗积分", required = true) @RequestParam Integer points) {
        try {
            pointsService.exchangePoints(userId, productId, points);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "兑换成功"));
        } catch (Exception e) {
            log.error("积分兑换失败: userId={}, productId={}, points={}", userId, productId, points, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @GetMapping("/calculate-deduct")
    @Operation(summary = "计算积分抵扣金额", description = "根据积分数量计算可抵扣金额")
    public ResponseEntity<Map<String, Object>> calculateDeductAmount(
            @Parameter(description = "积分数量", required = true) @RequestParam Integer points) {
        try {
            BigDecimal amount = pointsService.calculateDeductAmount(points);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "points", points,
                            "amount", amount)));
        } catch (Exception e) {
            log.error("计算积分抵扣金额失败: points={}", points, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @GetMapping("/calculate-earn")
    @Operation(summary = "计算可获得积分", description = "根据消费金额计算可获得积分")
    public ResponseEntity<Map<String, Object>> calculateEarnPoints(
            @Parameter(description = "消费金额", required = true) @RequestParam BigDecimal amount) {
        try {
            Integer points = pointsService.calculateEarnPoints(amount);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "amount", amount,
                            "points", points)));
        } catch (Exception e) {
            log.error("计算可获得积分失败: amount={}", amount, e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }
}
