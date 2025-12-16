package com.ccthub.userservice.repository;

import com.ccthub.userservice.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    /**
     * 根据商户ID查询活动
     */
    Page<Activity> findByMerchantId(Long merchantId, Pageable pageable);
    
    /**
     * 根据类型查询活动
     */
    Page<Activity> findByType(Integer type, Pageable pageable);
    
    /**
     * 根据状态查询活动
     */
    Page<Activity> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据审核状态查询活动
     */
    Page<Activity> findByAuditStatus(Integer auditStatus, Pageable pageable);
    
    /**
     * 查询进行中的活动
     */
    @Query("SELECT a FROM Activity a WHERE a.status = 1 " +
           "AND a.auditStatus = 1 " +
           "AND a.startsAt <= :now " +
           "AND a.endsAt >= :now")
    List<Activity> findOngoingActivities(@Param("now") LocalDateTime now);
    
    /**
     * 查询热门活动（根据参与人数）
     */
    @Query("SELECT a FROM Activity a " +
           "LEFT JOIN ActivityParticipation p ON a.id = p.activityId " +
           "WHERE a.status = 1 AND a.auditStatus = 1 " +
           "GROUP BY a.id " +
           "ORDER BY COUNT(p.id) DESC")
    List<Activity> findHotActivities(Pageable pageable);
    
    /**
     * 多条件查询
     */
    @Query("SELECT a FROM Activity a WHERE " +
           "(:merchantId IS NULL OR a.merchantId = :merchantId) AND " +
           "(:type IS NULL OR a.type = :type) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:auditStatus IS NULL OR a.auditStatus = :auditStatus) AND " +
           "(:keyword IS NULL OR a.name LIKE CONCAT('%', :keyword, '%'))")
    Page<Activity> findByConditions(
        @Param("merchantId") Long merchantId,
        @Param("type") Integer type,
        @Param("status") Integer status,
        @Param("auditStatus") Integer auditStatus,
        @Param("keyword") String keyword,
        Pageable pageable
    );
}
