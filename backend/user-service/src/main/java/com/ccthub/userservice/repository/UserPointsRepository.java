package com.ccthub.userservice.repository;

import com.ccthub.userservice.entity.UserPoints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户积分流水Repository
 */
@Repository
public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {

    /**
     * 分页查询用户的积分流水
     */
    Page<UserPoints> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 按来源查询用户的积分流水
     */
    Page<UserPoints> findByUserIdAndSourceOrderByCreatedAtDesc(
            Long userId, String source, Pageable pageable);

    /**
     * 按时间范围查询用户的积分流水
     */
    @Query("SELECT up FROM UserPoints up WHERE up.userId = :userId " +
           "AND up.createdAt >= :startTime AND up.createdAt <= :endTime " +
           "ORDER BY up.createdAt DESC")
    Page<UserPoints> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * 查询用户当前有效积分总额
     */
    @Query("SELECT COALESCE(SUM(up.points), 0) FROM UserPoints up " +
           "WHERE up.userId = :userId AND up.status = 1 " +
           "AND (up.expiresAt IS NULL OR up.expiresAt > :now)")
    Integer getTotalValidPoints(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * 查询即将过期的积分
     */
    @Query("SELECT up FROM UserPoints up WHERE up.userId = :userId " +
           "AND up.status = 1 AND up.expiresAt BETWEEN :now AND :expireTime " +
           "ORDER BY up.expiresAt ASC")
    List<UserPoints> findExpiringPoints(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            @Param("expireTime") LocalDateTime expireTime);

    /**
     * 查询已过期但未处理的积分
     */
    @Query("SELECT up FROM UserPoints up WHERE up.status = 1 " +
           "AND up.expiresAt IS NOT NULL AND up.expiresAt < :now")
    List<UserPoints> findExpiredPoints(@Param("now") LocalDateTime now);

    /**
     * 根据订单号查询积分流水
     */
    List<UserPoints> findByOrderNo(String orderNo);
}
