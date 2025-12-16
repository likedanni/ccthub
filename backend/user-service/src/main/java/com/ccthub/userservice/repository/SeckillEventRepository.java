package com.ccthub.userservice.repository;

import com.ccthub.userservice.entity.SeckillEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeckillEventRepository extends JpaRepository<SeckillEvent, Long> {
    
    /**
     * 根据商品ID查询秒杀
     */
    List<SeckillEvent> findByProductId(Long productId);
    
    /**
     * 根据状态查询秒杀
     */
    Page<SeckillEvent> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 查询进行中的秒杀
     */
    @Query("SELECT s FROM SeckillEvent s WHERE s.status = 1 " +
           "AND s.startsAt <= :now " +
           "AND s.endsAt >= :now " +
           "AND s.availableInventory > 0")
    List<SeckillEvent> findOngoingSeckills(@Param("now") LocalDateTime now);
    
    /**
     * 查询即将开始的秒杀
     */
    @Query("SELECT s FROM SeckillEvent s WHERE s.status = 0 " +
           "AND s.startsAt > :now " +
           "ORDER BY s.startsAt ASC")
    List<SeckillEvent> findUpcomingSeckills(@Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * 扣减库存（数据库层面的原子操作）
     */
    @Modifying
    @Query("UPDATE SeckillEvent s SET s.availableInventory = s.availableInventory - :quantity " +
           "WHERE s.id = :id AND s.availableInventory >= :quantity")
    int deductInventory(@Param("id") Long id, @Param("quantity") Integer quantity);
}
