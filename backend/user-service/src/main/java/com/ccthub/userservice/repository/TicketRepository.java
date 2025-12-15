package com.ccthub.userservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.entity.Ticket;

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
     * 动态条件查询：景区ID、名称模糊、状态
     */
    @Query("SELECT t FROM Ticket t WHERE (:scenicSpotId IS NULL OR t.scenicSpotId = :scenicSpotId) "
            + "AND (:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%',:name,'%'))) "
            + "AND (:status IS NULL OR t.status = :status)")
    Page<Ticket> findByFilters(@Param("scenicSpotId") Long scenicSpotId,
            @Param("name") String name,
            @Param("status") Integer status,
            Pageable pageable);

    /**
     * 统计景区的票种数量
     */
    long countByScenicSpotId(Long scenicSpotId);

    /**
     * 统计上架的票种数量
     */
    long countByStatus(Integer status);
}
