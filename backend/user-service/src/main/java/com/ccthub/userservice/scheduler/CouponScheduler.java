package com.ccthub.userservice.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.entity.UserCoupon;
import com.ccthub.userservice.repository.UserCouponRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 优惠券定时任务
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Slf4j
@Component
public class CouponScheduler {

    @Autowired
    private UserCouponRepository userCouponRepository;

    /**
     * 处理过期优惠券
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void processExpiredCoupons() {
        log.info("开始处理过期优惠券...");

        try {
            LocalDateTime now = LocalDateTime.now();
            int page = 0;
            int size = 100;
            int totalProcessed = 0;

            while (true) {
                // 分页查询未使用的优惠券
                Page<UserCoupon> userCouponPage = userCouponRepository.findByStatus(
                        UserCoupon.Status.UNUSED,
                        PageRequest.of(page, size));

                if (userCouponPage.isEmpty()) {
                    break;
                }

                List<UserCoupon> coupons = userCouponPage.getContent();

                // 检查并更新过期优惠券
                for (UserCoupon coupon : coupons) {
                    if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(now)) {
                        coupon.setStatus(UserCoupon.Status.EXPIRED);
                        userCouponRepository.save(coupon);
                        totalProcessed++;
                        log.debug("优惠券已过期: id={}, code={}, userId={}",
                                coupon.getId(), coupon.getCouponCode(), coupon.getUserId());
                    }
                }

                // 如果是最后一页，退出循环
                if (!userCouponPage.hasNext()) {
                    break;
                }

                page++;
            }

            log.info("过期优惠券处理完成，共处理{}张", totalProcessed);

        } catch (Exception e) {
            log.error("处理过期优惠券时发生错误", e);
        }
    }

    /**
     * 优惠券统计任务
     * 每天凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void couponStatistics() {
        log.info("开始优惠券统计任务...");

        try {
            // 统计各状态优惠券数量
            Page<UserCoupon> unusedPage = userCouponRepository.findByStatus(
                    UserCoupon.Status.UNUSED,
                    PageRequest.of(0, 1));

            Page<UserCoupon> usedPage = userCouponRepository.findByStatus(
                    UserCoupon.Status.USED,
                    PageRequest.of(0, 1));

            Page<UserCoupon> expiredPage = userCouponRepository.findByStatus(
                    UserCoupon.Status.EXPIRED,
                    PageRequest.of(0, 1));

            log.info("优惠券统计 - 未使用: {}, 已使用: {}, 已过期: {}",
                    unusedPage.getTotalElements(),
                    usedPage.getTotalElements(),
                    expiredPage.getTotalElements());

        } catch (Exception e) {
            log.error("优惠券统计任务失败", e);
        }
    }
}
