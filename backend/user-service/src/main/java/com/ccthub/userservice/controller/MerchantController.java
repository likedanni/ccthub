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

/**
 * 商户管理控制器
 * 提供商户的CRUD、审核、启停用等功能
 */
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
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMerchant(
            @PathVariable Long id,
            @RequestBody Merchant merchant) {
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
    @PutMapping("/{id}/audit")
    public ResponseEntity<?> auditMerchant(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
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
    @PutMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMerchant(@PathVariable Long id) {
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
    @GetMapping("/{id}")
    public ResponseEntity<?> getMerchantDetail(@PathVariable Long id) {
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
    @GetMapping
    public ResponseEntity<?> getMerchantList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer cooperationType,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
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
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
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
