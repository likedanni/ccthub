-- Sprint 6 数据库修复脚本
-- 修复活动和秒杀表的外键约束问题
-- 执行时间: 2025-12-16
USE `cct-hub`;
-- 1. 修改 activities 表: 允许 merchant_id 为 NULL (平台活动)
ALTER TABLE activities
MODIFY COLUMN merchant_id BIGINT NULL COMMENT '商户ID，NULL表示平台活动';
-- 2. 删除 activities 表的外键约束 (如果存在)
SET @constraint_name = (
        SELECT CONSTRAINT_NAME
        FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA = 'cct-hub'
            AND TABLE_NAME = 'activities'
            AND COLUMN_NAME = 'merchant_id'
            AND REFERENCED_TABLE_NAME IS NOT NULL
        LIMIT 1
    );
SET @sql = IF(
        @constraint_name IS NOT NULL,
        CONCAT(
            'ALTER TABLE activities DROP FOREIGN KEY ',
            @constraint_name
        ),
        'SELECT "外键约束不存在，跳过删除" AS info'
    );
PREPARE stmt
FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
-- 3. 修改 seckill_events 表: 允许 product_id 为 NULL (独立秒杀)
ALTER TABLE seckill_events
MODIFY COLUMN product_id BIGINT NULL COMMENT '商品ID，NULL表示独立秒杀活动';
-- 4. 删除 seckill_events 表的外键约束 (如果存在)
SET @constraint_name = (
        SELECT CONSTRAINT_NAME
        FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA = 'cct-hub'
            AND TABLE_NAME = 'seckill_events'
            AND COLUMN_NAME = 'product_id'
            AND REFERENCED_TABLE_NAME IS NOT NULL
        LIMIT 1
    );
SET @sql = IF(
        @constraint_name IS NOT NULL,
        CONCAT(
            'ALTER TABLE seckill_events DROP FOREIGN KEY ',
            @constraint_name
        ),
        'SELECT "外键约束不存在，跳过删除" AS info'
    );
PREPARE stmt
FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
-- 验证修改
SELECT '修复完成，验证表结构:' AS result;
SHOW COLUMNS
FROM activities LIKE 'merchant_id';
SHOW COLUMNS
FROM seckill_events LIKE 'product_id';