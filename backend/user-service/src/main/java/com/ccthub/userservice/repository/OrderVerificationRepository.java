package com.ccthub.userservice.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.entity.OrderVerification;

/**
 * 订单核销记录Repository
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Repository
public interface OrderVerificationRepository extends JpaRepository<OrderVerification, Long> {

    /**
     * 统计订单已核销数量
     */
    @Query("SELECT COALESCE(SUM(v.verifiedQuantity), 0) FROM OrderVerification v WHERE v.orderNo = ?1")
    long countByOrderNo(String orderNo);

    /**
     * 统计商户核销订单数（去重）
     */
    @Query("SELECT COUNT(DISTINCT v.orderNo) FROM OrderVerification v WHERE v.merchantId = ?1 AND v.verifiedAt BETWEEN ?2 AND ?3")
    long countDistinctOrdersByMerchant(Long merchantId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计商户核销票数总和
     */
    @Query("SELECT COALESCE(SUM(v.verifiedQuantity), 0) FROM OrderVerification v WHERE v.merchantId = ?1 AND v.verifiedAt BETWEEN ?2 AND ?3")
    long sumVerifiedQuantityByMerchant(Long merchantId, LocalDateTime startTime, LocalDateTime endTime);
}
