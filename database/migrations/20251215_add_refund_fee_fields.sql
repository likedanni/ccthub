-- 添加退款手续费和实际到账金额字段
-- 作者: CCTHub
-- 日期: 2025-12-15
USE `cct_hub`;
-- 添加退款手续费字段
ALTER TABLE `order_refunds`
ADD COLUMN `refund_fee` decimal(10, 2) DEFAULT 0 COMMENT "退款手续费"
AFTER `refund_amount`;
-- 添加实际到账金额字段
ALTER TABLE `order_refunds`
ADD COLUMN `actual_refund` decimal(10, 2) COMMENT "实际到账金额"
AFTER `refund_fee`;