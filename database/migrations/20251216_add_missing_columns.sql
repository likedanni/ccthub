-- ============================================================
-- 为DDL.sql添加实体类使用的缺失字段
-- 创建时间: 2025-12-16
-- 目的: 确保DDL.sql与实体类完全一致
-- ============================================================

-- 1. tickets表添加update_time字段
ALTER TABLE `tickets`
  ADD COLUMN IF NOT EXISTS `update_time` datetime DEFAULT (now()) COMMENT '更新时间' AFTER `create_time`;

-- 2. ticket_prices表添加inventory_locked和version字段
ALTER TABLE `ticket_prices`
  ADD COLUMN IF NOT EXISTS `inventory_locked` int NOT NULL DEFAULT 0 COMMENT '锁定库存' AFTER `inventory`,
  ADD COLUMN IF NOT EXISTS `version` int DEFAULT 0 COMMENT '乐观锁版本号' AFTER `is_active`;

-- 3. user_wallet表添加version字段
ALTER TABLE `user_wallet`
  ADD COLUMN IF NOT EXISTS `version` int DEFAULT 0 COMMENT '乐观锁版本号' AFTER `updated_at`;

-- 验证修改
SELECT 'tickets表结构:' AS info;
DESC `tickets`;

SELECT 'ticket_prices表结构:' AS info;
DESC `ticket_prices`;

SELECT 'user_wallet表结构:' AS info;
DESC `user_wallet`;
