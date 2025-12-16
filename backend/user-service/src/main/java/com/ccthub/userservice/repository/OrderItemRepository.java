package com.ccthub.userservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.entity.OrderItem;

/**
 * 订单明细Repository（通用订单明细）
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 根据订单号查询订单明细列表
     */
    List<OrderItem> findByOrderNo(String orderNo);

    /**
     * 根据核销码查询订单明细
     */
    Optional<OrderItem> findByVerificationCode(String verificationCode);

    /**
     * 根据订单号和核销状态查询
     */
    List<OrderItem> findByOrderNoAndVerificationStatus(String orderNo, Integer verificationStatus);

    /**
     * 根据商品ID查询订单明细
     */
    List<OrderItem> findByProductId(Long productId);

    /**
     * 根据票务日期查询订单明细
     */
    List<OrderItem> findByTicketDate(LocalDate ticketDate);

    /**
     * 统计订单已核销数量
     */
    long countByOrderNoAndVerificationStatus(String orderNo, Integer verificationStatus);
}
