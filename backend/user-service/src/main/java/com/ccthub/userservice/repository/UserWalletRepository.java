package com.ccthub.userservice.repository;

import com.ccthub.userservice.entity.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

/**
 * 用户钱包Repository
 */
@Repository
public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {

    /**
     * 根据用户ID查询钱包
     */
    Optional<UserWallet> findByUserId(Long userId);

    /**
     * 根据用户ID查询钱包（悲观锁）
     * 用于余额变动操作，防止并发问题
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM UserWallet w WHERE w.userId = :userId")
    Optional<UserWallet> findByUserIdWithLock(@Param("userId") Long userId);

    /**
     * 检查用户是否已有钱包
     */
    boolean existsByUserId(Long userId);

    /**
     * 根据用户ID和状态查询钱包
     */
    Optional<UserWallet> findByUserIdAndStatus(Long userId, Integer status);
}
