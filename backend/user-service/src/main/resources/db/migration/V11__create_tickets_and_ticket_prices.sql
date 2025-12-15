-- V11: 创建票种(tickets)与票价库存日历表(ticket_prices)
-- 票种模板表，定义某景区的一种门票规则
CREATE TABLE `tickets` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '票种ID，主键自增',
  `scenic_spot_id` bigint NOT NULL COMMENT '景区ID',
  `name` varchar(200) NOT NULL COMMENT '票种名称',
  `type` tinyint NOT NULL COMMENT '票种类型: 1-单票, 2-联票, 3-套票',
  `description` text COMMENT '票种描述',
  `validity_type` tinyint NOT NULL COMMENT '有效期类型: 1-指定日期, 2-有效天数',
  `valid_days` int COMMENT '有效期天数（当validity_type=2时使用）',
  `advance_days` int DEFAULT 7 COMMENT '可提前预订天数',
  `refund_policy` json NOT NULL COMMENT '退款规则配置，JSON格式',
  `change_policy` json NOT NULL COMMENT '改签规则配置，JSON格式',
  `limit_per_user` int DEFAULT 5 COMMENT '每用户限购数量',
  `limit_per_order` int DEFAULT 5 COMMENT '每订单限购数量',
  `limit_per_day` int COMMENT '每日限购数量',
  `require_real_name` boolean DEFAULT true COMMENT '是否需要实名',
  `require_id_card` boolean DEFAULT true COMMENT '是否需要身份证',
  `verification_mode` tinyint DEFAULT 1 COMMENT '核验方式: 1-二维码, 2-人脸, 3-身份证',
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-上架, 0-下架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_scenic_spot_id` (`scenic_spot_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`),
  CONSTRAINT `fk_tickets_scenic_spot` FOREIGN KEY (`scenic_spot_id`) REFERENCES `scenic_spots` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = '票种模板表，定义某景区的一种门票规则';

-- 票价与库存日历表，支持每天不同的价格和独立的库存管理
CREATE TABLE `ticket_prices` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '票价ID，主键自增',
  `ticket_id` bigint NOT NULL COMMENT '票种ID',
  `price_date` date NOT NULL COMMENT '适用日期',
  `price_type` tinyint NOT NULL COMMENT '价格类型: 1-成人票, 2-学生票, 3-儿童票, 4-老年票',
  `original_price` decimal(10, 2) NOT NULL COMMENT '门市价',
  `sell_price` decimal(10, 2) NOT NULL COMMENT '销售价',
  `inventory_total` int NOT NULL COMMENT '总库存',
  `inventory_available` int NOT NULL COMMENT '可用库存',
  `inventory_locked` int DEFAULT 0 COMMENT '锁定库存（下单未支付）',
  `is_active` boolean DEFAULT true COMMENT '该日期价格是否生效',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT 0 COMMENT '乐观锁版本号，用于防超卖',
  INDEX `idx_ticket_id` (`ticket_id`),
  INDEX `idx_price_date` (`price_date`),
  INDEX `idx_date_active` (`price_date`, `is_active`),
  INDEX `idx_is_active` (`is_active`),
  UNIQUE INDEX `idx_ticket_date_type` (`ticket_id`, `price_date`, `price_type`),
  CONSTRAINT `fk_ticket_prices_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = '票价与库存日历表，支持每天不同的价格和独立的库存管理';

-- 插入测试票种数据（太行山大峡谷示例）
INSERT INTO `tickets` 
  (`scenic_spot_id`, `name`, `type`, `description`, `validity_type`, `valid_days`, `advance_days`, 
   `refund_policy`, `change_policy`, `limit_per_user`, `limit_per_order`, `require_real_name`, `require_id_card`, `verification_mode`, `status`)
VALUES 
  (1, '太行山大峡谷成人票', 1, '适用于身高1.5米以上游客，可游览景区全部开放区域', 1, NULL, 7,
   '{"within_24h": 0.5, "within_48h": 0.7, "over_48h": 1.0}', 
   '{"allowed": true, "fee": 10.0, "advance_hours": 24}',
   5, 5, true, true, 1, 1),
  (1, '太行山大峡谷优惠票', 1, '适用于学生、军人、老年人等优惠人群（需出示有效证件）', 1, NULL, 7,
   '{"within_24h": 0.5, "within_48h": 0.7, "over_48h": 1.0}', 
   '{"allowed": true, "fee": 10.0, "advance_hours": 24}',
   5, 5, true, true, 1, 1),
  (1, '太行山大峡谷儿童票', 1, '适用于身高1.2-1.5米儿童（不含1.2米以下）', 1, NULL, 7,
   '{"within_24h": 0.5, "within_48h": 0.7, "over_48h": 1.0}', 
   '{"allowed": true, "fee": 5.0, "advance_hours": 24}',
   10, 10, false, false, 1, 1);

-- 插入测试票价数据（未来7天）
INSERT INTO `ticket_prices` 
  (`ticket_id`, `price_date`, `price_type`, `original_price`, `sell_price`, `inventory_total`, `inventory_available`)
SELECT 
  1 as ticket_id,
  DATE_ADD(CURDATE(), INTERVAL n DAY) as price_date,
  1 as price_type,
  120.00 as original_price,
  98.00 as sell_price,
  1000 as inventory_total,
  1000 as inventory_available
FROM (
  SELECT 0 as n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL 
  SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
) days;
