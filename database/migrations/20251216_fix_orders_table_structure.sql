-- ============================================================
-- 修正orders和order_items表结构，使其与DDL.sql一致
-- 创建时间: 2025-12-16
-- 问题: 本地数据库混合了DDL.sql和实体类字段，需要统一
-- ============================================================
-- 备份现有数据（如果有）
CREATE TABLE IF NOT EXISTS `orders_backup_20251216` AS
SELECT *
FROM `orders`;
CREATE TABLE IF NOT EXISTS `order_items_backup_20251216` AS
SELECT *
FROM `order_items`;
-- 删除orders表中门票订单专用字段（⚠️不删除id列，保持兼容）
-- 原因：实体类使用id作为主键，DDL使用order_no，两者共存以保持兼容
ALTER TABLE `orders` DROP COLUMN IF EXISTS `scenic_spot_id`,
    DROP COLUMN IF EXISTS `ticket_id`,
    DROP COLUMN IF EXISTS `visit_date`,
    DROP COLUMN IF EXISTS `visitor_count`,
    DROP COLUMN IF EXISTS `status`;
-- 删除order_items表中门票订单专用字段（⚠️不删除order_id列，保持兼容）
ALTER TABLE `order_items` DROP COLUMN IF EXISTS `ticket_price_id`,
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
-- 1. 保留了id和order_id列以保持与现有实体类的兼容性
-- 2. 删除了门票订单专用字段（scenic_spot_id, ticket_id等）
-- 3. 保留了通用订单字段（order_type, merchant_id, payment_status等）
-- 4. 后续需要重构Order.java和OrderItem.java为通用订单实体
-- 5. 建议创建TicketOrder视图或DTO来处理门票订单业务逻辑
-- 风险提示：
-- ⚠️ 此SQL修改了表结构，执行前务必备份数据！
-- ⚠️ 如有生产数据，建议先在测试环境验证！