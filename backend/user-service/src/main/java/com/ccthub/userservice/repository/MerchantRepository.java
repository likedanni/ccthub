package com.ccthub.userservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ccthub.userservice.entity.Merchant;

/**
 * 商户数据访问层
 */
@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

        /**
         * 根据审核状态查询商户
         */
        Page<Merchant> findByAuditStatus(Integer auditStatus, Pageable pageable);

        /**
         * 根据状态查询商户
         */
        Page<Merchant> findByStatus(Integer status, Pageable pageable);

        /**
         * 根据商户类型查询
         */
        Page<Merchant> findByType(Integer type, Pageable pageable);

        /**
         * 根据合作类型查询
         */
        Page<Merchant> findByCooperationType(Integer cooperationType, Pageable pageable);

        /**
         * 根据城市查询
         */
        Page<Merchant> findByCity(String city, Pageable pageable);

        /**
         * 根据名称模糊查询
         */
        Page<Merchant> findByNameContaining(String name, Pageable pageable);

        /**
         * 多条件查询
         */
        @Query("SELECT m FROM Merchant m WHERE " +
                        "(:name IS NULL OR m.name LIKE %:name%) AND " +
                        "(:type IS NULL OR m.type = :type) AND " +
                        "(:cooperationType IS NULL OR m.cooperationType = :cooperationType) AND " +
                        "(:auditStatus IS NULL OR m.auditStatus = :auditStatus) AND " +
                        "(:status IS NULL OR m.status = :status)")
        Page<Merchant> findByConditions(
                        @Param("name") String name,
                        @Param("type") Integer type,
                        @Param("cooperationType") Integer cooperationType,
                        @Param("auditStatus") Integer auditStatus,
                        @Param("status") Integer status,
                        Pageable pageable);

        /**
         * 查询指定区域的商户
         */
        @Query("SELECT m FROM Merchant m WHERE m.province = :province AND m.city = :city")
        List<Merchant> findByProvinceAndCity(
                        @Param("province") String province,
                        @Param("city") String city);

        /**
         * 统计各类型商户数量
         */
        @Query("SELECT m.type, COUNT(m) FROM Merchant m GROUP BY m.type")
        List<Object[]> countByType();

        /**
         * 统计各审核状态商户数量
         */
        @Query("SELECT m.auditStatus, COUNT(m) FROM Merchant m GROUP BY m.auditStatus")
        List<Object[]> countByAuditStatus();
}
