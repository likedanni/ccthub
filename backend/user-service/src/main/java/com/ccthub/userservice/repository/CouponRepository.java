package com.ccthub.userservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.entity.Coupon;

/**
 * 优惠券Repository
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    /**
     * 根据状态分页查询优惠券
     */
    Page<Coupon> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据类型分页查询优惠券
     */
    Page<Coupon> findByType(Integer type, Pageable pageable);

    /**
     * 根据状态和类型分页查询优惠券
     */
    Page<Coupon> findByStatusAndType(Integer status, Integer type, Pageable pageable);

    /**
     * 查询发放中的优惠券
     */
    List<Coupon> findByStatus(Integer status);
}
