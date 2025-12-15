package com.ccthub.userservice.repository;

import com.ccthub.userservice.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 票种Repository接口
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    /**
     * 根据景区ID查询票种列表
     */
    List<Ticket> findByScenicSpotId(Long scenicSpotId);
    
    /**
     * 根据景区ID和状态查询票种列表
     */
    List<Ticket> findByScenicSpotIdAndStatus(Long scenicSpotId, Integer status);
    
    /**
     * 根据票种类型查询
     */
    Page<Ticket> findByType(Integer type, Pageable pageable);
    
    /**
     * 根据状态查询
     */
    Page<Ticket> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据景区ID分页查询
     */
    Page<Ticket> findByScenicSpotId(Long scenicSpotId, Pageable pageable);
    
    /**
     * 根据名称模糊查询
     */
    Page<Ticket> findByNameContaining(String name, Pageable pageable);
    
    /**
     * 统计景区的票种数量
     */
    long countByScenicSpotId(Long scenicSpotId);
    
    /**
     * 统计上架的票种数量
     */
    long countByStatus(Integer status);
}
