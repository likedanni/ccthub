package com.ccthub.userservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.entity.UserCoupon;

/**
 * 用户优惠券Repository
 */
@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    /**
     * 分页查询用户的优惠券
     */
    Page<UserCoupon> findByUserId(Long userId, Pageable pageable);

    /**
     * 分页查询用户指定状态的优惠券
     */
    Page<UserCoupon> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    /**
     * 根据券码查询用户优惠券
     */
    Optional<UserCoupon> findByCouponCode(String couponCode);

    /**
     * 查询用户已领取某优惠券的数量
     */
    int countByUserIdAndCouponId(Long userId, Long couponId);

    /**
     * 分页查询所有用户优惠券
     */
    Page<UserCoupon> findAll(Pageable pageable);

    /**
     * 根据状态分页查询
     */
    Page<UserCoupon> findByStatus(Integer status, Pageable pageable);
}
