package com.ccthub.userservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.entity.OrderItem;

/**
 * 订单项Repository
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 根据订单ID查询订单项列表
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 根据核销码查询订单项
     */
    Optional<OrderItem> findByVerificationCode(String verificationCode);

    /**
     * 根据订单ID和核销状态查询订单项列表
     */
    List<OrderItem> findByOrderIdAndIsVerified(Long orderId, Boolean isVerified);

    /**
     * 统计订单已核销数量
     */
    long countByOrderIdAndIsVerified(Long orderId, Boolean isVerified);
}
