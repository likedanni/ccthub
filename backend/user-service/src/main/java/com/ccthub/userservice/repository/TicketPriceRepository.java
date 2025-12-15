package com.ccthub.userservice.repository;

import com.ccthub.userservice.entity.TicketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 票价Repository接口
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPrice, Long> {
    
    /**
     * 根据票种ID查询所有价格
     */
    List<TicketPrice> findByTicketId(Long ticketId);
    
    /**
     * 根据票种ID和日期查询价格
     */
    Optional<TicketPrice> findByTicketIdAndPriceDate(Long ticketId, LocalDate priceDate);
    
    /**
     * 根据票种ID、日期和价格类型查询
     */
    Optional<TicketPrice> findByTicketIdAndPriceDateAndPriceType(
        Long ticketId, LocalDate priceDate, Integer priceType);
    
    /**
     * 根据票种ID和日期范围查询价格列表
     */
    List<TicketPrice> findByTicketIdAndPriceDateBetween(
        Long ticketId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 查询启用的价格
     */
    List<TicketPrice> findByTicketIdAndIsActive(Long ticketId, Boolean isActive);
    
    /**
     * 查询日期范围内启用的价格
     */
    List<TicketPrice> findByTicketIdAndPriceDateBetweenAndIsActive(
        Long ticketId, LocalDate startDate, LocalDate endDate, Boolean isActive);
    
    /**
     * 使用悲观锁查询（用于库存扣减）
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT tp FROM TicketPrice tp WHERE tp.ticketId = :ticketId " +
           "AND tp.priceDate = :priceDate AND tp.priceType = :priceType")
    Optional<TicketPrice> findByTicketIdAndPriceDateAndPriceTypeWithLock(
        @Param("ticketId") Long ticketId,
        @Param("priceDate") LocalDate priceDate,
        @Param("priceType") Integer priceType);
    
    /**
     * 查询库存不足的票价（告警用）
     */
    @Query("SELECT tp FROM TicketPrice tp WHERE tp.ticketId = :ticketId " +
           "AND tp.inventoryAvailable < :threshold AND tp.isActive = true")
    List<TicketPrice> findLowInventory(
        @Param("ticketId") Long ticketId,
        @Param("threshold") Integer threshold);
    
    /**
     * 批量删除指定日期范围的价格
     */
    void deleteByTicketIdAndPriceDateBetween(Long ticketId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 统计票种的有效价格数量
     */
    long countByTicketIdAndIsActive(Long ticketId, Boolean isActive);
}
