package com.ccthub.userservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.entity.Order;

/**
 * 订单Repository
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据订单号查询订单
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单列表
     */
    List<Order> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 根据景区ID查询订单列表
     */
    List<Order> findByScenicSpotIdOrderByCreateTimeDesc(Long scenicSpotId);

    /**
     * 根据状态查询订单列表
     */
    List<Order> findByStatusOrderByCreateTimeDesc(String status);

    /**
     * 根据游玩日期查询订单列表
     */
    List<Order> findByVisitDateOrderByCreateTimeDesc(LocalDate visitDate);

    /**
     * 根据用户ID和状态查询订单列表
     */
    List<Order> findByUserIdAndStatusOrderByCreateTimeDesc(Long userId, String status);
}
