package com.ccthub.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.model.ScenicSpot;

@Repository
public interface ScenicSpotRepository extends JpaRepository<ScenicSpot, Long> {

    /**
     * 按状态查询景区列表（分页）
     */
    Page<ScenicSpot> findByStatus(String status, Pageable pageable);

    /**
     * 按城市查询景区列表（分页）
     */
    Page<ScenicSpot> findByCity(String city, Pageable pageable);

    /**
     * 按省份查询景区列表（分页）
     */
    Page<ScenicSpot> findByProvince(String province, Pageable pageable);

    /**
     * 按等级查询景区列表（分页）
     */
    Page<ScenicSpot> findByLevel(String level, Pageable pageable);

    /**
     * 按名称模糊查询景区列表（分页）
     */
    Page<ScenicSpot> findByNameContaining(String name, Pageable pageable);

    /**
     * 按状态和城市查询景区列表（分页）
     */
    Page<ScenicSpot> findByStatusAndCity(String status, String city, Pageable pageable);

    /**
     * 按状态和省份查询景区列表（分页）
     */
    Page<ScenicSpot> findByStatusAndProvince(String status, String province, Pageable pageable);

    /**
     * 按状态和等级查询景区列表（分页）
     */
    Page<ScenicSpot> findByStatusAndLevel(String status, String level, Pageable pageable);

    /**
     * 按名称模糊查询和状态查询景区列表（分页）
     */
    Page<ScenicSpot> findByNameContainingAndStatus(String name, String status, Pageable pageable);
}
