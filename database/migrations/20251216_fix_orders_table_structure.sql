-- ============================================================
-- 修正orders和order_items表结构，使其与DDL.sql一致
-- 创建时间: 2025-12-16
-- 问题: 本地数据库混合了DDL.sql和实体类字段，需要统一
-- ============================================================

-- 备份现有数据（如果有）
CREATE TABLE IF NOT EXISTS `orders_backup_20251216` AS SELECT * FROM `orders`;
CREATE TABLE IF NOT EXISTS `order_items_backup_20251216` AS SELECT * FROM `order_items`;

-- 删除orders表中实体类添加的多余字段
ALTER TABLE `orders`
  DROP COLUMN IF EXISTS `id`,
  DROP COLUMN IF EXISTS `scenic_spot_id`,
  DROP COLUMN IF EXISTS `ticket_id`,
  DROP COLUMN IF EXISTS `visit_date`,
  DROP COLUMN IF EXISTS `visitor_count`,
  DROP COLUMN IF EXISTS `status`;

-- 删除order_items表中实体类添加的多余字段
ALTER TABLE `order_items`
  DROP COLUMN IF EXISTS `order_id`,
  DROP COLUMN IF EXISTS `ticket_price_id`,
  DROP COLUMN IF EXISTS `visitor_id_card`,
  DROP COLUMN IF EXISTS `visitor_phone`,
  DROP COLUMN IF EXISTS `price`,
  DROP COLUMN IF EXISTS `is_verified`,
  DROP COLUMN IF EXISTS `verify_time`,
  DROP COLUMN IF EXISTS `verify_staff_id`,
  DROP COLUMN IF EXISTS `qr_code_url`,
  DROP COLUMN IF EXISTS `create_time`;

-- 验证表结构
SELECT 'orders表字段:' AS info;
DESC `orders`;

SELECT 'order_items表字段:' AS info;
DESC `order_items`;

-- 说明：
-- 1. orders表应使用order_no作为主键（DDL.sql定义）
-- 2. 门票订单信息存储在order_items中
-- 3. 实体类Order.java需要重构为通用订单实体
-- 4. 需要创建新的TicketOrder实体来处理门票订单业务逻辑
