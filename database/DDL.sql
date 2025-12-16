-- ============================================================
-- 长治文旅大生态服务平台 - 完整数据库DDL脚本
-- 版本: 3.0
-- 包含：33张核心表 + 完整外键约束 + 索引优化
-- 说明：每个字段都有中文注释，适合团队协作和文档维护
-- ============================================================
-- 注意：建议按顺序执行，确保先创建被引用的表
-- 1. 平台用户核心信息表
CREATE TABLE `users` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "用户ID，主键自增",
  `phone` varchar(20) UNIQUE NOT NULL COMMENT "手机号码，唯一",
  `phone_encrypted` varchar(128) NOT NULL COMMENT "手机号加密存储，用于安全匹配",
  `nickname` varchar(50) NOT NULL COMMENT "用户昵称",
  `avatar_url` varchar(500) COMMENT "头像URL地址",
  `id_card_encrypted` varchar(128) COMMENT "身份证号加密存储",
  `real_name` varchar(50) COMMENT "真实姓名",
  `member_level` tinyint DEFAULT 1 COMMENT "会员等级: 1-普通, 2-白银, 3-黄金, 4-钻石",
  `growth_value` int DEFAULT 0 COMMENT "成长值，用于会员升级",
  `total_points` int DEFAULT 0 COMMENT "累计积分总额",
  `available_points` int DEFAULT 0 COMMENT "当前可用积分",
  `wallet_balance` decimal(10, 2) DEFAULT 0 COMMENT "钱包余额(元)",
  `status` tinyint DEFAULT 1 COMMENT "用户状态: 1-正常, 0-冻结, 2-注销",
  `register_time` datetime DEFAULT (now()) COMMENT "注册时间",
  `last_login_time` datetime COMMENT "最后登录时间",
  `data_version` int DEFAULT 1 COMMENT "乐观锁版本号，用于并发控制",
  INDEX `idx_phone` (`phone`) COMMENT "手机号索引",
  INDEX `idx_phone_encrypted` (`phone_encrypted`) COMMENT "加密手机号索引",
  INDEX `idx_member_level` (`member_level`) COMMENT "会员等级索引",
  INDEX `idx_register_time` (`register_time`) COMMENT "注册时间索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引"
) COMMENT = "平台用户核心信息表，预计数据量50万+。敏感信息均加密存储。建议数据保留至注销后3年。";
-- 2. 景区基础信息表
CREATE TABLE `scenic_spots` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "景区ID，主键自增",
  `name` varchar(200) NOT NULL COMMENT "景区名称",
  `level` varchar(20) COMMENT "景区级别，如: A, AA, 5A",
  `introduction` text COMMENT "景区详细介绍",
  `province` varchar(50) NOT NULL COMMENT "所在省份",
  `city` varchar(50) NOT NULL COMMENT "所在城市",
  `address` varchar(500) NOT NULL COMMENT "详细地址",
  `longitude` decimal(10, 6) COMMENT "经度，用于地图定位",
  `latitude` decimal(10, 6) COMMENT "纬度，用于地图定位",
  `opening_hours` varchar(200) COMMENT "开放时间描述",
  `contact_phone` varchar(20) COMMENT "景区联系电话",
  `cover_image` varchar(500) COMMENT "封面图片URL",
  `status` tinyint DEFAULT 1 COMMENT "状态: 1-开放, 0-关闭",
  `create_time` datetime DEFAULT (now()) COMMENT "创建时间",
  INDEX `idx_city` (`city`) COMMENT "城市索引",
  INDEX `idx_level` (`level`) COMMENT "景区级别索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引"
) COMMENT = "文旅资源基础信息表，可扩展为多类型资源（博物馆、公园等）。";
-- 3. 商户信息表
CREATE TABLE `merchants` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "商户ID，主键自增",
  `name` varchar(200) NOT NULL COMMENT "商户名称",
  `type` tinyint NOT NULL COMMENT "商户类型: 1-景区, 2-餐饮, 3-文创, 4-生鲜便利",
  `cooperation_type` tinyint NOT NULL COMMENT "合作类型: 1-直营, 2-联营, 3-加盟",
  `contact_person` varchar(50) NOT NULL COMMENT "联系人姓名",
  `contact_phone` varchar(20) NOT NULL COMMENT "联系电话",
  `business_license` varchar(100) COMMENT "营业执照号码",
  `province` varchar(50) NOT NULL COMMENT "所在省份",
  `city` varchar(50) NOT NULL COMMENT "所在城市",
  `district` varchar(50) COMMENT "所在区县",
  `address` varchar(500) NOT NULL COMMENT "详细地址",
  `longitude` decimal(10, 6) COMMENT "经度，用于地图和附近推荐",
  `latitude` decimal(10, 6) COMMENT "纬度，用于地图和附近推荐",
  `settlement_rate` decimal(5, 4) COMMENT "平台结算费率",
  `audit_status` tinyint DEFAULT 0 COMMENT "审核状态: 0-待审核, 1-通过, 2-拒绝",
  `status` tinyint DEFAULT 1 COMMENT "状态: 1-正常, 0-停用",
  `level` tinyint DEFAULT 1 COMMENT "商户等级",
  `score` decimal(3, 2) DEFAULT 5 COMMENT "商户评分，1-5分",
  `create_time` datetime DEFAULT (now()) COMMENT "创建时间",
  INDEX `idx_city` (`city`) COMMENT "城市索引",
  INDEX `idx_type` (`type`) COMMENT "商户类型索引",
  INDEX `idx_cooperation_type` (`cooperation_type`) COMMENT "合作类型索引",
  INDEX `idx_audit_status` (`audit_status`) COMMENT "审核状态索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引"
) COMMENT = "入驻商户信息表，支持多种合作类型和结算规则。";
-- 4. 商品SPU表（标准产品单元）
CREATE TABLE `products` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "商品ID，主键自增",
  `merchant_id` bigint COMMENT "所属商户ID",
  `scenic_spot_id` bigint COMMENT "关联景区ID，非景区商品可为空",
  `type` tinyint NOT NULL COMMENT "商品类型: 1-门票, 2-餐饮, 3-文创, 4-生鲜",
  `category_id` bigint COMMENT "商品分类ID",
  `name` varchar(200) NOT NULL COMMENT "商品名称",
  `subtitle` varchar(200) COMMENT "副标题/卖点",
  `main_image` varchar(500) COMMENT "商品主图URL",
  `image_list` json COMMENT "商品图册，JSON数组格式",
  `description` text COMMENT "商品描述",
  `spec_template` json COMMENT "规格模板，JSON格式",
  `point_ratio` decimal(5, 4) DEFAULT 0.01 COMMENT "积分赠送比例",
  `audit_status` tinyint DEFAULT 0 COMMENT "审核状态: 0-待审, 1-通过, 2-拒绝",
  `shelf_status` tinyint DEFAULT 0 COMMENT "上下架状态: 0-下架, 1-上架",
  `create_time` datetime DEFAULT (now()) COMMENT "创建时间",
  `update_time` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_merchant_id` (`merchant_id`) COMMENT "商户ID索引",
  INDEX `idx_type` (`type`) COMMENT "商品类型索引",
  INDEX `idx_scenic_spot_id` (`scenic_spot_id`) COMMENT "景区ID索引",
  INDEX `idx_shelf_status` (`shelf_status`) COMMENT "上架状态索引",
  INDEX `idx_audit_status` (`audit_status`) COMMENT "审核状态索引",
  CONSTRAINT `fk_products_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`id`) ON DELETE
  SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_products_scenic_spot` FOREIGN KEY (`scenic_spot_id`) REFERENCES `scenic_spots` (`id`) ON DELETE
  SET NULL ON UPDATE CASCADE
) COMMENT = "标准化商品信息表(SPU)，与具体规格(SKU)分离。";
-- 5. 商品SKU表（库存保持单元）
CREATE TABLE `product_skus` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "SKU ID，主键自增",
  `product_id` bigint NOT NULL COMMENT "商品ID",
  `sku_code` varchar(50) UNIQUE NOT NULL COMMENT "SKU编码，唯一",
  `sku_name` varchar(200) NOT NULL COMMENT "SKU名称，如: 成人票-上午场",
  `specs` json NOT NULL COMMENT "具体规格，JSON格式，如: {\"时段\": \"上午\", \"人群\": \"成人\"}",
  `original_price` decimal(10, 2) NOT NULL COMMENT "门市价/原价",
  `sell_price` decimal(10, 2) NOT NULL COMMENT "销售价",
  `cost_price` decimal(10, 2) COMMENT "成本价，用于核算",
  `stock_total` int DEFAULT 0 COMMENT "总库存",
  `stock_available` int DEFAULT 0 COMMENT "可用库存",
  `stock_locked` int DEFAULT 0 COMMENT "锁定库存（下单未支付）",
  `status` tinyint DEFAULT 1 COMMENT "状态: 1-启用, 0-禁用",
  `sales_count` int DEFAULT 0 COMMENT "销售数量",
  INDEX `idx_product_id` (`product_id`) COMMENT "商品ID索引",
  INDEX `idx_sku_code` (`sku_code`) COMMENT "SKU编码索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  INDEX `idx_product_status` (`product_id`, `status`) COMMENT "商品状态联合索引",
  CONSTRAINT `fk_product_skus_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "商品最小库存单位表(SKU)，管理价格、库存和销售。";
-- 6. 优惠券模板表
CREATE TABLE `coupons` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "优惠券ID，主键自增",
  `name` varchar(100) NOT NULL COMMENT "优惠券名称",
  `type` tinyint NOT NULL COMMENT "券类型: 1-满减券, 2-折扣券, 3-代金券",
  `value` decimal(10, 2) NOT NULL COMMENT "优惠值: 满减金额、折扣(0.9)或固定金额",
  `min_spend` decimal(10, 2) COMMENT "最低消费金额门槛",
  `applicable_type` tinyint NOT NULL COMMENT "适用范围: 1-全平台, 2-指定商户, 3-指定商品",
  `applicable_ids` json COMMENT "适用的商户或商品ID列表，JSON数组格式",
  `validity_type` tinyint NOT NULL COMMENT "有效期类型: 1-固定时段, 2-领取后生效",
  `valid_days` int COMMENT "领取后有效天数（当validity_type=2时）",
  `starts_at` datetime COMMENT "有效期开始时间",
  `expires_at` datetime COMMENT "有效期结束时间",
  `total_quantity` int COMMENT "发放总张数，null表示不限量",
  `remaining_quantity` int COMMENT "剩余可发放张数",
  `limit_per_user` int DEFAULT 1 COMMENT "每人限领张数",
  `status` tinyint DEFAULT 0 COMMENT "状态: 0-未开始, 1-发放中, 2-已结束, 3-停用",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  INDEX `idx_applicable_type` (`applicable_type`) COMMENT "适用范围索引",
  INDEX `idx_expires_at` (`expires_at`) COMMENT "过期时间索引"
) COMMENT = "优惠券定义模板。用户领取后生成 user_coupons 记录。";
-- 7. 订单主表
CREATE TABLE `orders` (
  `order_no` varchar(32) PRIMARY KEY COMMENT "业务订单号，如: ORDER202412110001",
  `user_id` bigint NOT NULL COMMENT "用户ID，分片键",
  `merchant_id` bigint COMMENT "商户ID",
  `order_type` tinyint NOT NULL COMMENT "订单类型: 1-门票, 2-实物商品, 3-活动",
  `total_amount` decimal(10, 2) NOT NULL COMMENT "订单总金额",
  `discount_amount` decimal(10, 2) DEFAULT 0 COMMENT "优惠总金额",
  `pay_amount` decimal(10, 2) NOT NULL COMMENT "实际支付金额",
  `point_amount` decimal(10, 2) DEFAULT 0 COMMENT "积分抵扣金额",
  `point_earned` int DEFAULT 0 COMMENT "本次获得积分",
  `platform_fee` decimal(10, 2) DEFAULT 0.00 COMMENT "平台服务费",
  `payment_method` varchar(50) COMMENT "支付方式: wechat, alipay, balance",
  `payment_status` tinyint DEFAULT 0 COMMENT "支付状态: 0-待支付, 1-成功, 2-失败, 3-已退款, 4-处理中",
  `order_status` tinyint DEFAULT 0 COMMENT "订单状态: 0-待付款, 1-待使用, 2-已完成, 3-已取消, 4-退款中",
  `refund_status` tinyint DEFAULT 0 COMMENT "退款状态: 0-无退款, 1-退款中, 2-成功, 3-失败",
  `outer_order_no` varchar(64) COMMENT "外部订单号(如OTA订单号)",
  `create_time` datetime DEFAULT (now()) COMMENT "创建时间",
  `update_time` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_merchant_id` (`merchant_id`) COMMENT "商户ID索引",
  INDEX `idx_create_time` (`create_time`) COMMENT "创建时间索引",
  INDEX `idx_order_status` (`order_status`) COMMENT "订单状态索引",
  INDEX `idx_payment_status` (`payment_status`) COMMENT "支付状态索引",
  INDEX `idx_user_status_time` (`user_id`, `order_status`, `create_time` DESC) COMMENT "用户状态时间联合索引",
  INDEX `idx_merchant_status_time` (
    `merchant_id`,
    `order_status`,
    `create_time` DESC
  ) COMMENT "商户状态时间联合索引",
  INDEX `idx_outer_order_no` (`outer_order_no`) COMMENT "外部订单号索引",
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_orders_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`id`) ON DELETE
  SET NULL ON UPDATE CASCADE
) COMMENT = "订单主表，按user_id分片，需考虑按create_time分表。状态机复杂，需仔细设计。";
-- 8. 订单明细表
CREATE TABLE `order_items` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "明细ID，主键自增",
  `order_no` varchar(32) NOT NULL COMMENT "订单号",
  `product_id` bigint COMMENT "商品ID",
  `product_name` varchar(200) NOT NULL COMMENT "下单时的商品名称快照",
  `sku_id` bigint COMMENT "SKU ID",
  `sku_specs` varchar(500) COMMENT "下单时的规格快照",
  `unit_price` decimal(10, 2) NOT NULL COMMENT "单价",
  `quantity` int NOT NULL COMMENT "购买数量",
  `subtotal` decimal(10, 2) NOT NULL COMMENT "小计金额",
  `verification_code` varchar(50) UNIQUE COMMENT "核销码/券码，唯一",
  `verification_status` tinyint DEFAULT 0 COMMENT "核销状态: 0-未核销, 1-已核销, 2-已过期",
  `ticket_date` date COMMENT "票务使用日期",
  `visitor_name` varchar(50) COMMENT "游客姓名（明文，仅用于核验时显示）",
  `visitor_name_encrypted` varchar(128) COMMENT "加密存储的游客姓名",
  INDEX `idx_order_no` (`order_no`) COMMENT "订单号索引",
  INDEX `idx_verification_code` (`verification_code`) COMMENT "核销码索引",
  INDEX `idx_ticket_date` (`ticket_date`) COMMENT "使用日期索引",
  INDEX `idx_verification_status` (`verification_status`) COMMENT "核销状态索引",
  CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_no`) REFERENCES `orders` (`order_no`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE
  SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_order_items_sku` FOREIGN KEY (`sku_id`) REFERENCES `product_skus` (`id`) ON DELETE
  SET NULL ON UPDATE CASCADE
) COMMENT = "订单明细表，记录每件商品信息。核销码在此表管理。";
-- 9. 支付流水表
CREATE TABLE `payments` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "支付ID，主键自增",
  `payment_no` varchar(32) UNIQUE NOT NULL COMMENT "支付系统流水号",
  `order_no` varchar(32) NOT NULL COMMENT "订单号",
  `payment_type` varchar(20) NOT NULL COMMENT "支付类型: wechat, alipay, unionpay",
  `payment_channel` varchar(50) NOT NULL COMMENT "支付渠道: miniapp, app, h5, native",
  `payment_amount` decimal(10, 2) NOT NULL COMMENT "支付金额",
  `status` tinyint NOT NULL COMMENT "支付状态: 0-待支付, 1-成功, 2-失败, 3-关闭, 4-处理中",
  `third_party_no` varchar(64) COMMENT "第三方支付平台流水号",
  `payer_id` varchar(100) COMMENT "支付方标识（如微信openid）",
  `payment_time` datetime COMMENT "支付时间",
  `callback_time` datetime COMMENT "支付回调时间",
  `create_time` datetime DEFAULT (now()) COMMENT "创建时间",
  INDEX `idx_order_no` (`order_no`) COMMENT "订单号索引",
  INDEX `idx_payment_no` (`payment_no`) COMMENT "支付流水号索引",
  INDEX `idx_third_party_no` (`third_party_no`) COMMENT "第三方流水号索引",
  INDEX `idx_create_time` (`create_time`) COMMENT "创建时间索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  CONSTRAINT `fk_payments_order` FOREIGN KEY (`order_no`) REFERENCES `orders` (`order_no`) ON DELETE RESTRICT ON UPDATE CASCADE
) COMMENT = "支付流水表，与订单1:1或1:N。需严格对账。";
-- 10. 用户收货地址表
CREATE TABLE `user_addresses` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "地址ID，主键自增",
  `user_id` bigint NOT NULL COMMENT "用户ID",
  `recipient_name` varchar(50) NOT NULL COMMENT "收货人姓名",
  `recipient_phone` varchar(20) NOT NULL COMMENT "收货人电话",
  `province` varchar(50) NOT NULL COMMENT "省份",
  `city` varchar(50) NOT NULL COMMENT "城市",
  `district` varchar(50) NOT NULL COMMENT "区县",
  `detail_address` varchar(200) NOT NULL COMMENT "详细地址",
  `postal_code` varchar(10) COMMENT "邮政编码",
  `is_default` boolean DEFAULT false COMMENT "是否默认地址",
  `create_time` datetime DEFAULT (now()) COMMENT "创建时间",
  `update_time` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_user_default` (`user_id`, `is_default`) COMMENT "用户默认地址索引",
  CONSTRAINT `fk_user_addresses_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "用户收货地址表，用于实物商品（文创、生鲜）邮寄。";
-- 11. 商户员工角色表
CREATE TABLE `merchant_employee_roles` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "角色ID，主键自增",
  `merchant_id` bigint NOT NULL COMMENT "商户ID",
  `role_name` varchar(50) NOT NULL COMMENT "角色名称",
  `permissions` json COMMENT "该角色拥有的权限集合，JSON格式",
  `is_system` boolean DEFAULT false COMMENT "是否为系统预置角色",
  `create_time` datetime DEFAULT (now()) COMMENT "创建时间",
  INDEX `idx_merchant_id` (`merchant_id`) COMMENT "商户ID索引",
  CONSTRAINT `fk_merchant_roles_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "商户员工角色定义表，用于细粒度权限管理。";
-- 12. 商户员工表
CREATE TABLE `merchant_employees` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "员工ID，主键自增",
  `merchant_id` bigint NOT NULL COMMENT "商户ID",
  `user_id` bigint NOT NULL COMMENT "关联平台用户ID",
  `employee_name` varchar(50) NOT NULL COMMENT "员工姓名",
  `employee_phone` varchar(20) NOT NULL COMMENT "员工电话",
  `role` tinyint NOT NULL COMMENT "角色类型: 1-管理员, 2-运营员, 3-核销员",
  `status` tinyint DEFAULT 1 COMMENT "状态: 1-正常, 0-停用",
  `create_time` datetime DEFAULT (now()) COMMENT "创建时间",
  INDEX `idx_merchant_id` (`merchant_id`) COMMENT "商户ID索引",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_role` (`role`) COMMENT "角色类型索引",
  UNIQUE INDEX `idx_merchant_user` (`merchant_id`, `user_id`) COMMENT "商户用户唯一索引",
  CONSTRAINT `fk_merchant_employees_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_merchant_employees_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "商户员工绑定表。细粒度权限通过角色表(merchant_employee_roles)管理。";
-- 13. 商户员工角色分配表
CREATE TABLE `merchant_employee_role_assignments` (
  `employee_id` bigint NOT NULL COMMENT "员工ID",
  `role_id` bigint NOT NULL COMMENT "角色ID",
  `assigned_at` datetime DEFAULT (now()) COMMENT "分配时间",
  PRIMARY KEY (`employee_id`, `role_id`) COMMENT "联合主键",
  CONSTRAINT `fk_role_assignments_employee` FOREIGN KEY (`employee_id`) REFERENCES `merchant_employees` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_assignments_role` FOREIGN KEY (`role_id`) REFERENCES `merchant_employee_roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "商户员工与角色分配关系表。";
-- 14. 票种模板表
CREATE TABLE `tickets` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "票种ID，主键自增",
  `scenic_spot_id` bigint NOT NULL COMMENT "景区ID",
  `name` varchar(200) NOT NULL COMMENT "票种名称",
  `type` tinyint NOT NULL COMMENT "票种类型: 1-单票, 2-联票, 3-套票",
  `description` text COMMENT "票种描述",
  `validity_type` tinyint NOT NULL COMMENT "有效期类型: 1-指定日期, 2-有效天数",
  `valid_days` int COMMENT "有效期天数（当validity_type=2时使用）",
  `advance_days` int DEFAULT 7 COMMENT "可提前预订天数",
  `refund_policy` json NOT NULL COMMENT "退款规则配置，JSON格式",
  `change_policy` json NOT NULL COMMENT "改签规则配置，JSON格式",
  `limit_per_user` int DEFAULT 5 COMMENT "每用户限购数量",
  `limit_per_order` int DEFAULT 5 COMMENT "每订单限购数量",
  `limit_per_day` int COMMENT "每日限购数量",
  `require_real_name` boolean DEFAULT true COMMENT "是否需要实名",
  `require_id_card` boolean DEFAULT true COMMENT "是否需要身份证",
  `verification_mode` tinyint DEFAULT 1 COMMENT "核验方式: 1-二维码, 2-人脸, 3-身份证",
  `status` tinyint DEFAULT 1 COMMENT "状态: 1-上架, 0-下架",
  `create_time` datetime DEFAULT (now()) COMMENT "创建时间",
  `update_time` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_scenic_spot_id` (`scenic_spot_id`) COMMENT "景区ID索引",
  INDEX `idx_type` (`type`) COMMENT "票种类型索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  CONSTRAINT `fk_tickets_scenic_spot` FOREIGN KEY (`scenic_spot_id`) REFERENCES `scenic_spots` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "票种模板表，定义某景区的一种门票规则。实际销售的是其下某一天的具体库存（ticket_prices）。";
-- 15. 票价库存日历表
CREATE TABLE `ticket_prices` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "票价ID，主键自增",
  `ticket_id` bigint NOT NULL COMMENT "票种ID",
  `price_date` date NOT NULL COMMENT "适用日期",
  `price_type` tinyint NOT NULL COMMENT "价格类型: 1-成人票, 2-学生票, 3-儿童票, 4-老年票",
  `original_price` decimal(10, 2) NOT NULL COMMENT "门市价",
  `sell_price` decimal(10, 2) NOT NULL COMMENT "销售价",
  `inventory_total` int NOT NULL COMMENT "总库存",
  `inventory_available` int NOT NULL COMMENT "可用库存",
  `inventory_locked` int NOT NULL DEFAULT 0 COMMENT "锁定库存",
  `is_active` boolean DEFAULT true COMMENT "该日期价格是否生效",
  `version` int DEFAULT 0 COMMENT "乐观锁版本号",
  `create_time` datetime DEFAULT (now()) COMMENT "创建时间",
  `update_time` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_ticket_id` (`ticket_id`) COMMENT "票种ID索引",
  INDEX `idx_price_date` (`price_date`) COMMENT "适用日期索引",
  INDEX `idx_date_active` (`price_date`, `is_active`) COMMENT "日期生效状态联合索引",
  INDEX `idx_is_active` (`is_active`) COMMENT "生效状态索引",
  UNIQUE INDEX `idx_ticket_date_type` (`ticket_id`, `price_date`, `price_type`) COMMENT "票种日期类型唯一索引",
  CONSTRAINT `fk_ticket_prices_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "票价与库存日历表。支持每天不同的价格和独立的库存管理，是实际销售的单位。";
-- 16. 用户积分流水表
CREATE TABLE `user_points` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "流水ID，主键自增",
  `user_id` bigint NOT NULL COMMENT "用户ID",
  `change_type` tinyint NOT NULL COMMENT "变动类型: 1-增加, 2-减少",
  `source` varchar(50) NOT NULL COMMENT "积分来源/用途: order_pay, daily_checkin, invite, exchange",
  `points` int NOT NULL COMMENT "变动积分数额，正负代表增减",
  `current_balance` int NOT NULL COMMENT "变动后实时余额",
  `order_no` varchar(32) COMMENT "关联订单号",
  `activity_id` bigint COMMENT "关联活动ID",
  `expires_at` datetime COMMENT "积分过期时间",
  `status` tinyint DEFAULT 1 COMMENT "状态: 1-有效, 0-无效(如撤销)",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `remark` varchar(200) COMMENT "备注",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_source` (`source`) COMMENT "来源索引",
  INDEX `idx_created_at` (`created_at`) COMMENT "创建时间索引",
  INDEX `idx_expires_at` (`expires_at`) COMMENT "过期时间索引",
  INDEX `idx_order_no` (`order_no`) COMMENT "订单号索引",
  INDEX `idx_expires_status` (`expires_at`, `status`) COMMENT "过期状态联合索引",
  CONSTRAINT `fk_user_points_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "用户积分流水表，所有积分变动有迹可循。需定期清理过期积分记录（建议无效记录保留2年）。";
-- 17. 用户钱包表
CREATE TABLE `user_wallet` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "钱包ID，主键自增",
  `user_id` bigint UNIQUE NOT NULL COMMENT "用户ID，唯一",
  `balance` decimal(10, 2) DEFAULT 0 COMMENT "可用余额",
  `frozen_balance` decimal(10, 2) DEFAULT 0 COMMENT "冻结金额(如提现中、支付中)",
  `total_deposit` decimal(10, 2) DEFAULT 0 COMMENT "累计充值",
  `total_consumption` decimal(10, 2) DEFAULT 0 COMMENT "累计消费",
  `security_level` tinyint DEFAULT 1 COMMENT "安全等级",
  `pay_password_hash` varchar(128) COMMENT "支付密码哈希",
  `status` tinyint DEFAULT 1 COMMENT "状态: 1-正常, 0-冻结",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  `version` int DEFAULT 0 COMMENT "乐观锁版本号",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  CONSTRAINT `fk_user_wallet_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "用户钱包（余额）账户，独立于积分体系，用于储值消费。";
-- 17A. 钱包流水表
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
-- 18. 用户持有优惠券表
CREATE TABLE `user_coupons` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "记录ID，主键自增",
  `user_id` bigint NOT NULL COMMENT "用户ID",
  `coupon_id` bigint NOT NULL COMMENT "优惠券ID",
  `coupon_code` varchar(50) UNIQUE NOT NULL COMMENT "唯一券码",
  `status` tinyint DEFAULT 0 COMMENT "状态: 0-未使用, 1-已使用, 2-已过期",
  `received_at` datetime DEFAULT (now()) COMMENT "领取时间",
  `used_at` datetime COMMENT "使用时间",
  `order_no` varchar(32) COMMENT "使用的订单号",
  `expires_at` datetime NOT NULL COMMENT "该张券的实际过期时间",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_coupon_id` (`coupon_id`) COMMENT "优惠券ID索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  INDEX `idx_expires_at` (`expires_at`) COMMENT "过期时间索引",
  INDEX `idx_coupon_code` (`coupon_code`) COMMENT "券码索引",
  CONSTRAINT `fk_user_coupons_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_coupons_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "用户实际领取和持有的优惠券。由 coupons 模板生成。";
-- 19. 商户结算主表
CREATE TABLE `merchant_settlements` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "结算ID，主键自增",
  `settlement_no` varchar(32) UNIQUE NOT NULL COMMENT "结算单号，如 SETTLE202412001",
  `merchant_id` bigint NOT NULL COMMENT "商户ID",
  `period` varchar(20) NOT NULL COMMENT "结算周期，如 2024-12",
  `total_amount` decimal(12, 2) NOT NULL COMMENT "周期内交易总金额",
  `platform_fee` decimal(12, 2) NOT NULL COMMENT "平台服务费",
  `settlement_amount` decimal(12, 2) NOT NULL COMMENT "应付给商户的金额",
  `settlement_status` tinyint DEFAULT 0 COMMENT "结算状态: 0-待结算, 1-结算中, 2-结算成功, 3-结算失败",
  `payment_status` tinyint DEFAULT 0 COMMENT "付款状态: 0-待付款, 1-付款中, 2-付款成功, 3-付款失败",
  `payment_method` varchar(50) COMMENT "付款方式",
  `payment_time` datetime COMMENT "付款时间",
  `invoice_status` tinyint DEFAULT 0 COMMENT "发票状态: 0-未开, 1-已申请, 2-已开票",
  `note` text COMMENT "备注",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_merchant_id` (`merchant_id`) COMMENT "商户ID索引",
  INDEX `idx_settlement_no` (`settlement_no`) COMMENT "结算单号索引",
  INDEX `idx_period` (`period`) COMMENT "结算周期索引",
  INDEX `idx_settlement_status` (`settlement_status`) COMMENT "结算状态索引",
  INDEX `idx_created_at` (`created_at`) COMMENT "创建时间索引",
  CONSTRAINT `fk_merchant_settlements_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) COMMENT = "按周期给商户的结算单主表。明细见 settlement_details (下张表)。";
-- 20. 结算明细表
CREATE TABLE `settlement_details` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "明细ID，主键自增",
  `settlement_id` bigint NOT NULL COMMENT "结算单ID",
  `order_no` varchar(32) NOT NULL COMMENT "订单号",
  `order_type` tinyint NOT NULL COMMENT "订单类型",
  `order_amount` decimal(10, 2) NOT NULL COMMENT "订单金额",
  `platform_fee_rate` decimal(5, 4) NOT NULL COMMENT "平台费率",
  `platform_fee` decimal(10, 2) NOT NULL COMMENT "平台服务费",
  `merchant_share` decimal(10, 2) NOT NULL COMMENT "商户分账金额",
  `settled_at` datetime COMMENT "计入结算时间",
  `status` tinyint DEFAULT 1 COMMENT "状态: 1-正常, 0-异常(如退款需冲正)",
  INDEX `idx_settlement_id` (`settlement_id`) COMMENT "结算单ID索引",
  INDEX `idx_order_no` (`order_no`) COMMENT "订单号索引",
  CONSTRAINT `fk_settlement_details_settlement` FOREIGN KEY (`settlement_id`) REFERENCES `merchant_settlements` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "结算单明细，记录每一笔订单的分账构成。退款订单会产生冲正明细。";
-- 21. 营销活动主表
CREATE TABLE `activities` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "活动ID，主键自增",
  `merchant_id` bigint COMMENT "商户ID，为空则表示平台活动",
  `name` varchar(200) NOT NULL COMMENT "活动名称",
  `type` tinyint NOT NULL COMMENT "活动类型: 1-打卡任务, 2-积分奖励, 3-主题促销",
  `cover_image` varchar(500) COMMENT "封面图URL",
  `description` text COMMENT "活动描述",
  `starts_at` datetime NOT NULL COMMENT "开始时间",
  `ends_at` datetime NOT NULL COMMENT "结束时间",
  `location` varchar(500) COMMENT "活动地点",
  `participation_limit` int COMMENT "参与人数限制",
  `requirement_type` tinyint DEFAULT 0 COMMENT "参与条件: 0-无, 1-需购票, 2-需积分, 3-需报名",
  `requirement_value` varchar(200) COMMENT "条件值",
  `reward_config` json NOT NULL COMMENT "奖励配置JSON",
  `audit_status` tinyint DEFAULT 0 COMMENT "审核状态: 0-待审, 1-通过, 2-拒绝",
  `status` tinyint DEFAULT 0 COMMENT "活动状态: 0-未开始, 1-进行中, 2-已结束, 3-取消",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_merchant_id` (`merchant_id`) COMMENT "商户ID索引",
  INDEX `idx_type` (`type`) COMMENT "活动类型索引",
  INDEX `idx_time_range` (`starts_at`, `ends_at`) COMMENT "时间范围索引",
  INDEX `idx_status` (`status`) COMMENT "活动状态索引",
  INDEX `idx_audit_status` (`audit_status`) COMMENT "审核状态索引",
  CONSTRAINT `fk_activities_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`id`) ON DELETE
  SET NULL ON UPDATE CASCADE
) COMMENT = "营销活动主表，支持平台和商户活动。参与记录见 activity_participations (下批)。";
-- 22. 本地事务消息表
CREATE TABLE `transaction_messages` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "消息ID，主键自增",
  `business_id` varchar(64) NOT NULL COMMENT "业务ID，如订单号",
  `business_type` varchar(50) NOT NULL COMMENT "业务类型，如order_pay",
  `payload` json NOT NULL COMMENT "消息内容，JSON格式",
  `status` tinyint DEFAULT 0 COMMENT "状态: 0-待发送，1-已发送，2-已完成，3-失败",
  `retry_count` int DEFAULT 0 COMMENT "重试次数",
  `next_retry_time` datetime COMMENT "下次重试时间",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  INDEX `idx_status_retry` (`status`, `next_retry_time`) COMMENT "状态重试时间索引"
) COMMENT = "本地消息表，用于实现分布式事务最终一致性。需定期清理已完成/失败消息。";
-- 23. 订单退款表
CREATE TABLE `order_refunds` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "退款ID，主键自增",
  `refund_no` varchar(32) UNIQUE NOT NULL COMMENT "退款单号，如 REFUND202412110001",
  `order_no` varchar(32) NOT NULL COMMENT "订单号",
  `user_id` bigint NOT NULL COMMENT "用户ID",
  `refund_type` tinyint NOT NULL COMMENT "退款类型: 1-全额退款, 2-部分退款",
  `refund_amount` decimal(10, 2) NOT NULL COMMENT "退款金额",
  `refund_fee` decimal(10, 2) DEFAULT 0 COMMENT "退款手续费",
  `actual_refund` decimal(10, 2) COMMENT "实际到账金额",
  `refund_reason` varchar(200) NOT NULL COMMENT "退款原因",
  `refund_evidence` json COMMENT "退款凭证图片等，JSON格式",
  `status` tinyint DEFAULT 0 COMMENT "状态: 0-待审核, 1-审核通过, 2-审核拒绝, 3-退款中, 4-成功, 5-失败",
  `auditor_id` bigint COMMENT "审核人ID",
  `audited_at` datetime COMMENT "审核时间",
  `audit_note` varchar(500) COMMENT "审核备注",
  `payment_refund_no` varchar(64) COMMENT "支付渠道退款流水号",
  `payment_refund_at` datetime COMMENT "支付渠道退款时间",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_order_no` (`order_no`) COMMENT "订单号索引",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_refund_no` (`refund_no`) COMMENT "退款单号索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  INDEX `idx_created_at` (`created_at`) COMMENT "创建时间索引",
  CONSTRAINT `fk_order_refunds_order` FOREIGN KEY (`order_no`) REFERENCES `orders` (`order_no`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_order_refunds_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) COMMENT = "订单退款申请与处理表。与 order_changes 共同构成售后体系。建议终态记录保留3年。";
-- 24. 订单改签表
CREATE TABLE `order_changes` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "改签ID，主键自增",
  `change_no` varchar(32) UNIQUE NOT NULL COMMENT "改签单号",
  `order_no` varchar(32) NOT NULL COMMENT "订单号",
  `user_id` bigint NOT NULL COMMENT "用户ID",
  `change_type` tinyint NOT NULL COMMENT "类型: 1-改日期, 2-改时间, 3-改票种",
  `original_ticket_id` bigint COMMENT "原票种ID",
  `new_ticket_id` bigint COMMENT "新票种ID",
  `original_use_date` date COMMENT "原使用日期",
  `new_use_date` date COMMENT "新使用日期",
  `price_difference` decimal(10, 2) COMMENT "差价，正为需补，负为应退",
  `change_fee` decimal(10, 2) DEFAULT 0 COMMENT "改签手续费",
  `total_amount` decimal(10, 2) NOT NULL COMMENT "改签总金额",
  `change_reason` varchar(200) NOT NULL COMMENT "改签原因",
  `status` tinyint DEFAULT 0 COMMENT "状态: 0-待审核, 1-审核通过, 2-拒绝, 3-已完成",
  `auditor_id` bigint COMMENT "审核人ID",
  `audited_at` datetime COMMENT "审核时间",
  `audit_note` varchar(500) COMMENT "审核备注",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_order_no` (`order_no`) COMMENT "订单号索引",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_change_no` (`change_no`) COMMENT "改签单号索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  CONSTRAINT `fk_order_changes_order` FOREIGN KEY (`order_no`) REFERENCES `orders` (`order_no`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_order_changes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_order_changes_original_ticket` FOREIGN KEY (`original_ticket_id`) REFERENCES `tickets` (`id`) ON DELETE
  SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_order_changes_new_ticket` FOREIGN KEY (`new_ticket_id`) REFERENCES `tickets` (`id`) ON DELETE
  SET NULL ON UPDATE CASCADE
) COMMENT = "门票订单改签记录表。涉及库存释放和重新锁定。";
-- 25. 订单核销记录表
CREATE TABLE `order_verifications` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "核销ID，主键自增",
  `order_no` varchar(32) NOT NULL COMMENT "订单号",
  `merchant_id` bigint NOT NULL COMMENT "商户ID",
  `operator_id` bigint NOT NULL COMMENT "操作员ID",
  `verified_quantity` int DEFAULT 1 COMMENT "本次核销数量",
  `verified_at` datetime DEFAULT (now()) COMMENT "核销时间",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  INDEX `idx_order_no` (`order_no`) COMMENT "订单号索引",
  INDEX `idx_merchant_id` (`merchant_id`) COMMENT "商户ID索引",
  INDEX `idx_operator_id` (`operator_id`) COMMENT "操作员ID索引",
  INDEX `idx_verified_at` (`verified_at`) COMMENT "核销时间索引",
  CONSTRAINT `fk_verifications_order` FOREIGN KEY (`order_no`) REFERENCES `orders` (`order_no`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_verifications_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) COMMENT = "订单核销记录表。记录每次核销操作，支持部分核销和批量核销。";
-- 26. 用户反馈表
CREATE TABLE `user_feedback` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "反馈ID，主键自增",
  `user_id` bigint NOT NULL COMMENT "用户ID",
  `type` tinyint NOT NULL COMMENT "反馈类型: 1-投诉, 2-建议, 3-咨询",
  `content` text NOT NULL COMMENT "反馈内容",
  `images` json COMMENT "上传的凭证图片，JSON格式",
  `contact_phone` varchar(20) COMMENT "联系电话",
  `order_no` varchar(32) COMMENT "关联订单",
  `merchant_id` bigint COMMENT "关联商户",
  `status` tinyint DEFAULT 0 COMMENT "处理状态: 0-待处理, 1-处理中, 2-已处理",
  `handler_id` bigint COMMENT "处理人ID",
  `handled_at` datetime COMMENT "处理时间",
  `handle_result` text COMMENT "处理结果",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_type` (`type`) COMMENT "反馈类型索引",
  INDEX `idx_status` (`status`) COMMENT "处理状态索引",
  INDEX `idx_created_at` (`created_at`) COMMENT "创建时间索引",
  INDEX `idx_order_no` (`order_no`) COMMENT "订单号索引",
  CONSTRAINT `fk_user_feedback_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_user_feedback_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`id`) ON DELETE
  SET NULL ON UPDATE CASCADE
) COMMENT = "用户反馈与工单表。用于客服跟踪和处理。建议已处理记录保留1年。";
-- 26. 用户评价表
CREATE TABLE `user_comments` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "评价ID，主键自增",
  `user_id` bigint NOT NULL COMMENT "用户ID",
  `target_type` tinyint NOT NULL COMMENT "评价对象类型: 1-景区/商户, 2-商品, 3-活动",
  `target_id` bigint NOT NULL COMMENT "评价对象ID",
  `order_no` varchar(32) NOT NULL COMMENT "必须基于订单评价",
  `rating` tinyint NOT NULL COMMENT "评分: 1-5星",
  `content` text COMMENT "评价内容",
  `images` json COMMENT "评价图片，JSON格式",
  `is_anonymous` boolean DEFAULT false COMMENT "是否匿名评价",
  `like_count` int DEFAULT 0 COMMENT "点赞数",
  `reply_count` int DEFAULT 0 COMMENT "回复数",
  `status` tinyint DEFAULT 1 COMMENT "状态: 1-显示, 0-隐藏",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_target_type` (`target_type`) COMMENT "评价对象类型索引",
  INDEX `idx_target_id` (`target_id`) COMMENT "评价对象ID索引",
  INDEX `idx_order_no` (`order_no`) COMMENT "订单号索引",
  INDEX `idx_rating` (`rating`) COMMENT "评分索引",
  INDEX `idx_created_at` (`created_at`) COMMENT "创建时间索引",
  CONSTRAINT `fk_user_comments_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_user_comments_order` FOREIGN KEY (`order_no`) REFERENCES `orders` (`order_no`) ON DELETE RESTRICT ON UPDATE CASCADE
) COMMENT = "用户评价表。支持对商户、商品、活动的多维度评价，是信用体系基础。";
-- 27. 活动参与记录表
CREATE TABLE `activity_participations` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "参与记录ID，主键自增",
  `activity_id` bigint NOT NULL COMMENT "活动ID",
  `user_id` bigint NOT NULL COMMENT "用户ID",
  `participation_status` tinyint DEFAULT 1 COMMENT "参与状态: 1-进行中, 2-已完成, 3-已放弃",
  `reward_status` tinyint DEFAULT 0 COMMENT "奖励状态: 0-未发放, 1-已发放, 2-发放失败",
  `reward_detail` json COMMENT "实际获得的奖励详情，JSON格式",
  `checkpoints_progress` json COMMENT "打卡点完成进度，JSON格式",
  `joined_at` datetime DEFAULT (now()) COMMENT "参与时间",
  `completed_at` datetime COMMENT "完成时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_activity_user` (`activity_id`, `user_id`) COMMENT "活动用户联合索引",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_participation_status` (`participation_status`) COMMENT "参与状态索引",
  INDEX `idx_joined_at` (`joined_at`) COMMENT "参与时间索引",
  UNIQUE INDEX `idx_activity_user_unique` (`activity_id`, `user_id`) COMMENT "活动用户唯一索引",
  CONSTRAINT `fk_activity_participations_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_activity_participations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "用户参与营销活动的记录表。用于跟踪进度和发放奖励。";
-- 28. 秒杀活动表
CREATE TABLE `seckill_events` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "秒杀ID，主键自增",
  `product_id` bigint NOT NULL COMMENT "商品ID",
  `title` varchar(200) NOT NULL COMMENT "秒杀标题",
  `seckill_price` decimal(10, 2) NOT NULL COMMENT "秒杀价",
  `original_price` decimal(10, 2) NOT NULL COMMENT "原价",
  `total_inventory` int NOT NULL COMMENT "秒杀总库存",
  `available_inventory` int NOT NULL COMMENT "秒杀可用库存",
  `limit_per_user` int DEFAULT 1 COMMENT "每人限购数量",
  `starts_at` datetime NOT NULL COMMENT "开始时间",
  `ends_at` datetime NOT NULL COMMENT "结束时间",
  `status` tinyint DEFAULT 0 COMMENT "状态: 0-未开始, 1-进行中, 2-已结束, 3-手动关闭",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  INDEX `idx_product_id` (`product_id`) COMMENT "商品ID索引",
  INDEX `idx_time_range` (`starts_at`, `ends_at`) COMMENT "时间范围索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  CONSTRAINT `fk_seckill_events_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "秒杀/闪购活动专用表，独立库存以应对高并发。需配合缓存和队列。";
-- 29. 商户钱包表
CREATE TABLE `merchant_wallet` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "钱包ID，主键自增",
  `merchant_id` bigint UNIQUE NOT NULL COMMENT "商户ID，唯一",
  `balance` decimal(12, 2) DEFAULT 0 COMMENT "可提现余额",
  `frozen_balance` decimal(12, 2) DEFAULT 0 COMMENT "冻结余额(结算中/提现中)",
  `total_income` decimal(12, 2) DEFAULT 0 COMMENT "累计收入",
  `total_withdrawn` decimal(12, 2) DEFAULT 0 COMMENT "累计已提现",
  `security_level` tinyint DEFAULT 1 COMMENT "安全等级",
  `withdraw_password_hash` varchar(128) COMMENT "提现密码哈希",
  `status` tinyint DEFAULT 1 COMMENT "状态: 1-正常, 0-冻结",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_merchant_id` (`merchant_id`) COMMENT "商户ID索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  CONSTRAINT `fk_merchant_wallet_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "商户钱包，用于存放可提现的结算收入。";
-- 30. 发票表
CREATE TABLE `invoices` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "发票ID，主键自增",
  `invoice_no` varchar(32) UNIQUE NOT NULL COMMENT "发票号码",
  `user_id` bigint NOT NULL COMMENT "用户ID",
  `type` tinyint NOT NULL COMMENT "发票类型: 1-普通, 2-专用",
  `title` varchar(200) NOT NULL COMMENT "发票抬头",
  `tax_number` varchar(50) COMMENT "纳税人识别号",
  `amount` decimal(10, 2) NOT NULL COMMENT "开票金额",
  `status` tinyint DEFAULT 0 COMMENT "开票状态: 0-待开, 1-开票中, 2-成功, 3-失败",
  `file_url` varchar(500) COMMENT "发票文件URL",
  `order_no` varchar(32) COMMENT "关联订单(可为空，支持合并开票)",
  `applied_at` datetime DEFAULT (now()) COMMENT "申请时间",
  `issued_at` datetime COMMENT "开票时间",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_invoice_no` (`invoice_no`) COMMENT "发票号码索引",
  INDEX `idx_status` (`status`) COMMENT "开票状态索引",
  INDEX `idx_applied_at` (`applied_at`) COMMENT "申请时间索引",
  CONSTRAINT `fk_invoices_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) COMMENT = "用户发票申请与记录表。可与订单1对1或1对多。";
-- 31. 资讯文章表
CREATE TABLE `articles` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "文章ID，主键自增",
  `category` varchar(50) NOT NULL COMMENT "文章分类",
  `title` varchar(200) NOT NULL COMMENT "文章标题",
  `author` varchar(50) COMMENT "作者",
  `cover_image` varchar(500) COMMENT "封面图URL",
  `summary` varchar(500) COMMENT "摘要",
  `content` text NOT NULL COMMENT "文章内容",
  `view_count` int DEFAULT 0 COMMENT "浏览量",
  `like_count` int DEFAULT 0 COMMENT "点赞数",
  `is_pinned` boolean DEFAULT false COMMENT "是否置顶",
  `status` tinyint DEFAULT 0 COMMENT "状态: 0-草稿, 1-已发布, 2-隐藏",
  `published_at` datetime COMMENT "发布时间",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_category` (`category`) COMMENT "分类索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  INDEX `idx_published_at` (`published_at`) COMMENT "发布时间索引",
  INDEX `idx_is_pinned` (`is_pinned`) COMMENT "置顶索引"
) COMMENT = "平台资讯、攻略、公告等文章内容表。";
-- 32. 操作日志表
CREATE TABLE `operation_logs` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "日志ID，主键自增",
  `user_id` bigint COMMENT "操作人ID，可为空(系统操作)",
  `user_type` tinyint COMMENT "用户类型: 1-平台用户, 2-商户, 3-管理员",
  `module` varchar(50) NOT NULL COMMENT "操作模块",
  `action` varchar(200) NOT NULL COMMENT "操作内容",
  `request_method` varchar(10) COMMENT "请求方法",
  `request_url` varchar(500) COMMENT "请求URL",
  `request_params` text COMMENT "请求参数",
  `ip_address` varchar(50) COMMENT "IP地址",
  `user_agent` text COMMENT "用户代理",
  `is_success` boolean DEFAULT true COMMENT "是否成功",
  `error_message` text COMMENT "错误信息",
  `duration_ms` int COMMENT "操作耗时(毫秒)",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  INDEX `idx_user_id` (`user_id`) COMMENT "用户ID索引",
  INDEX `idx_module` (`module`) COMMENT "操作模块索引",
  INDEX `idx_created_at` (`created_at`) COMMENT "创建时间索引",
  INDEX `idx_is_success` (`is_success`) COMMENT "成功状态索引"
) COMMENT = "系统关键操作审计日志表，用于安全追踪和行为分析。建议详细日志保留6个月。";
-- 33. 数据字典表
CREATE TABLE `data_dictionaries` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT "字典ID，主键自增",
  `dict_type` varchar(50) NOT NULL COMMENT "字典类型，如: user_status",
  `dict_code` varchar(50) NOT NULL COMMENT "字典编码，如: 0",
  `dict_value` varchar(200) NOT NULL COMMENT "字典值，如: 正常",
  `sort_order` int DEFAULT 0 COMMENT "排序",
  `is_default` boolean DEFAULT false COMMENT "是否默认值",
  `status` tinyint DEFAULT 1 COMMENT "状态: 1-启用, 0-停用",
  `remark` varchar(200) COMMENT "备注",
  `created_at` datetime DEFAULT (now()) COMMENT "创建时间",
  `updated_at` datetime DEFAULT (now()) COMMENT "更新时间",
  INDEX `idx_dict_type` (`dict_type`) COMMENT "字典类型索引",
  INDEX `idx_sort_order` (`sort_order`) COMMENT "排序索引",
  INDEX `idx_status` (`status`) COMMENT "状态索引",
  UNIQUE INDEX `idx_type_code` (`dict_type`, `dict_code`) COMMENT "类型编码唯一索引"
) COMMENT = "系统数据字典表，统一管理所有状态、类型的枚举值，便于维护和多语言扩展。";
-- 34. 景区日度统计表（新增汇总表）
CREATE TABLE `scenic_daily_stats` (
  `stat_date` DATE NOT NULL COMMENT "统计日期",
  `scenic_spot_id` BIGINT NOT NULL COMMENT "景区ID",
  `order_count` INT DEFAULT 0 COMMENT "订单数",
  `total_sales_amount` DECIMAL(12, 2) DEFAULT 0 COMMENT "总销售额",
  `unique_user_count` INT DEFAULT 0 COMMENT "下单用户数",
  `new_user_count` INT DEFAULT 0 COMMENT "新用户数",
  `created_at` DATETIME DEFAULT (now()) COMMENT "创建时间",
  `updated_at` DATETIME DEFAULT (now()) COMMENT "更新时间",
  PRIMARY KEY (`stat_date`, `scenic_spot_id`) COMMENT "联合主键",
  INDEX `idx_scenic_spot_id` (`scenic_spot_id`) COMMENT "景区ID索引",
  CONSTRAINT `fk_scenic_daily_stats_scenic_spot` FOREIGN KEY (`scenic_spot_id`) REFERENCES `scenic_spots` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT = "景区维度日度统计汇总表，用于加速报表查询。";
-----------------------------------------------------
-- 建议在创建表后，初始化数据字典
INSERT INTO `data_dictionaries` (
    `dict_type`,
    `dict_code`,
    `dict_value`,
    `sort_order`,
    `is_default`,
    `remark`
  )
VALUES -- 用户状态
  ('user_status', '1', '正常', 1, true, '用户正常状态'),
  ('user_status', '0', '冻结', 2, false, '用户被冻结'),
  ('user_status', '2', '注销', 3, false, '用户已注销'),
  -- 订单状态
  ('order_status', '0', '待付款', 1, true, '等待用户支付'),
  ('order_status', '1', '待使用', 2, false, '已支付，等待使用'),
  ('order_status', '2', '已完成', 3, false, '订单已完成'),
  ('order_status', '3', '已取消', 4, false, '订单已取消'),
  ('order_status', '4', '退款中', 5, false, '正在退款处理中'),
  -- 支付状态
  ('payment_status', '0', '待支付', 1, true, '等待支付'),
  ('payment_status', '1', '成功', 2, false, '支付成功'),
  ('payment_status', '2', '失败', 3, false, '支付失败'),
  ('payment_status', '3', '已退款', 4, false, '已退款'),
  ('payment_status', '4', '处理中', 5, false, '支付处理中'),
  -- 商品类型
  ('product_type', '1', '门票', 1, true, '景区门票'),
  ('product_type', '2', '餐饮', 2, false, '餐饮商品'),
  ('product_type', '3', '文创', 3, false, '文创商品'),
  ('product_type', '4', '生鲜', 4, false, '生鲜商品'),
  -- 商户类型
  ('merchant_type', '1', '景区', 1, true, '景区商户'),
  ('merchant_type', '2', '餐饮', 2, false, '餐饮商户'),
  ('merchant_type', '3', '文创', 3, false, '文创商户'),
  ('merchant_type', '4', '生鲜便利', 4, false, '生鲜便利商户'),
  -- 审核状态
  ('audit_status', '0', '待审核', 1, true, '等待审核'),
  ('audit_status', '1', '通过', 2, false, '审核通过'),
  ('audit_status', '2', '拒绝', 3, false, '审核拒绝');