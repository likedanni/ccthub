package com.ccthub.userservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.entity.SeckillEvent;
import com.ccthub.userservice.service.SeckillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 秒杀活动管理控制器
 * 提供秒杀活动的创建、查询、更新、删除以及秒杀购买等功能
 */
@Tag(name = "秒杀活动管理", description = "秒杀活动相关接口，包括秒杀活动列表、创建、更新、删除、状态管理以及秒杀购买等功能")
@RestController
@RequestMapping("/api/seckills")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillService seckillService;

    /**
     * 创建秒杀活动
     */
    @Operation(summary = "创建秒杀活动", description = "新增一个秒杀活动，需要设置活动名称、票种、价格、库存、开始结束时间等信息")
    @PostMapping
    public ResponseEntity<?> createSeckill(
            @Parameter(description = "秒杀活动信息", required = true) @RequestBody SeckillEvent seckillEvent) {
        try {
            SeckillEvent created = seckillService.createSeckill(seckillEvent);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                    Map.of("error", e.getMessage(), "cause", e.getCause() != null ? e.getCause().getMessage() : ""));
        }
    }

    /**
     * 更新秒杀活动
     */
    @Operation(summary = "更新秒杀活动", description = "修改指定秒杀活动的基本信息")
    @PutMapping("/{id}")
    public ResponseEntity<SeckillEvent> updateSeckill(
            @Parameter(description = "秒杀活动ID", required = true) @PathVariable Long id,
            @Parameter(description = "秒杀活动信息", required = true) @RequestBody SeckillEvent seckillEvent) {
        SeckillEvent updated = seckillService.updateSeckill(id, seckillEvent);
        return ResponseEntity.ok(updated);
    }

    /**
     * 修改秒杀状态
     */
    @Operation(summary = "修改秒杀状态", description = "修改秒杀活动的状态：0-未开始，1-进行中，2-已结束，3-已取消")
    @PutMapping("/{id}/status")
    public ResponseEntity<SeckillEvent> toggleStatus(
            @Parameter(description = "秒杀活动ID", required = true) @PathVariable Long id,
            @Parameter(description = "秒杀状态：0-未开始，1-进行中，2-已结束，3-已取消", required = true) @RequestParam Integer status) {
        SeckillEvent updated = seckillService.toggleStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除秒杀活动
     */
    @Operation(summary = "删除秒杀活动", description = "删除指定的秒杀活动")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeckill(
            @Parameter(description = "秒杀活动ID", required = true) @PathVariable Long id) {
        seckillService.deleteSeckill(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取秒杀详情
     */
    @Operation(summary = "获取秒杀详情", description = "根据秒杀活动ID查询详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<SeckillEvent> getSeckillDetail(
            @Parameter(description = "秒杀活动ID", required = true) @PathVariable Long id) {
        SeckillEvent seckill = seckillService.getSeckillDetail(id);
        return ResponseEntity.ok(seckill);
    }

    /**
     * 分页查询秒杀列表
     */
    @Operation(summary = "分页查询秒杀列表", description = "查询秒杀活动列表，支持按状态筛选")
    @GetMapping
    public ResponseEntity<Page<SeckillEvent>> getSeckillList(
            @Parameter(description = "秒杀状态：0-未开始，1-进行中，2-已结束，3-已取消") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码（从0开始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        Page<SeckillEvent> seckills = seckillService.getSeckillList(status, page, size);
        return ResponseEntity.ok(seckills);
    }

    /**
     * 查询进行中的秒杀
     */
    @Operation(summary = "查询进行中的秒杀", description = "获取当前正在进行中的所有秒杀活动列表")
    @GetMapping("/ongoing")
    public ResponseEntity<List<SeckillEvent>> getOngoingSeckills() {
        List<SeckillEvent> seckills = seckillService.getOngoingSeckills();
        return ResponseEntity.ok(seckills);
    }

    /**
     * 查询即将开始的秒杀
     */
    @Operation(summary = "查询即将开始的秒杀", description = "获取即将开始的秒杀活动列表，按开始时间排序")
    @GetMapping("/upcoming")
    public ResponseEntity<List<SeckillEvent>> getUpcomingSeckills(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") int limit) {
        List<SeckillEvent> seckills = seckillService.getUpcomingSeckills(limit);
        return ResponseEntity.ok(seckills);
    }

    /**
     * 处理秒杀购买
     */
    @Operation(summary = "处理秒杀购买", description = "用户参与秒杀活动，购买指定数量的秒杀商品")
    @PostMapping("/{id}/purchase")
    public ResponseEntity<Boolean> processPurchase(
            @Parameter(description = "秒杀活动ID", required = true) @PathVariable Long id,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "购买数量") @RequestParam(defaultValue = "1") Integer quantity) {
        boolean success = seckillService.processPurchase(id, userId, quantity);
        return ResponseEntity.ok(success);
    }
}
