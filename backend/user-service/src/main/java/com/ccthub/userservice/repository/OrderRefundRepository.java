package com.ccthub.userservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.entity.OrderRefund;

/**
 * 订单退款Repository接口
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Repository
public interface OrderRefundRepository extends JpaRepository<OrderRefund, Long> {

    /**
     * 根据退款单号查询
     */
    Optional<OrderRefund> findByRefundNo(String refundNo);

    /**
     * 根据订单号查询退款记录
     */
    List<OrderRefund> findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询退款列表
     */
    Page<OrderRefund> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据状态查询退款列表
     */
    Page<OrderRefund> findByStatus(Integer status, Pageable pageable);

    /**
     * 动态条件查询
     */
    @Query("SELECT r FROM OrderRefund r WHERE "
            + "(:orderNo IS NULL OR r.orderNo = :orderNo) "
            + "AND (:userId IS NULL OR r.userId = :userId) "
            + "AND (:status IS NULL OR r.status = :status) "
            + "AND (:startTime IS NULL OR r.createdAt >= :startTime) "
            + "AND (:endTime IS NULL OR r.createdAt <= :endTime)")
    Page<OrderRefund> findByFilters(@Param("orderNo") String orderNo,
            @Param("userId") Long userId,
            @Param("status") Integer status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * 统计某状态的退款数量
     */
    long countByStatus(Integer status);

    /**
     * 统计待审核的退款数量
     */
    default long countPendingAudits() {
        return countByStatus(OrderRefund.STATUS_PENDING_AUDIT);
    }
}
