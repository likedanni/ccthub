package com.ccthub.userservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.entity.Order;

/**
 * 订单Repository（通用订单）
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据订单号查询订单
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单列表（按创建时间倒序）
     */
    List<Order> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 根据用户ID和订单状态查询
     */
    List<Order> findByUserIdAndOrderStatusOrderByCreateTimeDesc(Long userId, Integer orderStatus);

    /**
     * 根据用户ID和订单类型查询
     */
    List<Order> findByUserIdAndOrderTypeOrderByCreateTimeDesc(Long userId, Integer orderType);

    /**
     * 根据商户ID查询订单列表（按创建时间倒序）
     */
    List<Order> findByMerchantIdOrderByCreateTimeDesc(Long merchantId);

    /**
     * 根据订单状态查询订单列表
     */
    List<Order> findByOrderStatusOrderByCreateTimeDesc(Integer orderStatus);

    /**
     * 根据支付状态查询订单列表
     */
    List<Order> findByPaymentStatusOrderByCreateTimeDesc(Integer paymentStatus);

    /**
     * 根据用户ID和订单类型查询（不排序）
     */
    List<Order> findByUserIdAndOrderType(Long userId, Integer orderType);

    /**
     * 根据订单类型查询所有订单（按创建时间倒序）
     */
    List<Order> findByOrderTypeOrderByCreateTimeDesc(Integer orderType);

    /**
     * 根据订单类型查询所有订单（不排序）
     */
    List<Order> findByOrderType(Integer orderType);

    /**
     * 统计用户订单总数（按类型）
     */
    long countByUserIdAndOrderType(Long userId, Integer orderType);
}
