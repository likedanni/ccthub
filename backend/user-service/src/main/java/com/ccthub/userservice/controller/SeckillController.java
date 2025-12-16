package com.ccthub.userservice.controller;

import com.ccthub.userservice.entity.SeckillEvent;
import com.ccthub.userservice.service.SeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seckills")
@RequiredArgsConstructor
public class SeckillController {
    
    private final SeckillService seckillService;
    
    /**
     * 创建秒杀活动
     */
    @PostMapping
    public ResponseEntity<SeckillEvent> createSeckill(@RequestBody SeckillEvent seckillEvent) {
        SeckillEvent created = seckillService.createSeckill(seckillEvent);
        return ResponseEntity.ok(created);
    }
    
    /**
     * 更新秒杀活动
     */
    @PutMapping("/{id}")
    public ResponseEntity<SeckillEvent> updateSeckill(
        @PathVariable Long id,
        @RequestBody SeckillEvent seckillEvent
    ) {
        SeckillEvent updated = seckillService.updateSeckill(id, seckillEvent);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * 修改秒杀状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<SeckillEvent> toggleStatus(
        @PathVariable Long id,
        @RequestParam Integer status
    ) {
        SeckillEvent updated = seckillService.toggleStatus(id, status);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * 删除秒杀活动
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeckill(@PathVariable Long id) {
        seckillService.deleteSeckill(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取秒杀详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<SeckillEvent> getSeckillDetail(@PathVariable Long id) {
        SeckillEvent seckill = seckillService.getSeckillDetail(id);
        return ResponseEntity.ok(seckill);
    }
    
    /**
     * 分页查询秒杀列表
     */
    @GetMapping
    public ResponseEntity<Page<SeckillEvent>> getSeckillList(
        @RequestParam(required = false) Integer status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<SeckillEvent> seckills = seckillService.getSeckillList(status, page, size);
        return ResponseEntity.ok(seckills);
    }
    
    /**
     * 查询进行中的秒杀
     */
    @GetMapping("/ongoing")
    public ResponseEntity<List<SeckillEvent>> getOngoingSeckills() {
        List<SeckillEvent> seckills = seckillService.getOngoingSeckills();
        return ResponseEntity.ok(seckills);
    }
    
    /**
     * 查询即将开始的秒杀
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<SeckillEvent>> getUpcomingSeckills(
        @RequestParam(defaultValue = "10") int limit
    ) {
        List<SeckillEvent> seckills = seckillService.getUpcomingSeckills(limit);
        return ResponseEntity.ok(seckills);
    }
    
    /**
     * 处理秒杀购买
     */
    @PostMapping("/{id}/purchase")
    public ResponseEntity<Boolean> processPurchase(
        @PathVariable Long id,
        @RequestParam Long userId,
        @RequestParam(defaultValue = "1") Integer quantity
    ) {
        boolean success = seckillService.processPurchase(id, userId, quantity);
        return ResponseEntity.ok(success);
    }
}
