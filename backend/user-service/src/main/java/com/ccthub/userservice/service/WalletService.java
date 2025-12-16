package com.ccthub.userservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.SetPayPasswordRequest;
import com.ccthub.userservice.dto.WalletDTO;
import com.ccthub.userservice.dto.WalletRechargeRequest;
import com.ccthub.userservice.dto.WalletTransactionDTO;
import com.ccthub.userservice.entity.UserWallet;
import com.ccthub.userservice.entity.WalletTransaction;
import com.ccthub.userservice.repository.UserWalletRepository;
import com.ccthub.userservice.repository.WalletTransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 钱包服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserWalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 充值优惠配置
     * 满100送10
     */
    private static final Map<BigDecimal, BigDecimal> RECHARGE_BONUS_CONFIG = new HashMap<>() {
        {
            put(new BigDecimal("100"), new BigDecimal("10")); // 充值100送10
            put(new BigDecimal("200"), new BigDecimal("20")); // 充值200送20
            put(new BigDecimal("500"), new BigDecimal("50")); // 充值500送50
            put(new BigDecimal("1000"), new BigDecimal("100")); // 充值1000送100
        }
    };

    /**
     * 创建用户钱包
     * 用户注册时自动调用
     */
    @Transactional
    public UserWallet createWallet(Long userId) {
        // 检查是否已存在钱包
        if (walletRepository.existsByUserId(userId)) {
            log.warn("用户{}已有钱包，无需重复创建", userId);
            return walletRepository.findByUserId(userId).orElseThrow();
        }

        // 创建新钱包
        UserWallet wallet = UserWallet.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .frozenBalance(BigDecimal.ZERO)
                .totalDeposit(BigDecimal.ZERO)
                .totalConsumption(BigDecimal.ZERO)
                .securityLevel(1)
                .status(UserWallet.Status.NORMAL)
                .build();

        wallet = walletRepository.save(wallet);
        log.info("为用户{}创建钱包成功，钱包ID: {}", userId, wallet.getId());
        return wallet;
    }

    /**
     * 获取用户钱包信息
     */
    public WalletDTO getWallet(Long userId) {
        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("钱包不存在"));

        return toWalletDTO(wallet);
    }

    /**
     * 钱包充值
     * 返回支付流水号，前端用于调起支付
     */
    @Transactional
    public String recharge(Long userId, WalletRechargeRequest request) {
        // 检查钱包
        UserWallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new RuntimeException("钱包不存在"));

        if (!UserWallet.Status.NORMAL.equals(wallet.getStatus())) {
            throw new RuntimeException("钱包已冻结，无法充值");
        }

        // 计算充值赠送金额
        BigDecimal bonusAmount = calculateBonus(request.getAmount());
        BigDecimal totalAmount = request.getAmount().add(bonusAmount);

        // 生成流水号
        String transactionNo = generateTransactionNo();

        // 创建充值流水（待支付状态）
        WalletTransaction transaction = WalletTransaction.builder()
                .transactionNo(transactionNo)
                .userId(userId)
                .walletId(wallet.getId())
                .transactionType(WalletTransaction.Type.RECHARGE)
                .amount(totalAmount)
                .balanceAfter(wallet.getBalance().add(totalAmount))
                .status(WalletTransaction.Status.PROCESSING)
                .remark(bonusAmount.compareTo(BigDecimal.ZERO) > 0
                        ? String.format("充值%.2f元，赠送%.2f元", request.getAmount(), bonusAmount)
                        : String.format("充值%.2f元", request.getAmount()))
                .build();

        transactionRepository.save(transaction);
        log.info("创建充值流水: {}, 用户: {}, 金额: {}, 赠送: {}",
                transactionNo, userId, request.getAmount(), bonusAmount);

        // TODO: 调用支付服务创建支付订单
        // 这里应该调用PaymentService创建支付订单
        // 支付成功后通过回调完成充值

        return transactionNo;
    }

    /**
     * 完成充值（支付回调后调用）
     */
    @Transactional
    public void completeRecharge(String transactionNo, String paymentNo) {
        // 查询充值流水
        WalletTransaction transaction = transactionRepository.findByTransactionNo(transactionNo)
                .orElseThrow(() -> new RuntimeException("充值流水不存在"));

        if (WalletTransaction.Status.SUCCESS.equals(transaction.getStatus())) {
            log.warn("充值流水{}已完成，无需重复处理", transactionNo);
            return;
        }

        // 更新钱包余额
        UserWallet wallet = walletRepository.findByUserIdWithLock(transaction.getUserId())
                .orElseThrow(() -> new RuntimeException("钱包不存在"));

        wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
        wallet.setTotalDeposit(wallet.getTotalDeposit().add(transaction.getAmount()));
        walletRepository.save(wallet);

        // 更新流水状态
        transaction.setStatus(WalletTransaction.Status.SUCCESS);
        transaction.setPaymentNo(paymentNo);
        transactionRepository.save(transaction);

        log.info("完成充值: 流水号={}, 用户={}, 金额={}, 余额={}",
                transactionNo, transaction.getUserId(), transaction.getAmount(), wallet.getBalance());
    }

    /**
     * 余额支付
     */
    @Transactional
    public void payByBalance(Long userId, String orderNo, BigDecimal amount, String payPassword) {
        // 验证支付密码
        UserWallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new RuntimeException("钱包不存在"));

        if (wallet.getPayPasswordHash() == null) {
            throw new RuntimeException("未设置支付密码，请先设置");
        }

        if (!passwordEncoder.matches(payPassword, wallet.getPayPasswordHash())) {
            throw new RuntimeException("支付密码错误");
        }

        // 检查余额
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }

        // 扣减余额
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setTotalConsumption(wallet.getTotalConsumption().add(amount));
        walletRepository.save(wallet);

        // 创建消费流水
        String transactionNo = generateTransactionNo();
        WalletTransaction transaction = WalletTransaction.builder()
                .transactionNo(transactionNo)
                .userId(userId)
                .walletId(wallet.getId())
                .transactionType(WalletTransaction.Type.CONSUMPTION)
                .amount(amount.negate())
                .balanceAfter(wallet.getBalance())
                .orderNo(orderNo)
                .status(WalletTransaction.Status.SUCCESS)
                .remark("订单支付")
                .build();

        transactionRepository.save(transaction);
        log.info("余额支付成功: 流水号={}, 用户={}, 订单={}, 金额={}, 余额={}",
                transactionNo, userId, orderNo, amount, wallet.getBalance());
    }

    /**
     * 退款到钱包
     */
    @Transactional
    public void refundToWallet(Long userId, String orderNo, String refundNo, BigDecimal amount) {
        UserWallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new RuntimeException("钱包不存在"));

        // 增加余额
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        // 创建退款流水
        String transactionNo = generateTransactionNo();
        WalletTransaction transaction = WalletTransaction.builder()
                .transactionNo(transactionNo)
                .userId(userId)
                .walletId(wallet.getId())
                .transactionType(WalletTransaction.Type.REFUND)
                .amount(amount)
                .balanceAfter(wallet.getBalance())
                .orderNo(orderNo)
                .refundNo(refundNo)
                .status(WalletTransaction.Status.SUCCESS)
                .remark("订单退款")
                .build();

        transactionRepository.save(transaction);
        log.info("退款到钱包成功: 流水号={}, 用户={}, 订单={}, 退款号={}, 金额={}, 余额={}",
                transactionNo, userId, orderNo, refundNo, amount, wallet.getBalance());
    }

    /**
     * 查询钱包流水
     */
    public Page<WalletTransactionDTO> getTransactions(Long userId, Integer transactionType,
            LocalDateTime startTime, LocalDateTime endTime,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WalletTransaction> transactions;

        if (transactionType != null && startTime != null && endTime != null) {
            transactions = transactionRepository.findByUserIdAndTypeAndDateRange(
                    userId, transactionType, startTime, endTime, pageable);
        } else if (transactionType != null) {
            transactions = transactionRepository.findByUserIdAndTransactionTypeOrderByCreatedAtDesc(
                    userId, transactionType, pageable);
        } else if (startTime != null && endTime != null) {
            transactions = transactionRepository.findByUserIdAndDateRange(
                    userId, startTime, endTime, pageable);
        } else {
            transactions = transactionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        return transactions.map(this::toTransactionDTO);
    }

    /**
     * 设置支付密码
     */
    @Transactional
    public void setPayPassword(Long userId, SetPayPasswordRequest request) {
        // TODO: 验证短信验证码

        if (!request.getPayPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("钱包不存在"));

        // 加密并保存支付密码
        String hashedPassword = passwordEncoder.encode(request.getPayPassword());
        wallet.setPayPasswordHash(hashedPassword);
        walletRepository.save(wallet);

        log.info("用户{}设置支付密码成功", userId);
    }

    /**
     * 修改支付密码
     */
    @Transactional
    public void changePayPassword(Long userId, String oldPassword, SetPayPasswordRequest request) {
        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("钱包不存在"));

        if (wallet.getPayPasswordHash() == null) {
            throw new RuntimeException("未设置支付密码");
        }

        if (!passwordEncoder.matches(oldPassword, wallet.getPayPasswordHash())) {
            throw new RuntimeException("原支付密码错误");
        }

        if (!request.getPayPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 加密并保存新密码
        String hashedPassword = passwordEncoder.encode(request.getPayPassword());
        wallet.setPayPasswordHash(hashedPassword);
        walletRepository.save(wallet);

        log.info("用户{}修改支付密码成功", userId);
    }

    /**
     * 验证支付密码
     */
    public boolean verifyPayPassword(Long userId, String payPassword) {
        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("钱包不存在"));

        if (wallet.getPayPasswordHash() == null) {
            return false;
        }

        return passwordEncoder.matches(payPassword, wallet.getPayPasswordHash());
    }

    /**
     * 计算充值赠送金额
     */
    private BigDecimal calculateBonus(BigDecimal rechargeAmount) {
        // 找到最大的满足条件的充值档位
        BigDecimal bonus = BigDecimal.ZERO;
        for (Map.Entry<BigDecimal, BigDecimal> entry : RECHARGE_BONUS_CONFIG.entrySet()) {
            if (rechargeAmount.compareTo(entry.getKey()) >= 0) {
                BigDecimal currentBonus = entry.getValue();
                if (currentBonus.compareTo(bonus) > 0) {
                    bonus = currentBonus;
                }
            }
        }
        return bonus;
    }

    /**
     * 生成流水号
     */
    private String generateTransactionNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 10000);
        return "WT" + timestamp + String.format("%04d", random);
    }

    /**
     * 转换为WalletDTO
     */
    private WalletDTO toWalletDTO(UserWallet wallet) {
        return WalletDTO.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .frozenBalance(wallet.getFrozenBalance())
                .totalDeposit(wallet.getTotalDeposit())
                .totalConsumption(wallet.getTotalConsumption())
                .securityLevel(wallet.getSecurityLevel())
                .hasPayPassword(wallet.getPayPasswordHash() != null)
                .status(wallet.getStatus())
                .build();
    }

    /**
     * 转换为WalletTransactionDTO
     */
    private WalletTransactionDTO toTransactionDTO(WalletTransaction transaction) {
        return WalletTransactionDTO.builder()
                .id(transaction.getId())
                .transactionNo(transaction.getTransactionNo())
                .userId(transaction.getUserId())
                .transactionType(transaction.getTransactionType())
                .transactionTypeDesc(getTransactionTypeDesc(transaction.getTransactionType()))
                .amount(transaction.getAmount())
                .balanceAfter(transaction.getBalanceAfter())
                .orderNo(transaction.getOrderNo())
                .paymentNo(transaction.getPaymentNo())
                .refundNo(transaction.getRefundNo())
                .remark(transaction.getRemark())
                .status(transaction.getStatus())
                .statusDesc(getStatusDesc(transaction.getStatus()))
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    /**
     * 获取交易类型描述
     */
    private String getTransactionTypeDesc(Integer type) {
        if (type == null)
            return "未知";
        return switch (type) {
            case 1 -> "充值";
            case 2 -> "消费";
            case 3 -> "退款";
            case 4 -> "提现";
            case 5 -> "冻结";
            case 6 -> "解冻";
            default -> "未知";
        };
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null)
            return "未知";
        return switch (status) {
            case 0 -> "失败";
            case 1 -> "成功";
            case 2 -> "处理中";
            default -> "未知";
        };
    }

    /**
     * 获取钱包列表(管理后台使用)
     */
    public Page<WalletDTO> getWalletList(Long userId, String phone, Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserWallet> walletPage;

        // 根据筛选条件查询
        if (userId != null && status != null) {
            walletPage = walletRepository.findByUserIdAndStatus(userId, status, pageable);
        } else if (userId != null) {
            UserWallet wallet = walletRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("用户钱包不存在"));
            walletPage = new org.springframework.data.domain.PageImpl<>(
                    java.util.List.of(wallet), pageable, 1);
        } else if (status != null) {
            walletPage = walletRepository.findByStatus(status, pageable);
        } else {
            walletPage = walletRepository.findAll(pageable);
        }

        // 转换为DTO
        return walletPage.map(wallet -> {
            WalletDTO dto = new WalletDTO();
            dto.setId(wallet.getId());
            dto.setUserId(wallet.getUserId());
            // TODO: 从User表查询phone,暂时设置为null
            dto.setPhone(null);
            dto.setBalance(wallet.getBalance());
            dto.setFrozenBalance(wallet.getFrozenBalance());
            dto.setTotalDeposit(wallet.getTotalDeposit());
            dto.setTotalConsumption(wallet.getTotalConsumption());
            dto.setStatus(wallet.getStatus());
            dto.setCreatedAt(wallet.getCreatedAt());
            dto.setUpdatedAt(wallet.getUpdatedAt());
            return dto;
        });
    }
}
