-- ============================================================
-- 添加钱包流水表
-- 创建时间: 2025-12-15
-- ============================================================

CREATE TABLE `wallet_transactions` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "流水ID，主键自增",
  `transaction_no` varchar(32) UNIQUE NOT NULL COMMENT "流水号，唯一",
  `user_id` bigint NOT NULL COMMENT "用户ID",
  `wallet_id` bigint NOT NULL COMMENT "钱包ID",
  `transaction_type` tinyint NOT NULL COMMENT "交易类型: 1-充值, 2-消费, 3-退款, 4-提现, 5-冻结, 6-解冻",
  `amount` decimal(10, 2) NOT NULL COMMENT "变动金额（正数表示增加，负数表示减少）",
  `balance_after` decimal(10, 2) NOT NULL COMMENT "变动后余额",
  `order_no` varchar(32) COMMENT "关联订单号",
  `payment_no` varchar(32) COMMENT "关联支付流水号",
  `refund_no` varchar(32) COMMENT "关联退款流水号",
  `remark` varchar(200) COMMENT "备注",
  `status` tinyint NOT NULL COMMENT "状态: 1-成功, 0-失败, 2-处理中",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  INDEX `idx_transaction_no` (`transaction_no`) COMMENT "流水号索引",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_wallet_id` (`wallet_id`) COMMENT "钱包ID索引",
  INDEX `idx_transaction_type` (`transaction_type`) COMMENT "交易类型索引",
  INDEX `idx_created_at` (`created_at`) COMMENT "创建时间索引",
  INDEX `idx_order_no` (`order_no`) COMMENT "订单号索引",
  INDEX `idx_payment_no` (`payment_no`) COMMENT "支付流水号索引",
  INDEX `idx_refund_no` (`refund_no`) COMMENT "退款流水号索引",
  INDEX `idx_user_created` (`user_id`, `created_at` DESC) COMMENT "用户创建时间联合索引",
  CONSTRAINT `fk_wallet_transactions_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_wallet_transactions_wallet` FOREIGN KEY (`wallet_id`) REFERENCES `user_wallet` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "钱包流水表，记录所有钱包余额变动";
