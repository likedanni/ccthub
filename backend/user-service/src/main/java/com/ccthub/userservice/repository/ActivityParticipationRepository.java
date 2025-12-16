package com.ccthub.userservice.repository;

import com.ccthub.userservice.entity.ActivityParticipation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityParticipationRepository extends JpaRepository<ActivityParticipation, Long> {
    
    /**
     * 查询用户是否已参与某活动
     */
    Optional<ActivityParticipation> findByActivityIdAndUserId(Long activityId, Long userId);
    
    /**
     * 查询用户的所有参与记录
     */
    Page<ActivityParticipation> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 查询某活动的所有参与记录
     */
    Page<ActivityParticipation> findByActivityId(Long activityId, Pageable pageable);
    
    /**
     * 统计某活动的参与人数
     */
    Long countByActivityId(Long activityId);
    
    /**
     * 统计某活动已完成的人数
     */
    Long countByActivityIdAndParticipationStatus(Long activityId, Integer participationStatus);
    
    /**
     * 查询待发放奖励的参与记录
     */
    @Query("SELECT p FROM ActivityParticipation p WHERE " +
           "p.participationStatus = 2 AND p.rewardStatus = 0")
    List<ActivityParticipation> findPendingRewards();
    
    /**
     * 查询用户在进行中的活动
     */
    @Query("SELECT p FROM ActivityParticipation p WHERE " +
           "p.userId = :userId AND p.participationStatus = 1")
    List<ActivityParticipation> findOngoingByUserId(@Param("userId") Long userId);
}
