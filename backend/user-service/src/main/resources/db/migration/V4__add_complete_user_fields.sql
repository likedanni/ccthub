-- 添加 DDL.sql 中定义的完整用户表字段
-- 参考: /database/DDL.sql 中的 users 表定义
-- 1. 添加加密手机号和用户基础信息字段
ALTER TABLE `users`
ADD COLUMN `phone_encrypted` VARCHAR(128) COMMENT '手机号加密存储，用于安全匹配',
    ADD COLUMN `nickname` VARCHAR(50) COMMENT '用户昵称',
    ADD COLUMN `avatar_url` VARCHAR(500) COMMENT '头像URL地址',
    ADD COLUMN `id_card_encrypted` VARCHAR(128) COMMENT '身份证号加密存储',
    ADD COLUMN `real_name` VARCHAR(50) COMMENT '真实姓名';
-- 2. 添加会员体系字段（使用 INT 而非 TINYINT 以匹配 JPA）
ALTER TABLE `users`
ADD COLUMN `member_level` INT DEFAULT 1 COMMENT '会员等级: 1-普通, 2-白银, 3-黄金, 4-钻石',
    ADD COLUMN `growth_value` INT DEFAULT 0 COMMENT '成长值，用于会员升级';
-- 3. 添加积分体系字段
ALTER TABLE `users`
ADD COLUMN `total_points` INT DEFAULT 0 COMMENT '累计积分总额',
    ADD COLUMN `available_points` INT DEFAULT 0 COMMENT '当前可用积分';
-- 4. 添加钱包字段
ALTER TABLE `users`
ADD COLUMN `wallet_balance` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '钱包余额(元)';
-- 5. 添加状态和时间字段
ALTER TABLE `users`
ADD COLUMN `register_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    ADD COLUMN `last_login_time` DATETIME COMMENT '最后登录时间';
-- 6. 添加乐观锁版本号
ALTER TABLE `users`
ADD COLUMN `data_version` INT DEFAULT 1 COMMENT '乐观锁版本号，用于并发控制';
-- 7. 创建新索引
CREATE INDEX `idx_phone_encrypted` ON `users`(`phone_encrypted`);
CREATE INDEX `idx_member_level` ON `users`(`member_level`);
CREATE INDEX `idx_register_time` ON `users`(`register_time`);
-- 8. 为现有用户设置默认值
UPDATE `users`
SET `phone_encrypted` = SHA2(CONCAT(phone, 'salt'), 256),
    `nickname` = CONCAT('用户', SUBSTRING(phone, -4)),
    `register_time` = COALESCE(created_at, CURRENT_TIMESTAMP)
WHERE `phone_encrypted` IS NULL;
-- 9. 将 phone_encrypted 和 nickname 设置为 NOT NULL
ALTER TABLE `users`
MODIFY COLUMN `phone_encrypted` VARCHAR(128) NOT NULL COMMENT '手机号加密存储，用于安全匹配',
    MODIFY COLUMN `nickname` VARCHAR(50) NOT NULL COMMENT '用户昵称';