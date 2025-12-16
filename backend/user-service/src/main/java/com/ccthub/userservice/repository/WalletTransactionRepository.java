package com.ccthub.userservice.repository;

import com.ccthub.userservice.entity.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 钱包流水Repository
 */
@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    /**
     * 根据流水号查询
     */
    Optional<WalletTransaction> findByTransactionNo(String transactionNo);

    /**
     * 分页查询用户的钱包流水
     */
    Page<WalletTransaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 按类型查询用户的钱包流水
     */
    Page<WalletTransaction> findByUserIdAndTransactionTypeOrderByCreatedAtDesc(
            Long userId, Integer transactionType, Pageable pageable);

    /**
     * 按时间范围查询用户的钱包流水
     */
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.userId = :userId " +
           "AND wt.createdAt >= :startTime AND wt.createdAt <= :endTime " +
           "ORDER BY wt.createdAt DESC")
    Page<WalletTransaction> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * 按类型和时间范围查询用户的钱包流水
     */
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.userId = :userId " +
           "AND wt.transactionType = :transactionType " +
           "AND wt.createdAt >= :startTime AND wt.createdAt <= :endTime " +
           "ORDER BY wt.createdAt DESC")
    Page<WalletTransaction> findByUserIdAndTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("transactionType") Integer transactionType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * 查询用户的钱包流水列表（不分页）
     */
    List<WalletTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据订单号查询流水
     */
    List<WalletTransaction> findByOrderNo(String orderNo);

    /**
     * 根据支付流水号查询
     */
    Optional<WalletTransaction> findByPaymentNo(String paymentNo);

    /**
     * 根据退款流水号查询
     */
    Optional<WalletTransaction> findByRefundNo(String refundNo);
}
