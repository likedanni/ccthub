package com.ccthub.userservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.entity.SeckillEvent;
import com.ccthub.userservice.repository.SeckillEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillService {

    private final SeckillEventRepository seckillEventRepository;

    /**
     * 创建秒杀活动
     */
    @Transactional
    public SeckillEvent createSeckill(SeckillEvent seckillEvent) {
        // 设置默认状态
        if (seckillEvent.getStatus() == null) {
            seckillEvent.setStatus(0); // 未开始
        }

        // 初始化可用库存
        seckillEvent.setAvailableInventory(seckillEvent.getTotalInventory());

        return seckillEventRepository.save(seckillEvent);
    }

    /**
     * 更新秒杀活动
     */
    @Transactional
    public SeckillEvent updateSeckill(Long id, SeckillEvent seckillEvent) {
        SeckillEvent existing = seckillEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        // 只允许更新部分字段
        if (seckillEvent.getTitle() != null) {
            existing.setTitle(seckillEvent.getTitle());
        }
        if (seckillEvent.getSeckillPrice() != null) {
            existing.setSeckillPrice(seckillEvent.getSeckillPrice());
        }
        if (seckillEvent.getOriginalPrice() != null) {
            existing.setOriginalPrice(seckillEvent.getOriginalPrice());
        }
        if (seckillEvent.getTotalInventory() != null) {
            // 更新总库存时，同步更新可用库存
            int soldCount = existing.getTotalInventory() - existing.getAvailableInventory();
            existing.setTotalInventory(seckillEvent.getTotalInventory());
            existing.setAvailableInventory(seckillEvent.getTotalInventory() - soldCount);
        }
        if (seckillEvent.getLimitPerUser() != null) {
            existing.setLimitPerUser(seckillEvent.getLimitPerUser());
        }
        if (seckillEvent.getStartsAt() != null) {
            existing.setStartsAt(seckillEvent.getStartsAt());
        }
        if (seckillEvent.getEndsAt() != null) {
            existing.setEndsAt(seckillEvent.getEndsAt());
        }

        return seckillEventRepository.save(existing);
    }

    /**
     * 修改秒杀状态
     */
    @Transactional
    public SeckillEvent toggleStatus(Long id, Integer status) {
        SeckillEvent seckillEvent = seckillEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        seckillEvent.setStatus(status);
        return seckillEventRepository.save(seckillEvent);
    }

    /**
     * 删除秒杀活动
     */
    @Transactional
    public void deleteSeckill(Long id) {
        SeckillEvent seckillEvent = seckillEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        // 只允许删除未开始或已结束的秒杀
        if (seckillEvent.getStatus() == 1) {
            throw new RuntimeException("秒杀进行中，无法删除");
        }

        seckillEventRepository.deleteById(id);
    }

    /**
     * 获取秒杀详情
     */
    public SeckillEvent getSeckillDetail(Long id) {
        return seckillEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));
    }

    /**
     * 分页查询秒杀列表
     */
    public Page<SeckillEvent> getSeckillList(Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (status != null) {
            return seckillEventRepository.findByStatus(status, pageable);
        }

        return seckillEventRepository.findAll(pageable);
    }

    /**
     * 查询进行中的秒杀
     */
    public List<SeckillEvent> getOngoingSeckills() {
        return seckillEventRepository.findOngoingSeckills(LocalDateTime.now());
    }

    /**
     * 查询即将开始的秒杀
     */
    public List<SeckillEvent> getUpcomingSeckills(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return seckillEventRepository.findUpcomingSeckills(LocalDateTime.now(), pageable);
    }

    /**
     * 扣减库存（数据库层面的原子操作）
     * 注意：实际生产环境建议使用Redis实现
     */
    @Transactional
    public boolean deductInventory(Long seckillId, Integer quantity) {
        int updated = seckillEventRepository.deductInventory(seckillId, quantity);
        return updated > 0;
    }

    /**
     * 处理秒杀购买
     * 注意：这是简化版本，实际生产环境需要：
     * 1. Redis预扣库存
     * 2. 消息队列异步处理订单
     * 3. 分布式锁防止超卖
     */
    @Transactional
    public boolean processPurchase(Long seckillId, Long userId, Integer quantity) {
        SeckillEvent seckill = seckillEventRepository.findById(seckillId)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));

        // 检查秒杀状态
        if (seckill.getStatus() != 1) {
            throw new RuntimeException("秒杀未开始或已结束");
        }

        // 检查时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(seckill.getStartsAt()) || now.isAfter(seckill.getEndsAt())) {
            throw new RuntimeException("不在秒杀时间范围内");
        }

        // 检查数量限制
        if (quantity > seckill.getLimitPerUser()) {
            throw new RuntimeException("超过单人购买限制");
        }

        // 扣减库存（数据库层面的原子操作）
        boolean success = deductInventory(seckillId, quantity);

        if (!success) {
            throw new RuntimeException("库存不足");
        }

        // TODO: 创建订单（这里应该调用订单服务）
        log.info("秒杀成功 - 用户ID: {}, 秒杀ID: {}, 数量: {}", userId, seckillId, quantity);

        return true;
    }

    /**
     * 根据商品ID查询秒杀
     */
    public List<SeckillEvent> getSeckillsByProduct(Long productId) {
        return seckillEventRepository.findByProductId(productId);
    }
}
