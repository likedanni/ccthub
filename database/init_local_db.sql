-- 长治文旅平台 - 本地数据库初始化脚本
-- 创建日期: 2025-12-15
-- 说明: 直接在本地 MySQL 8.0 执行，不使用 Flyway
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `cct_hub` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `cct_hub`;
-- ========================================
-- 票种表（tickets）
-- ========================================
CREATE TABLE IF NOT EXISTS `tickets` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '票种ID',
    `scenic_spot_id` BIGINT NOT NULL COMMENT '景区ID',
    `name` VARCHAR(200) NOT NULL COMMENT '票种名称',
    `type` TINYINT NOT NULL COMMENT '票种类型 1-单票 2-联票 3-套票',
    `description` TEXT COMMENT '票种描述',
    `validity_type` TINYINT NOT NULL COMMENT '有效期类型 1-指定日期 2-有效天数',
    `valid_days` INT COMMENT '有效天数',
    `advance_days` INT COMMENT '提前预订天数',
    `refund_policy` JSON NOT NULL COMMENT '退款规则',
    `change_policy` JSON NOT NULL COMMENT '改签规则',
    `limit_per_user` INT COMMENT '每用户限购数量',
    `limit_per_order` INT COMMENT '每订单限购数量',
    `limit_per_day` INT COMMENT '每日限购数量',
    `require_real_name` TINYINT(1) DEFAULT 0 COMMENT '是否需要实名',
    `require_id_card` TINYINT(1) DEFAULT 0 COMMENT '是否需要身份证',
    `verification_mode` TINYINT COMMENT '核验方式 1-二维码 2-人脸 3-身份证',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_scenic_spot_id` (`scenic_spot_id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '票种表';
-- ========================================
-- 票价表（ticket_prices）
-- ========================================
CREATE TABLE IF NOT EXISTS `ticket_prices` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '票价ID',
    `ticket_id` BIGINT NOT NULL COMMENT '票种ID',
    `price_date` DATE NOT NULL COMMENT '价格日期',
    `price_type` TINYINT NOT NULL COMMENT '价格类型 1-成人 2-学生 3-儿童 4-老年',
    `original_price` DECIMAL(10, 2) NOT NULL COMMENT '原价',
    `sell_price` DECIMAL(10, 2) NOT NULL COMMENT '售价',
    `inventory_total` INT NOT NULL COMMENT '总库存',
    `inventory_available` INT NOT NULL COMMENT '可用库存',
    `inventory_locked` INT NOT NULL DEFAULT 0 COMMENT '锁定库存',
    `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_ticket_date_type` (`ticket_id`, `price_date`, `price_type`),
    KEY `idx_ticket_id` (`ticket_id`),
    KEY `idx_price_date` (`price_date`),
    KEY `idx_is_active` (`is_active`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '票价表';
-- ========================================
-- 订单表（orders）
-- ========================================
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `scenic_spot_id` BIGINT NOT NULL COMMENT '景区ID',
    `ticket_id` BIGINT NOT NULL COMMENT '票种ID',
    `visit_date` DATE NOT NULL COMMENT '游玩日期',
    `visitor_count` INT NOT NULL COMMENT '游客数量',
    `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总额',
    `discount_amount` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '优惠金额',
    `actual_amount` DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态',
    `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系人电话',
    `remark` VARCHAR(500) COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `pay_time` DATETIME COMMENT '支付时间',
    `cancel_time` DATETIME COMMENT '取消时间',
    `refund_time` DATETIME COMMENT '退款时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_scenic_spot_id` (`scenic_spot_id`),
    KEY `idx_status` (`status`),
    KEY `idx_visit_date` (`visit_date`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表';
-- ========================================
-- 订单明细表（order_items）
-- ========================================
CREATE TABLE IF NOT EXISTS `order_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `ticket_price_id` BIGINT NOT NULL COMMENT '票价ID',
    `visitor_name` VARCHAR(50) NOT NULL COMMENT '游客姓名',
    `visitor_id_card` VARCHAR(100) COMMENT '身份证号（加密）',
    `visitor_phone` VARCHAR(20) COMMENT '联系电话',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '单价',
    `verification_code` VARCHAR(64) NOT NULL COMMENT '核销码（UUID）',
    `qr_code_url` VARCHAR(255) COMMENT '二维码URL',
    `is_verified` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已核销',
    `verify_time` DATETIME COMMENT '核销时间',
    `verify_staff_id` BIGINT COMMENT '核销员ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_verification_code` (`verification_code`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_is_verified` (`is_verified`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单明细表';
-- ========================================
-- 插入测试票种数据
-- ========================================
INSERT IGNORE INTO `tickets` (
        `id`,
        `scenic_spot_id`,
        `name`,
        `type`,
        `description`,
        `validity_type`,
        `valid_days`,
        `advance_days`,
        `refund_policy`,
        `change_policy`,
        `limit_per_user`,
        `limit_per_order`,
        `limit_per_day`,
        `require_real_name`,
        `require_id_card`,
        `verification_mode`,
        `status`
    )
VALUES (
        1,
        1,
        '太行山大峡谷成人票',
        1,
        '适用于18-60周岁成人游客',
        1,
        NULL,
        1,
        '{"within_24h": 0.5, "within_48h": 0.7, "over_48h": 1.0}',
        '{"allowed": true, "fee": 10.0, "advance_hours": 24}',
        10,
        5,
        NULL,
        1,
        1,
        1,
        1
    ),
    (
        2,
        1,
        '太行山大峡谷优惠票',
        1,
        '适用于学生、老年人、军人等优惠人群',
        1,
        NULL,
        1,
        '{"within_24h": 0.5, "within_48h": 0.7, "over_48h": 1.0}',
        '{"allowed": true, "fee": 5.0, "advance_hours": 24}',
        10,
        5,
        NULL,
        1,
        1,
        1,
        1
    ),
    (
        3,
        1,
        '太行山大峡谷儿童票',
        1,
        '适用于1.2米以下儿童',
        1,
        NULL,
        1,
        '{"within_24h": 0.8, "within_48h": 0.9, "over_48h": 1.0}',
        '{"allowed": true, "fee": 0.0, "advance_hours": 12}',
        10,
        5,
        NULL,
        0,
        0,
        1,
        1
    );
-- ========================================
-- 插入测试票价数据（未来7天）
-- ========================================
INSERT IGNORE INTO `ticket_prices` (
        `ticket_id`,
        `price_date`,
        `price_type`,
        `original_price`,
        `sell_price`,
        `inventory_total`,
        `inventory_available`,
        `is_active`
    )
SELECT 1 AS ticket_id,
    DATE_ADD(CURDATE(), INTERVAL days.n DAY) AS price_date,
    1 AS price_type,
    128.00 AS original_price,
    98.00 AS sell_price,
    1000 AS inventory_total,
    1000 AS inventory_available,
    1 AS is_active
FROM (
        SELECT 0 AS n
        UNION ALL
        SELECT 1
        UNION ALL
        SELECT 2
        UNION ALL
        SELECT 3
        UNION ALL
        SELECT 4
        UNION ALL
        SELECT 5
        UNION ALL
        SELECT 6
    ) AS days;