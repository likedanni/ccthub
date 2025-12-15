package com.ccthub.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.model.ChinaRegion;

@Repository
public interface ChinaRegionRepository extends JpaRepository<ChinaRegion, Long> {
    List<ChinaRegion> findByLevel(Integer level);

    List<ChinaRegion> findByParentCode(String parentCode);
}
