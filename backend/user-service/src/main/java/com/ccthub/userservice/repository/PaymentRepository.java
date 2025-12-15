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

import com.ccthub.userservice.entity.Payment;

/**
 * 支付Repository接口
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 根据支付单号查询
     */
    Optional<Payment> findByPaymentNo(String paymentNo);

    /**
     * 根据订单号查询支付列表
     */
    List<Payment> findByOrderNo(String orderNo);

    /**
     * 根据订单号和状态查询
     */
    Optional<Payment> findByOrderNoAndStatus(String orderNo, Integer status);

    /**
     * 根据第三方流水号查询
     */
    Optional<Payment> findByThirdPartyNo(String thirdPartyNo);

    /**
     * 动态条件查询
     */
    @Query("SELECT p FROM Payment p WHERE "
            + "(:orderNo IS NULL OR p.orderNo = :orderNo) "
            + "AND (:paymentType IS NULL OR p.paymentType = :paymentType) "
            + "AND (:status IS NULL OR p.status = :status) "
            + "AND (:startTime IS NULL OR p.createTime >= :startTime) "
            + "AND (:endTime IS NULL OR p.createTime <= :endTime)")
    Page<Payment> findByFilters(@Param("orderNo") String orderNo,
            @Param("paymentType") String paymentType,
            @Param("status") Integer status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * 统计某状态的支付数量
     */
    long countByStatus(Integer status);

    /**
     * 查询超时未支付订单
     */
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createTime < :timeoutTime")
    List<Payment> findTimeoutPendingPayments(@Param("status") Integer status, 
                                              @Param("timeoutTime") LocalDateTime timeoutTime);
}
