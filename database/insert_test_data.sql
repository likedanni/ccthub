-- 插入活动和秒杀测试数据
USE `cct-hub`;

-- 临时禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 插入活动测试数据
INSERT INTO activities (merchant_id, name, type, description, starts_at, ends_at, location, participation_limit, requirement_type, requirement_value, reward_config, audit_status, status) VALUES
(1, '长治太行山打卡活动', 1, '完成3个打卡点即可获得100积分奖励，探索太行山美景', '2025-12-20 08:00:00', '2025-12-30 20:00:00', '长治市太行山景区', 1000, 1, 0, '{"type":"points","value":100,"description":"完成打卡获得100积分"}', 1, 1),
(1, '景区会员积分翻倍活动', 2, '活动期间所有景区消费积分翻倍，会员专享福利', '2025-12-18 00:00:00', '2025-12-25 23:59:59', '全部景区', 5000, 2, 1, '{"type":"points","multiplier":2,"description":"消费积分翻倍"}', 1, 1),
(2, '圣诞主题促销活动', 3, '圣诞特惠，景区门票8折优惠，限时抢购', '2025-12-24 00:00:00', '2025-12-26 23:59:59', '全部景区', 3000, 1, 0, '{"type":"discount","value":0.8,"description":"门票8折"}', 1, 0),
(2, '元旦跨年打卡挑战', 1, '跨年夜打卡，赢取新年礼包，见证2026到来', '2025-12-31 18:00:00', '2026-01-01 02:00:00', '长治市人民广场', 500, 1, 0, '{"type":"gift","description":"新年礼包"}', 0, 0);

-- 插入秒杀测试数据
INSERT INTO seckill_events (product_id, title, seckill_price, original_price, total_inventory, available_inventory, limit_per_user, starts_at, ends_at, status) VALUES
(1, '太行山景区门票限时秒杀', 29.90, 99.00, 100, 100, 2, '2025-12-20 10:00:00', '2025-12-20 12:00:00', 0),
(2, '黄崖洞景区门票特惠', 39.90, 120.00, 200, 200, 3, '2025-12-21 14:00:00', '2025-12-21 16:00:00', 0),
(3, '平遥古城联票秒杀', 59.90, 180.00, 150, 150, 1, '2025-12-22 09:00:00', '2025-12-22 11:00:00', 1),
(4, '云冈石窟门票抢购', 49.90, 150.00, 80, 45, 2, '2025-12-19 10:00:00', '2025-12-19 12:00:00', 2);

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

SELECT '数据插入成功！' AS result;
SELECT COUNT(*) AS activity_count FROM activities;
SELECT COUNT(*) AS seckill_count FROM seckill_events;
