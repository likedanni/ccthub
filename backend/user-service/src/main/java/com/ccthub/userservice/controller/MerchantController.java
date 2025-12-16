package com.ccthub.userservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.ccthub.userservice.entity.Merchant;
import com.ccthub.userservice.service.MerchantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 商户管理控制器
 * 提供商户的CRUD、审核、启停用等功能
 */
@Tag(name = "商户管理", description = "商户管理相关接口，包括商户列表、入驻审核、商户详情、商户启停用等功能")
@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    /**
     * 创建商户
     * POST /api/merchants
     */
    @PostMapping
    public ResponseEntity<?> createMerchant(@RequestBody Merchant merchant) {
        try {
            Merchant created = merchantService.createMerchant(merchant);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage(),
                    "cause", e.getCause() != null ? e.getCause().getMessage() : ""));
        }
    }

    /**
     * 更新商户信息
     * PUT /api/merchants/{id}
     */
    @Operation(summary = "更新商户信息", description = "修改指定商户的基本信息")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMerchant(
            @Parameter(description = "商户ID", required = true) @PathVariable Long id,
            @Parameter(description = "商户信息", required = true) @RequestBody Merchant merchant) {
        try {
            Merchant updated = merchantService.updateMerchant(id, merchant);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage(),
                    "cause", e.getCause() != null ? e.getCause().getMessage() : ""));
        }
    }

    /**
     * 审核商户
     * PUT /api/merchants/{id}/audit
     * Body: { "auditStatus": 1 } // 1-通过, 2-拒绝
     */
    @Operation(summary = "审核商户", description = "审核商户入驻申请，设置审核状态为通过(1)或拒绝(2)")
    @PutMapping("/{id}/audit")
    public ResponseEntity<?> auditMerchant(
            @Parameter(description = "商户ID", required = true) @PathVariable Long id,
            @Parameter(description = "审核信息，包含 auditStatus 字段：1-通过，2-拒绝", required = true) @RequestBody Map<String, Integer> request) {
        try {
            Integer auditStatus = request.get("auditStatus");
            Merchant updated = merchantService.auditMerchant(id, auditStatus);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()));
        }
    }

    /**
     * 启用/停用商户
     * PUT /api/merchants/{id}/status
     * Body: { "status": 1 } // 1-正常, 0-停用
     */
    @Operation(summary = "启用/停用商户", description = "切换商户状态，设置为正常(1)或停用(0)")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(
            @Parameter(description = "商户ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态信息，包含 status 字段：1-正常，0-停用", required = true) @RequestBody Map<String, Integer> request) {
        try {
            Integer status = request.get("status");
            Merchant updated = merchantService.toggleStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()));
        }
    }

    /**
     * 删除商户
     * DELETE /api/merchants/{id}
     */
    @Operation(summary = "删除商户", description = "删除指定商户（硬删除）")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMerchant(
            @Parameter(description = "商户ID", required = true) @PathVariable Long id) {
        try {
            merchantService.deleteMerchant(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()));
        }
    }

    /**
     * 获取商户详情
     * GET /api/merchants/{id}
     */
    @Operation(summary = "获取商户详情", description = "根据商户ID查询商户详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMerchantDetail(
            @Parameter(description = "商户ID", required = true) @PathVariable Long id) {
        try {
            Merchant merchant = merchantService.getMerchantDetail(id);
            return ResponseEntity.ok(merchant);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(Map.of(
                    "error", "商户不存在"));
        }
    }

    /**
     * 分页查询商户列表（支持多条件筛选）
     * GET
     * /api/merchants?name=xxx&type=1&cooperationType=1&auditStatus=0&status=1&page=0&size=10
     */
    @Operation(summary = "分页查询商户列表", description = "支持按商户名称、类型、合作类型、审核状态、启停用状态等多条件筛选查询")
    @GetMapping
    public ResponseEntity<?> getMerchantList(
            @Parameter(description = "商户名称（模糊匹配）") @RequestParam(required = false) String name,
            @Parameter(description = "商户类型：1-景区，2-餐饮，3-文创，4-生鲜便利") @RequestParam(required = false) Integer type,
            @Parameter(description = "合作类型：1-直营，2-联营，3-加盟") @RequestParam(required = false) Integer cooperationType,
            @Parameter(description = "审核状态：0-待审核，1-已通过，2-已拒绝") @RequestParam(required = false) Integer auditStatus,
            @Parameter(description = "启停用状态：0-停用，1-正常") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码（从0开始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Merchant> merchants = merchantService.getMerchantList(
                    name, type, cooperationType, auditStatus, status, page, size);
            return ResponseEntity.ok(merchants);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()));
        }
    }

    /**
     * 获取待审核商户列表
     * GET /api/merchants/pending
     */
    @Operation(summary = "获取待审核商户列表", description = "查询所有待审核状态(auditStatus=0)的商户列表，支持分页")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingMerchants(
            @Parameter(description = "页码（从0开始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Merchant> merchants = merchantService.getPendingMerchants(page, size);
            return ResponseEntity.ok(merchants);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()));
        }
    }

    /**
     * 获取商户统计数据
     * GET /api/merchants/statistics
     */
    @Operation(summary = "获取商户统计数据", description = "获取商户的汇总统计信息，包括按类型、审核状态等维度的统计")
    @GetMapping("/statistics")
    public ResponseEntity<?> getMerchantStatistics() {
        try {
            Map<String, Object> statistics = merchantService.getMerchantStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()));
        }
    }
}
