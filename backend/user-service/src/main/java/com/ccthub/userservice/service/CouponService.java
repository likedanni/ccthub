package com.ccthub.userservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.entity.Coupon;
import com.ccthub.userservice.entity.UserCoupon;
import com.ccthub.userservice.repository.CouponRepository;
import com.ccthub.userservice.repository.UserCouponRepository;

/**
 * 优惠券Service
 */
@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    /**
     * 获取优惠券列表
     */
    public Page<Coupon> getCouponList(Integer status, Integer type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (status != null && type != null) {
            return couponRepository.findByStatusAndType(status, type, pageable);
        } else if (status != null) {
            return couponRepository.findByStatus(status, pageable);
        } else if (type != null) {
            return couponRepository.findByType(type, pageable);
        } else {
            return couponRepository.findAll(pageable);
        }
    }

    /**
     * 获取优惠券详情
     */
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("优惠券不存在"));
    }

    /**
     * 创建优惠券
     */
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        // 设置初始剩余数量
        if (coupon.getTotalQuantity() != null) {
            coupon.setRemainingQuantity(coupon.getTotalQuantity());
        }

        // 设置初始状态
        if (coupon.getStatus() == null) {
            coupon.setStatus(Coupon.Status.NOT_STARTED);
        }

        return couponRepository.save(coupon);
    }

    /**
     * 更新优惠券
     */
    @Transactional
    public Coupon updateCoupon(Long id, Coupon couponUpdate) {
        Coupon coupon = getCouponById(id);

        // 更新基本信息
        if (couponUpdate.getName() != null) {
            coupon.setName(couponUpdate.getName());
        }
        if (couponUpdate.getType() != null) {
            coupon.setType(couponUpdate.getType());
        }
        if (couponUpdate.getValue() != null) {
            coupon.setValue(couponUpdate.getValue());
        }
        if (couponUpdate.getMinSpend() != null) {
            coupon.setMinSpend(couponUpdate.getMinSpend());
        }
        if (couponUpdate.getApplicableType() != null) {
            coupon.setApplicableType(couponUpdate.getApplicableType());
        }
        if (couponUpdate.getApplicableIds() != null) {
            coupon.setApplicableIds(couponUpdate.getApplicableIds());
        }
        if (couponUpdate.getValidityType() != null) {
            coupon.setValidityType(couponUpdate.getValidityType());
        }
        if (couponUpdate.getValidDays() != null) {
            coupon.setValidDays(couponUpdate.getValidDays());
        }
        if (couponUpdate.getStartsAt() != null) {
            coupon.setStartsAt(couponUpdate.getStartsAt());
        }
        if (couponUpdate.getExpiresAt() != null) {
            coupon.setExpiresAt(couponUpdate.getExpiresAt());
        }
        if (couponUpdate.getTotalQuantity() != null) {
            coupon.setTotalQuantity(couponUpdate.getTotalQuantity());
        }
        if (couponUpdate.getLimitPerUser() != null) {
            coupon.setLimitPerUser(couponUpdate.getLimitPerUser());
        }

        return couponRepository.save(coupon);
    }

    /**
     * 更新优惠券状态
     */
    @Transactional
    public Coupon updateCouponStatus(Long id, Integer status) {
        Coupon coupon = getCouponById(id);
        coupon.setStatus(status);
        return couponRepository.save(coupon);
    }

    /**
     * 获取用户优惠券列表
     */
    public Page<UserCoupon> getUserCoupons(Long userId, Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "receivedAt"));

        if (userId != null && status != null) {
            return userCouponRepository.findByUserIdAndStatus(userId, status, pageable);
        } else if (userId != null) {
            return userCouponRepository.findByUserId(userId, pageable);
        } else if (status != null) {
            return userCouponRepository.findByStatus(status, pageable);
        } else {
            return userCouponRepository.findAll(pageable);
        }
    }

    /**
     * 发放优惠券给用户
     */
    @Transactional
    public UserCoupon grantCoupon(Long couponId, Long userId) {
        Coupon coupon = getCouponById(couponId);

        // 检查优惠券状态
        if (!coupon.getStatus().equals(Coupon.Status.IN_PROGRESS)) {
            throw new RuntimeException("优惠券未在发放期");
        }

        // 检查剩余数量
        if (coupon.getRemainingQuantity() != null && coupon.getRemainingQuantity() <= 0) {
            throw new RuntimeException("优惠券已领完");
        }

        // 检查用户领取限制
        int userReceivedCount = userCouponRepository.countByUserIdAndCouponId(userId, couponId);
        if (userReceivedCount >= coupon.getLimitPerUser()) {
            throw new RuntimeException("已达到领取上限");
        }

        // 创建用户优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setCouponCode(generateCouponCode());
        userCoupon.setStatus(UserCoupon.Status.UNUSED);

        // 计算过期时间
        LocalDateTime expiresAt;
        if (coupon.getValidityType().equals(Coupon.ValidityType.FIXED_PERIOD)) {
            expiresAt = coupon.getExpiresAt();
        } else {
            expiresAt = LocalDateTime.now().plusDays(coupon.getValidDays());
        }
        userCoupon.setExpiresAt(expiresAt);

        // 减少剩余数量
        if (coupon.getRemainingQuantity() != null) {
            coupon.setRemainingQuantity(coupon.getRemainingQuantity() - 1);
            couponRepository.save(coupon);
        }

        return userCouponRepository.save(userCoupon);
    }

    /**
     * 使用优惠券
     */
    @Transactional
    public UserCoupon useCoupon(Long userCouponId, String orderNo) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new RuntimeException("用户优惠券不存在"));

        // 检查券状态
        if (!userCoupon.getStatus().equals(UserCoupon.Status.UNUSED)) {
            throw new RuntimeException("优惠券不可用");
        }

        // 检查是否过期
        if (userCoupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("优惠券已过期");
        }

        // 标记为已使用
        userCoupon.setStatus(UserCoupon.Status.USED);
        userCoupon.setUsedAt(LocalDateTime.now());
        userCoupon.setOrderNo(orderNo);

        return userCouponRepository.save(userCoupon);
    }

    /**
     * 生成优惠券码
     */
    private String generateCouponCode() {
        return "CPN" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
