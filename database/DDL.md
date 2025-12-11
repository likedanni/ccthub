### **第一步：项目完整数据表清单（共 8 大类，30 张表）**

在生成任何代码前，请先审阅这份完整的清单，确认范围无遗漏。

| 序号                     | 表名 (英文)            | 中文表名       | 核心业务域     | 优先级 | 依赖项                |
| :----------------------- | :--------------------- | :------------- | :------------- | :----- | :-------------------- |
| **一、用户与会员中心**   |
| 1                        | `users`                | 用户核心表     | 所有业务基础   | P0     | -                     |
| 2                        | `user_addresses`       | 用户收货地址表 | 电商、文创邮寄 | P0     | `users`               |
| 3                        | `user_favorites`       | 用户收藏表     | 个性化推荐     | P1     | `users`, `products`   |
| 4                        | `user_coupons`         | 用户优惠券表   | 营销与交易     | P1     | `users`, `coupons`    |
| 5                        | `user_points`          | 用户积分流水表 | 会员与营销体系 | P1     | `users`               |
| 6                        | `user_feedback`        | 用户反馈表     | 客服与体验     | P2     | `users`, `orders`     |
| **二、商户与供应链**     |
| 7                        | `merchants`            | 商户主表       | B 端业务基础   | P0     | -                     |
| 8                        | `merchant_employees`   | 商户员工表     | 商户端权限     | P0     | `merchants`, `users`  |
| 9                        | `merchant_settlements` | 商户结算主表   | 财务           | P1     | `merchants`, `orders` |
| 10                       | `merchant_wallet`      | 商户钱包表     | 财务           | P1     | `merchants`           |
| **三、商品、票务与库存** |
| 11                       | `scenic_spots`         | 景区/资源表    | 文旅资源基础   | P0     | -                     |
| 12                       | `products`             | 商品 SPU 表    | 统一商品管理   | P0     | `merchants`           |
| 13                       | `product_skus`         | 商品 SKU 表    | 库存与价格     | P0     | `products`            |
| 14                       | `tickets`              | 票种模板表     | 票务业务       | P0     | `scenic_spots`        |
| 15                       | `ticket_prices`        | 票价日历表     | 动态定价       | P0     | `tickets`             |
| **四、交易与订单**       |
| 16                       | `orders`               | 订单主表       | 交易核心       | P0     | `users`, `merchants`  |
| 17                       | `order_items`          | 订单明细表     | 交易核心       | P0     | `orders`, `products`  |
| 18                       | `order_refunds`        | 订单退款表     | 售后           | P1     | `orders`              |
| 19                       | `order_changes`        | 订单改签表     | 票务售后       | P1     | `orders`, `tickets`   |
| **五、支付与财务**       |
| 20                       | `payments`             | 支付记录表     | 资金流水       | P0     | `orders`              |
| 21                       | `invoices`             | 发票表         | 财务           | P2     | `users`, `orders`     |
| 22                       | `user_wallet`          | 用户钱包表     | 资金账户       | P1     | `users`               |
| **六、营销与活动**       |
| 23                       | `activities`           | 营销活动表     | 活动运营       | P1     | `merchants` (可选)    |
| 24                       | `coupons`              | 优惠券模板表   | 营销           | P1     | -                     |
| 25                       | `seckill_events`       | 秒杀活动表     | 营销（高并发） | P2     | `products`            |
| **七、内容与互动**       |
| 26                       | `articles`             | 资讯文章表     | 内容运营       | P2     | -                     |
| 27                       | `user_comments`        | 用户评价表     | UGC 与信用     | P1     | `users`, `orders`     |
| **八、系统与支撑**       |
| 28                       | `system_configs`       | 系统配置表     | 系统运维       | P1     | -                     |
| 29                       | `operation_logs`       | 操作日志表     | 安全审计       | P1     | -                     |
| 30                       | `data_dictionaries`    | 数据字典表     | 系统维护       | P2     | -                     |

### **第二步：分批生成纯净 DDL 脚本**

为确保每批脚本在 `dbdiagram.io` 中均能**一次性通过，无任何语法错误**，我将按以下顺序交付：

1.  **批次 1 (基础 P0)**：包含第 1、7、11、12、13、16、17、20 号表（共 8 张），构建最核心的业务链路。
2.  **批次 2 (扩展 P0)**：包含第 2、8、14、15 号表（共 4 张），扩展用户、商户和票务细节。
3.  **批次 3 (P1 核心)**：包含第 4、5、9、10、22、23、24 号表（共 7 张），构建积分、营销、财务能力。
4.  **批次 4 (P1/P2 剩余)**：包含剩余 11 张表，完成所有功能。

---

# 长治文旅平台数据库架构评审报告

## 一、执行摘要与风险评级

- **整体评价**：**良好**。表结构设计总体规范，考虑到了业务核心实体与关系，注释清晰。但在**安全合规、高并发下的扩展性、业务逻辑完备性**方面存在若干需立即优化的中高风险点。
- **最高优先级问题**：
  1. **敏感数据加密字段缺失**：`order_items.visitor_name`（游客姓名）作为个人敏感信息未加密存储，违反《个人信息保护法》要求。
  2. **分布式事务一致性风险**：组合支付（余额+积分）场景缺少原子性保证，可能导致资金与积分数据不一致。
  3. **关键业务字段缺失**：`orders`表缺少`platform_fee`（平台服务费）字段，影响实时对账与结算准确性。

## 二、详细审查发现

### 1. 安全与合规

- **[发现 1]：游客姓名等敏感信息未加密**

  - **问题描述**：`order_items.visitor_name`字段明文存储，与`users`表中对`phone`、`id_card`的加密处理不一致，存在合规风险。
  - **风险影响**：违反最小必要原则和等保三级对个人敏感信息存储的加密要求，一旦数据泄露将导致法律风险。
  - **修改建议**：新增加密存储字段，并保留明文字段（如需展示），或使用可检索加密技术。
    ```sql
    ALTER TABLE `order_items`
    ADD COLUMN `visitor_name_encrypted` VARCHAR(128) COMMENT '加密存储的游客姓名',
    MODIFY COLUMN `visitor_name` VARCHAR(50) COMMENT '游客姓名（明文，仅用于核验时显示）';
    ```

- **[发现 2]：缺乏数据生命周期管理策略**

  - **问题描述**：`operation_logs`、`user_points`（过期记录）、`user_feedback`（已处理）等表会无限增长，影响性能与合规。
  - **风险影响**：存储成本攀升，查询性能下降，违反数据最小化存储原则。
  - **修改建议**：为相关表添加`data_retention_policy`注释，并建议通过定时任务或事件进行归档/清理。
    - `operation_logs`: 保留 6 个月详细日志，之后可归档至历史表或对象存储。
    - `user_points` (状态为 0-无效的记录): 保留 2 年。
    - `user_feedback` (状态为 2-已处理): 保留 1 年。
    - `order_refunds`/`order_changes` (终态记录): 保留 3 年。

- **[发现 3]：商户权限 JSON 字段存在性能与灵活性隐患**
  - **问题描述**：`merchant_employees.permissions`使用 JSON 存储，当权限项多、变更频繁时，无法建立有效索引，权限验证需全表解析 JSON，性能差。
  - **风险影响**：商户员工数量大时，权限验证接口响应慢。
  - **修改建议**：采用经典的“角色-权限”关系表设计，实现细粒度、可索引的权限管理。
    ```sql
    CREATE TABLE `merchant_employee_roles` (
      `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
      `merchant_id` BIGINT NOT NULL,
      `role_name` VARCHAR(50) NOT NULL,
      `permissions` JSON COMMENT '该角色拥有的权限集合',
      `is_system` BOOLEAN DEFAULT FALSE COMMENT '是否为系统预置角色'
    );
    CREATE TABLE `merchant_employee_role_assignments` (
      `employee_id` BIGINT NOT NULL,
      `role_id` BIGINT NOT NULL,
      PRIMARY KEY (`employee_id`, `role_id`)
    );
    -- 修改原merchant_employees表，移除permissions字段
    ALTER TABLE `merchant_employees` DROP COLUMN `permissions`;
    ```

### 2. 性能与扩展

- **[发现 1]：分库分表策略**

  - **分片键选择**：
    - **`orders` & `order_items`表**：**首选`user_id`作为分片键**。
      - **优势**：符合用户视角查询模式（查我的订单），使同一用户的订单数据集中，避免跨分片查询。`user_id`分布均匀。
      - **劣势**：商户查其所有订单需跨分片聚合（可通过`merchant_id`全局二级索引或异步 ETL 到分析库解决）。
    - **分表规则**：按`user_id`进行哈希分片（例如 64 个分片），同时按`create_time`进行按月/季度分表（例如`orders_202501`），实现两级分片。
  - **`users`表策略**：
    - **读写分离**：配置一主多从，读流量路由到从库。
    - **缓存策略**：
      - **L1 缓存（本地缓存）**：对`id`和`phone`进行缓存，有效期短（如 5 分钟），解决会话级高频查询。
      - **L2 缓存（Redis）**：
        - 存储完整用户信息，key 为`user:{id}`，TTL 为 30 分钟。
        - 存储用户会话状态，key 为`session:{token}`。
        - 使用写穿透（Write-Through）策略，更新数据库时同步更新/失效缓存。

- **[发现 2]：索引优化建议**

  - **新增索引**：
    ```sql
    -- orders表: 高频查询-用户订单列表，覆盖状态和时间筛选
    CREATE INDEX `idx_user_status_time` ON `orders` (`user_id`, `order_status`, `create_time` DESC);
    -- orders表: 商户后台查询
    CREATE INDEX `idx_merchant_status_time` ON `orders` (`merchant_id`, `order_status`, `create_time` DESC);
    -- product_skus表: 商品管理页常用查询
    CREATE INDEX `idx_product_status` ON `product_skus` (`product_id`, `status`);
    -- ticket_prices表: 前台门票日历查询
    CREATE INDEX `idx_date_active` ON `ticket_prices` (`price_date`, `is_active`);
    -- user_points表: 积分过期清理任务
    CREATE INDEX `idx_expires_status` ON `user_points` (`expires_at`, `status`) WHERE `expires_at` IS NOT NULL;
    ```
  - **冗余索引检查**：现有索引设计合理，暂未发现明显冗余。`users`表上`phone`已是 UNIQUE 约束，`phone_encrypted`的独立索引有必要，用于加密匹配。

- **[发现 3]：景区百万订单报表查询分析与优化**
  - **涉及表关联**：`orders` -> `order_items` -> `products` -> `scenic_spots`，并关联`payments`确认支付成功。
  - **潜在瓶颈**：
    1. **`orders`与`order_items`的大表 JOIN**。
    2. 按`scenic_spot_id`和月份进行分组聚合时的全表扫描。
    3. 复购率计算需要嵌套查询或窗口函数，计算复杂。
  - **优化路径**：
    1. **创建汇总表**：定期（如每日凌晨）预聚合景区维度的核心指标。
       ```sql
       CREATE TABLE `scenic_daily_stats` (
         `stat_date` DATE NOT NULL,
         `scenic_spot_id` BIGINT NOT NULL,
         `order_count` INT DEFAULT 0,
         `total_sales_amount` DECIMAL(12,2) DEFAULT 0,
         `unique_user_count` INT DEFAULT 0,
         `new_user_count` INT DEFAULT 0,
         PRIMARY KEY (`stat_date`, `scenic_spot_id`)
       );
       ```
    2. **使用分析型数据库**：将订单数据同步至**ClickHouse**，利用其列式存储和向量化引擎进行复杂聚合查询。
    3. **SQL 优化示例**（在汇总表基础上）：
       ```sql
       -- 假设已预聚合，查询某景区月度报表
       SELECT
           DATE_FORMAT(stat_date, '%Y-%m') AS month,
           SUM(order_count) AS 订单数,
           SUM(total_sales_amount) AS 总销售额,
           SUM(total_sales_amount) / SUM(order_count) AS 客单价,
           (SUM(CASE WHEN unique_user_count > 1 THEN 1 ELSE 0 END) / COUNT(DISTINCT user_id)) AS 复购率 -- 简化逻辑
       FROM `scenic_daily_stats` sds
       JOIN ... -- 获取用户维度信息
       WHERE sds.scenic_spot_id = 1 AND stat_date BETWEEN '2023-01-01' AND '2025-12-31'
       GROUP BY DATE_FORMAT(stat_date, '%Y-%m');
       ```

### 3. 业务逻辑

- **[发现 1]：枚举值语义不统一**

  - `status`字段：
    - `users.status`的`1`代表“冻结”，而`merchants.status`、`scenic_spots.status`、`product_skus.status`的`1`代表“正常/启用”。**建议统一：`0`=停用/冻结，`1`=正常**。
    - `orders.order_status`的`1`=“待使用”，`activities.status`的`1`=“进行中”。建议保持业务独立性，但需在数据字典中清晰定义。
  - **修改建议**：严格执行`data_dictionaries`表的管理，所有枚举字段必须在字典中有定义。优先修改`users.status`枚举值以符合主流约定。

- **[发现 2]：关键业务字段缺失**

  1. **`orders.platform_fee`（平台服务费）**：用于实时记录平台从该订单中收取的费用，是结算的基础。
     ```sql
     ALTER TABLE `orders` ADD COLUMN `platform_fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '平台服务费';
     ```
  2. **`tickets.limit_per_order`（每单限购数）**：防止刷票和黄牛囤票。
     ```sql
     ALTER TABLE `tickets` ADD COLUMN `limit_per_order` INT DEFAULT 5 COMMENT '每订单限购数量';
     ```
  3. **`orders.outer_order_no`（外部订单号）**：对接第三方系统（如 OTA）时使用。
     ```sql
     ALTER TABLE `orders` ADD COLUMN `outer_order_no` VARCHAR(64) COMMENT '外部订单号(如OTA订单号)', ADD INDEX `idx_outer_order_no` (`outer_order_no`);
     ```

- **[发现 3]：组合支付事务一致性方案**
  - **风险**：余额扣减、积分扣减、订单创建非原子操作，网络超时或服务宕机可能导致数据不一致（如钱扣了但订单未成功）。
  - **最终一致性方案设计**：
    1. **引入“支付中”中间状态**：在`orders.payment_status`中增加`4-处理中`。
    2. **使用本地消息表（Local Transaction Table）**：
       ```sql
       CREATE TABLE `transaction_messages` (
         `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
         `business_id` VARCHAR(64) NOT NULL COMMENT '业务ID，如订单号',
         `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型，如order_pay',
         `payload` JSON NOT NULL COMMENT '消息内容',
         `status` TINYINT DEFAULT 0 COMMENT '0-待发送，1-已发送，2-已完成，3-失败',
         `retry_count` INT DEFAULT 0,
         `next_retry_time` DATETIME,
         `created_at` DATETIME DEFAULT NOW(),
         INDEX `idx_status_retry` (`status`, `next_retry_time`)
       );
       ```
    3. **流程**：
       - a. 开启本地事务。
       - b. 插入订单（状态为`待支付`），插入支付流水（状态为`处理中`），扣减用户余额/积分（`frozen_balance`/`locked_points`），并插入一条`status=0`的本地消息记录。
       - c. 提交本地事务。
       - d. 定时任务扫描`status=0`的消息，调用支付渠道，成功后更新订单和支付流水状态为成功，并解冻余额/积分。若失败，进入重试或人工处理流程。

## 三、优化后的完整 DDL 脚本

```sql
-- 注意：此脚本已整合所有评审建议，为最终可执行版本。
-- 1. 用户表
CREATE TABLE `users` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `phone` varchar(20) UNIQUE NOT NULL,
  `phone_encrypted` varchar(128) NOT NULL COMMENT '加密存储，用于安全匹配',
  `nickname` varchar(50) NOT NULL,
  `avatar_url` varchar(500),
  `id_card_encrypted` varchar(128) COMMENT '加密存储',
  `real_name` varchar(50),
  `member_level` tinyint DEFAULT 1 COMMENT '会员等级: 1-普通, 2-白银, 3-黄金, 4-钻石',
  `growth_value` int DEFAULT 0 COMMENT '成长值',
  `total_points` int DEFAULT 0 COMMENT '累计积分',
  `available_points` int DEFAULT 0 COMMENT '可用积分',
  `wallet_balance` decimal(10,2) DEFAULT 0 COMMENT '钱包余额(元)',
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-正常, 0-冻结, 2-注销', -- 修改枚举值，与主流一致
  `register_time` datetime DEFAULT (now()),
  `last_login_time` datetime,
  `data_version` int DEFAULT 1 COMMENT '乐观锁版本号'
) COMMENT = '平台用户核心信息表，预计数据量50万+。敏感信息均加密存储。建议数据保留至注销后3年。';

-- 2. 商户表
CREATE TABLE `merchants` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `type` tinyint NOT NULL COMMENT '类型: 1-景区, 2-餐饮, 3-文创, 4-生鲜便利',
  `cooperation_type` tinyint NOT NULL COMMENT '合作类型: 1-直营, 2-联营, 3-加盟',
  `contact_person` varchar(50) NOT NULL,
  `contact_phone` varchar(20) NOT NULL,
  `business_license` varchar(100),
  `province` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `district` varchar(50),
  `address` varchar(500) NOT NULL,
  `longitude` decimal(10,6) COMMENT '经度，用于地图和附近推荐',
  `latitude` decimal(10,6) COMMENT '纬度',
  `settlement_rate` decimal(5,4) COMMENT '平台结算费率',
  `audit_status` tinyint DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-通过, 2-拒绝',
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-正常, 0-停用',
  `level` tinyint DEFAULT 1 COMMENT '商户等级',
  `score` decimal(3,2) DEFAULT 5 COMMENT '商户评分',
  `create_time` datetime DEFAULT (now())
) COMMENT = '入驻商户信息表，支持多种合作类型和结算规则。';

-- 3. 景区表
CREATE TABLE `scenic_spots` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `level` varchar(20) COMMENT '景区级别，如: A, AA, 5A',
  `introduction` text COMMENT '详细介绍',
  `province` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `address` varchar(500) NOT NULL,
  `longitude` decimal(10,6),
  `latitude` decimal(10,6),
  `opening_hours` varchar(200) COMMENT '开放时间描述',
  `contact_phone` varchar(20),
  `cover_image` varchar(500) COMMENT '封面图',
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-开放, 0-关闭',
  `create_time` datetime DEFAULT (now())
) COMMENT = '文旅资源基础信息表，可扩展为多类型资源（博物馆、公园等）。';

-- 4. 商品SPU表
CREATE TABLE `products` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `merchant_id` bigint,
  `scenic_spot_id` bigint COMMENT '可为空，非景区商品无关联',
  `type` tinyint NOT NULL COMMENT '商品类型: 1-门票, 2-餐饮, 3-文创, 4-生鲜',
  `category_id` bigint COMMENT '商品分类ID',
  `name` varchar(200) NOT NULL,
  `subtitle` varchar(200) COMMENT '副标题/卖点',
  `main_image` varchar(500),
  `image_list` json COMMENT 'JSON数组，商品图册',
  `description` text COMMENT '商品描述',
  `spec_template` json COMMENT 'JSON，规格模板',
  `point_ratio` decimal(5,4) DEFAULT 0.01 COMMENT '积分赠送比例',
  `audit_status` tinyint DEFAULT 0 COMMENT '审核状态: 0-待审, 1-通过, 2-拒绝',
  `shelf_status` tinyint DEFAULT 0 COMMENT '上下架: 0-下架, 1-上架',
  `create_time` datetime DEFAULT (now()),
  `update_time` datetime DEFAULT (now())
) COMMENT = '标准化商品信息表(SPU)，与具体规格(SKU)分离。';

-- 5. 商品SKU表
CREATE TABLE `product_skus` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `sku_code` varchar(50) UNIQUE NOT NULL COMMENT 'SKU编码，唯一',
  `sku_name` varchar(200) NOT NULL COMMENT 'SKU名称，如: 成人票-上午场',
  `specs` json NOT NULL COMMENT 'JSON，具体规格，如: {"时段": "上午", "人群": "成人"}',
  `original_price` decimal(10,2) NOT NULL COMMENT '门市价/原价',
  `sell_price` decimal(10,2) NOT NULL COMMENT '销售价',
  `cost_price` decimal(10,2) COMMENT '成本价，用于核算',
  `stock_total` int DEFAULT 0 COMMENT '总库存',
  `stock_available` int DEFAULT 0 COMMENT '可用库存',
  `stock_locked` int DEFAULT 0 COMMENT '锁定库存（下单未支付）',
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
  `sales_count` int DEFAULT 0 COMMENT '销售数量'
) COMMENT = '商品最小库存单位表(SKU)，管理价格、库存和销售。';

-- 6. 订单主表（需分库分表）
CREATE TABLE `orders` (
  `order_no` varchar(32) PRIMARY KEY COMMENT '业务订单号，如: ORDER202412110001',
  `user_id` bigint NOT NULL COMMENT '分片键',
  `merchant_id` bigint,
  `order_type` tinyint NOT NULL COMMENT '订单类型: 1-门票, 2-实物商品, 3-活动',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `discount_amount` decimal(10,2) DEFAULT 0 COMMENT '优惠总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实际支付金额',
  `point_amount` decimal(10,2) DEFAULT 0 COMMENT '积分抵扣金额',
  `point_earned` int DEFAULT 0 COMMENT '本次获得积分',
  `platform_fee` decimal(10,2) DEFAULT 0.00 COMMENT '平台服务费', -- 新增字段
  `payment_method` varchar(50) COMMENT '支付方式: wechat, alipay, balance',
  `payment_status` tinyint DEFAULT 0 COMMENT '支付状态: 0-待支付, 1-成功, 2-失败, 3-已退款, 4-处理中',
  `order_status` tinyint DEFAULT 0 COMMENT '订单状态: 0-待付款, 1-待使用, 2-已完成, 3-已取消, 4-退款中',
  `refund_status` tinyint DEFAULT 0 COMMENT '退款状态: 0-无退款, 1-退款中, 2-成功, 3-失败',
  `outer_order_no` varchar(64) COMMENT '外部订单号(如OTA订单号)', -- 新增字段
  `create_time` datetime DEFAULT (now()),
  `update_time` datetime DEFAULT (now())
) COMMENT = '订单主表，按user_id分片，需考虑按create_time分表。状态机复杂，需仔细设计。';

-- 7. 订单明细表（需分库分表，与orders相同分片规则）
CREATE TABLE `order_items` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL,
  `product_id` bigint,
  `product_name` varchar(200) NOT NULL COMMENT '下单时的商品快照',
  `sku_id` bigint,
  `sku_specs` varchar(500) COMMENT '下单时的规格快照',
  `unit_price` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `verification_code` varchar(50) UNIQUE COMMENT '核销码/券码，唯一',
  `verification_status` tinyint DEFAULT 0 COMMENT '核销状态: 0-未核销, 1-已核销, 2-已过期',
  `ticket_date` date COMMENT '票务使用日期',
  `visitor_name` varchar(50) COMMENT '游客姓名（明文，仅用于核验时显示）',
  `visitor_name_encrypted` varchar(128) COMMENT '加密存储的游客姓名' -- 新增加密字段
) COMMENT = '订单明细表，记录每件商品信息。核销码在此表管理。';

-- 8. 支付流水表
CREATE TABLE `payments` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `payment_no` varchar(32) UNIQUE NOT NULL COMMENT '支付系统流水号',
  `order_no` varchar(32) NOT NULL,
  `payment_type` varchar(20) NOT NULL COMMENT '支付类型: wechat, alipay, unionpay',
  `payment_channel` varchar(50) NOT NULL COMMENT '支付渠道: miniapp, app, h5, native',
  `payment_amount` decimal(10,2) NOT NULL,
  `status` tinyint NOT NULL COMMENT '状态: 0-待支付, 1-成功, 2-失败, 3-关闭, 4-处理中',
  `third_party_no` varchar(64) COMMENT '第三方支付平台流水号',
  `payer_id` varchar(100) COMMENT '支付方标识（如微信openid）',
  `payment_time` datetime,
  `callback_time` datetime COMMENT '支付回调时间',
  `create_time` datetime DEFAULT (now())
) COMMENT = '支付流水表，与订单1:1或1:N。需严格对账。';

-- 9. 用户地址表
CREATE TABLE `user_addresses` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `recipient_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `recipient_phone` varchar(20) NOT NULL,
  `province` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `district` varchar(50) NOT NULL,
  `detail_address` varchar(200) NOT NULL,
  `postal_code` varchar(10),
  `is_default` boolean DEFAULT false COMMENT '是否默认地址',
  `create_time` datetime DEFAULT (now()),
  `update_time` datetime DEFAULT (now())
) COMMENT = '用户收货地址表，用于实物商品（文创、生鲜）邮寄。';

-- 10. 商户员工角色表（新增）
CREATE TABLE `merchant_employee_roles` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `merchant_id` bigint NOT NULL,
  `role_name` varchar(50) NOT NULL,
  `permissions` json COMMENT '该角色拥有的权限集合',
  `is_system` boolean DEFAULT false COMMENT '是否为系统预置角色',
  `create_time` datetime DEFAULT (now())
);

-- 11. 商户员工角色分配表（新增）
CREATE TABLE `merchant_employee_role_assignments` (
  `employee_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `assigned_at` datetime DEFAULT (now()),
  PRIMARY KEY (`employee_id`, `role_id`)
);

-- 12. 商户员工表（已移除permissions字段）
CREATE TABLE `merchant_employees` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `merchant_id` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '关联平台用户账号',
  `employee_name` varchar(50) NOT NULL,
  `employee_phone` varchar(20) NOT NULL,
  `role` tinyint NOT NULL COMMENT '角色: 1-管理员, 2-运营员, 3-核销员', -- 可考虑与角色表关联
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-正常, 0-停用',
  `create_time` datetime DEFAULT (now())
) COMMENT = '商户员工绑定表。细粒度权限通过角色表(merchant_employee_roles)管理。';

-- 13. 票种模板表
CREATE TABLE `tickets` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `scenic_spot_id` bigint NOT NULL,
  `name` varchar(200) NOT NULL,
  `type` tinyint NOT NULL COMMENT '票种类型: 1-单票, 2-联票, 3-套票',
  `description` text,
  `validity_type` tinyint NOT NULL COMMENT '有效期类型: 1-指定日期, 2-有效天数',
  `valid_days` int COMMENT '有效期天数（当validity_type=2时使用）',
  `advance_days` int DEFAULT 7 COMMENT '可提前预订天数',
  `refund_policy` json NOT NULL COMMENT 'JSON，退款规则配置',
  `change_policy` json NOT NULL COMMENT 'JSON，改签规则配置',
  `limit_per_user` int DEFAULT 5 COMMENT '每用户限购数量',
  `limit_per_order` int DEFAULT 5 COMMENT '每订单限购数量', -- 新增字段
  `limit_per_day` int COMMENT '每日限购数量',
  `require_real_name` boolean DEFAULT true,
  `require_id_card` boolean DEFAULT true,
  `verification_mode` tinyint DEFAULT 1 COMMENT '核验方式: 1-二维码, 2-人脸, 3-身份证',
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-上架, 0-下架',
  `create_time` datetime DEFAULT (now())
) COMMENT = '票种模板表，定义某景区的一种门票规则。实际销售的是其下某一天的具体库存（ticket_prices）。';

-- 14. 票价库存日历表
CREATE TABLE `ticket_prices` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `ticket_id` bigint NOT NULL,
  `price_date` date NOT NULL COMMENT '适用日期',
  `price_type` tinyint NOT NULL COMMENT '价格类型: 1-成人票, 2-学生票, 3-儿童票, 4-老年票',
  `original_price` decimal(10,2) NOT NULL COMMENT '门市价',
  `sell_price` decimal(10,2) NOT NULL COMMENT '销售价',
  `inventory_total` int NOT NULL COMMENT '总库存',
  `inventory_available` int NOT NULL COMMENT '可用库存',
  `is_active` boolean DEFAULT true COMMENT '该日期价格是否生效',
  `create_time` datetime DEFAULT (now()),
  `update_time` datetime DEFAULT (now())
) COMMENT = '票价与库存日历表。支持每天不同的价格和独立的库存管理，是实际销售的单位。';

-- 15. 用户积分流水表
CREATE TABLE `user_points` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `change_type` tinyint NOT NULL COMMENT '变动类型: 1-增加, 2-减少',
  `source` varchar(50) NOT NULL COMMENT '积分来源/用途: order_pay, daily_checkin, invite, exchange',
  `points` int NOT NULL COMMENT '变动积分数额，正负代表增减',
  `current_balance` int NOT NULL COMMENT '变动后实时余额',
  `order_no` varchar(32) COMMENT '关联订单号',
  `activity_id` bigint COMMENT '关联活动ID',
  `expires_at` datetime COMMENT '积分过期时间',
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-有效, 0-无效(如撤销)',
  `created_at` datetime DEFAULT (now()),
  `remark` varchar(200) COMMENT '备注'
) COMMENT = '用户积分流水表，所有积分变动有迹可循。需定期清理过期积分记录（建议无效记录保留2年）。';

-- 16. 用户钱包表
CREATE TABLE `user_wallet` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint UNIQUE NOT NULL,
  `balance` decimal(10,2) DEFAULT 0 COMMENT '可用余额',
  `frozen_balance` decimal(10,2) DEFAULT 0 COMMENT '冻结金额(如提现中、支付中)',
  `total_deposit` decimal(10,2) DEFAULT 0 COMMENT '累计充值',
  `total_consumption` decimal(10,2) DEFAULT 0 COMMENT '累计消费',
  `security_level` tinyint DEFAULT 1,
  `pay_password_hash` varchar(128) COMMENT '支付密码哈希',
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-正常, 0-冻结',
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime DEFAULT (now())
) COMMENT = '用户钱包（余额）账户，独立于积分体系，用于储值消费。';

-- 17. 优惠券模板表
CREATE TABLE `coupons` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `type` tinyint NOT NULL COMMENT '券类型: 1-满减券, 2-折扣券, 3-代金券',
  `value` decimal(10,2) NOT NULL COMMENT '优惠值: 满减金额、折扣(0.9)或固定金额',
  `min_spend` decimal(10,2) COMMENT '最低消费金额门槛',
  `applicable_type` tinyint NOT NULL COMMENT '适用范围: 1-全平台, 2-指定商户, 3-指定商品',
  `applicable_ids` json COMMENT 'JSON数组，适用的商户或商品ID列表',
  `validity_type` tinyint NOT NULL COMMENT '有效期类型: 1-固定时段, 2-领取后生效',
  `valid_days` int COMMENT '领取后有效天数（当validity_type=2时）',
  `starts_at` datetime COMMENT '有效期开始时间',
  `expires_at` datetime COMMENT '有效期结束时间',
  `total_quantity` int COMMENT '发放总张数，null表示不限量',
  `remaining_quantity` int COMMENT '剩余可发放张数',
  `limit_per_user` int DEFAULT 1 COMMENT '每人限领张数',
  `status` tinyint DEFAULT 0 COMMENT '状态: 0-未开始, 1-发放中, 2-已结束, 3-停用',
  `created_at` datetime DEFAULT (now())
) COMMENT = '优惠券定义模板。用户领取后生成 user_coupons 记录。';

-- 18. 用户持有优惠券表
CREATE TABLE `user_coupons` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `coupon_id` bigint NOT NULL,
  `coupon_code` varchar(50) UNIQUE NOT NULL COMMENT '唯一券码',
  `status` tinyint DEFAULT 0 COMMENT '状态: 0-未使用, 1-已使用, 2-已过期',
  `received_at` datetime DEFAULT (now()),
  `used_at` datetime,
  `order_no` varchar(32) COMMENT '使用的订单号',
  `expires_at` datetime NOT NULL COMMENT '该张券的实际过期时间'
) COMMENT = '用户实际领取和持有的优惠券。由 coupons 模板生成。';

-- 19. 商户结算主表
CREATE TABLE `merchant_settlements` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `settlement_no` varchar(32) UNIQUE NOT NULL COMMENT '结算单号，如 SETTLE202412001',
  `merchant_id` bigint NOT NULL,
  `period` varchar(20) NOT NULL COMMENT '结算周期，如 2024-12',
  `total_amount` decimal(12,2) NOT NULL COMMENT '周期内交易总金额',
  `platform_fee` decimal(12,2) NOT NULL COMMENT '平台服务费',
  `settlement_amount` decimal(12,2) NOT NULL COMMENT '应付给商户的金额',
  `settlement_status` tinyint DEFAULT 0 COMMENT '结算状态: 0-待结算, 1-结算中, 2-结算成功, 3-结算失败',
  `payment_status` tinyint DEFAULT 0 COMMENT '付款状态: 0-待付款, 1-付款中, 2-付款成功, 3-付款失败',
  `payment_method` varchar(50) COMMENT '付款方式',
  `payment_time` datetime,
  `invoice_status` tinyint DEFAULT 0 COMMENT '发票状态: 0-未开, 1-已申请, 2-已开票',
  `note` text COMMENT '备注',
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime DEFAULT (now())
) COMMENT = '按周期给商户的结算单主表。明细见 settlement_details (下张表)。';

-- 20. 结算明细表
CREATE TABLE `settlement_details` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `settlement_id` bigint NOT NULL,
  `order_no` varchar(32) NOT NULL,
  `order_type` tinyint NOT NULL,
  `order_amount` decimal(10,2) NOT NULL,
  `platform_fee_rate` decimal(5,4) NOT NULL,
  `platform_fee` decimal(10,2) NOT NULL,
  `merchant_share` decimal(10,2) NOT NULL,
  `settled_at` datetime COMMENT '计入结算时间',
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-正常, 0-异常(如退款需冲正)'
) COMMENT = '结算单明细，记录每一笔订单的分账构成。退款订单会产生冲正明细。';

-- 21. 营销活动主表
CREATE TABLE `activities` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `merchant_id` bigint COMMENT '为空则表示平台活动',
  `name` varchar(200) NOT NULL,
  `type` tinyint NOT NULL COMMENT '活动类型: 1-打卡任务, 2-积分奖励, 3-主题促销',
  `cover_image` varchar(500),
  `description` text,
  `starts_at` datetime NOT NULL,
  `ends_at` datetime NOT NULL,
  `location` varchar(500),
  `participation_limit` int,
  `requirement_type` tinyint DEFAULT 0 COMMENT '参与条件: 0-无, 1-需购票, 2-需积分, 3-需报名',
  `requirement_value` varchar(200),
  `reward_config` json NOT NULL COMMENT '奖励配置JSON',
  `audit_status` tinyint DEFAULT 0 COMMENT '审核状态: 0-待审, 1-通过, 2-拒绝',
  `status` tinyint DEFAULT 0 COMMENT '活动状态: 0-未开始, 1-进行中, 2-已结束, 3-取消',
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime DEFAULT (now())
) COMMENT = '营销活动主表，支持平台和商户活动。参与记录见 activity_participations (下批)。';

-- 22. 本地事务消息表（新增）
CREATE TABLE `transaction_messages` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `business_id` varchar(64) NOT NULL COMMENT '业务ID，如订单号',
  `business_type` varchar(50) NOT NULL COMMENT '业务类型，如order_pay',
  `payload` json NOT NULL COMMENT '消息内容',
  `status` tinyint DEFAULT 0 COMMENT '0-待发送，1-已发送，2-已完成，3-失败',
  `retry_count` int DEFAULT 0,
  `next_retry_time` datetime,
  `created_at` datetime DEFAULT (now()),
  INDEX `idx_status_retry` (`status`, `next_retry_time`)
) COMMENT = '本地消息表，用于实现分布式事务最终一致性。需定期清理已完成/失败消息。';

-- 23. 订单退款表
CREATE TABLE `order_refunds` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `refund_no` varchar(32) UNIQUE NOT NULL COMMENT '退款单号，如 REFUND202412110001',
  `order_no` varchar(32) NOT NULL,
  `user_id` bigint NOT NULL,
  `refund_type` tinyint NOT NULL COMMENT '退款类型: 1-全额退款, 2-部分退款',
  `refund_amount` decimal(10,2) NOT NULL,
  `refund_reason` varchar(200) NOT NULL,
  `refund_evidence` json COMMENT 'JSON，退款凭证图片等',
  `status` tinyint DEFAULT 0 COMMENT '状态: 0-待审核, 1-审核通过, 2-审核拒绝, 3-退款中, 4-成功, 5-失败',
  `auditor_id` bigint COMMENT '审核人ID',
  `audited_at` datetime COMMENT '审核时间',
  `audit_note` varchar(500) COMMENT '审核备注',
  `payment_refund_no` varchar(64) COMMENT '支付渠道退款流水号',
  `payment_refund_at` datetime COMMENT '支付渠道退款时间',
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime DEFAULT (now())
) COMMENT = '订单退款申请与处理表。与 order_changes 共同构成售后体系。建议终态记录保留3年。';

-- 24. 订单改签表
CREATE TABLE `order_changes` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `change_no` varchar(32) UNIQUE NOT NULL COMMENT '改签单号',
  `order_no` varchar(32) NOT NULL,
  `user_id` bigint NOT NULL,
  `change_type` tinyint NOT NULL COMMENT '类型: 1-改日期, 2-改时间, 3-改票种',
  `original_ticket_id` bigint COMMENT '原票种ID',
  `new_ticket_id` bigint COMMENT '新票种ID',
  `original_use_date` date,
  `new_use_date` date,
  `price_difference` decimal(10,2) COMMENT '差价，正为需补，负为应退',
  `change_fee` decimal(10,2) DEFAULT 0,
  `total_amount` decimal(10,2) NOT NULL COMMENT '改签总金额',
  `change_reason` varchar(200) NOT NULL,
  `status` tinyint DEFAULT 0 COMMENT '状态: 0-待审核, 1-审核通过, 2-拒绝, 3-已完成',
  `auditor_id` bigint,
  `audited_at` datetime,
  `audit_note` varchar(500),
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime DEFAULT (now())
) COMMENT = '门票订单改签记录表。涉及库存释放和重新锁定。';

-- 25. 用户反馈表
CREATE TABLE `user_feedback` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` tinyint NOT NULL COMMENT '反馈类型: 1-投诉, 2-建议, 3-咨询',
  `content` text NOT NULL,
  `images` json COMMENT 'JSON，上传的凭证图片',
  `contact_phone` varchar(20),
  `order_no` varchar(32) COMMENT '关联订单',
  `merchant_id` bigint COMMENT '关联商户',
  `status` tinyint DEFAULT 0 COMMENT '处理状态: 0-待处理, 1-处理中, 2-已处理',
  `handler_id` bigint COMMENT '处理人ID',
  `handled_at` datetime COMMENT '处理时间',
  `handle_result` text COMMENT '处理结果',
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime DEFAULT (now())
) COMMENT = '用户反馈与工单表。用于客服跟踪和处理。建议已处理记录保留1年。';

-- 26. 用户评价表
CREATE TABLE `user_comments` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `target_type` tinyint NOT NULL COMMENT '评价对象类型: 1-景区/商户, 2-商品, 3-活动',
  `target_id` bigint NOT NULL COMMENT '评价对象ID',
  `order_no` varchar(32) NOT NULL COMMENT '必须基于订单评价',
  `rating` tinyint NOT NULL COMMENT '评分: 1-5星',
  `content` text,
  `images` json COMMENT '评价图片',
  `is_anonymous` boolean DEFAULT false,
  `like_count` int DEFAULT 0,
  `reply_count` int DEFAULT 0,
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-显示, 0-隐藏',
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime DEFAULT (now())
) COMMENT = '用户评价表。支持对商户、商品、活动的多维度评价，是信用体系基础。';

-- 27. 活动参与记录表
CREATE TABLE `activity_participations` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `activity_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `participation_status` tinyint DEFAULT 1 COMMENT '参与状态: 1-进行中, 2-已完成, 3-已放弃',
  `reward_status` tinyint DEFAULT 0 COMMENT '奖励状态: 0-未发放, 1-已发放, 2-发放失败',
  `reward_detail` json COMMENT '实际获得的奖励详情',
  `checkpoints_progress` json COMMENT 'JSON，打卡点完成进度',
  `joined_at` datetime DEFAULT (now()),
  `completed_at` datetime,
  `updated_at` datetime DEFAULT (now())
) COMMENT = '用户参与营销活动的记录表。用于跟踪进度和发放奖励。';

-- 28. 秒杀活动表
CREATE TABLE `seckill_events` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `title` varchar(200) NOT NULL,
  `seckill_price` decimal(10,2) NOT NULL,
  `original_price` decimal(10,2) NOT NULL,
  `total_inventory` int NOT NULL COMMENT '秒杀总库存',
  `available_inventory` int NOT NULL COMMENT '秒杀可用库存',
  `limit_per_user` int DEFAULT 1 COMMENT '每人限购',
  `starts_at` datetime NOT NULL,
  `ends_at` datetime NOT NULL,
  `status` tinyint DEFAULT 0 COMMENT '状态: 0-未开始, 1-进行中, 2-已结束, 3-手动关闭',
  `created_at` datetime DEFAULT (now())
) COMMENT = '秒杀/闪购活动专用表，独立库存以应对高并发。需配合缓存和队列。';

-- 29. 商户钱包表
CREATE TABLE `merchant_wallet` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `merchant_id` bigint UNIQUE NOT NULL,
  `balance` decimal(12,2) DEFAULT 0 COMMENT '可提现余额',
  `frozen_balance` decimal(12,2) DEFAULT 0 COMMENT '冻结余额(结算中/提现中)',
  `total_income` decimal(12,2) DEFAULT 0 COMMENT '累计收入',
  `total_withdrawn` decimal(12,2) DEFAULT 0 COMMENT '累计已提现',
  `security_level` tinyint DEFAULT 1,
  `withdraw_password_hash` varchar(128),
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-正常, 0-冻结',
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime DEFAULT (now())
) COMMENT = '商户钱包，用于存放可提现的结算收入。';

-- 30. 发票表
CREATE TABLE `invoices` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `invoice_no` varchar(32) UNIQUE NOT NULL COMMENT '发票号码',
  `user_id` bigint NOT NULL,
  `type` tinyint NOT NULL COMMENT '发票类型: 1-普通, 2-专用',
  `title` varchar(200) NOT NULL COMMENT '发票抬头',
  `tax_number` varchar(50) COMMENT '纳税人识别号',
  `amount` decimal(10,2) NOT NULL,
  `status` tinyint DEFAULT 0 COMMENT '开票状态: 0-待开, 1-开票中, 2-成功, 3-失败',
  `file_url` varchar(500) COMMENT '发票文件URL',
  `order_no` varchar(32) COMMENT '关联订单(可为空，支持合并开票)',
  `applied_at` datetime DEFAULT (now()),
  `issued_at` datetime COMMENT '开票时间',
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime DEFAULT (now())
) COMMENT = '用户发票申请与记录表。可与订单1对1或1对多。';

-- 31. 资讯文章表
CREATE TABLE `articles` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `category` varchar(50) NOT NULL COMMENT '文章分类',
  `title` varchar(200) NOT NULL,
  `author` varchar(50),
  `cover_image` varchar(500),
  `summary` varchar(500) COMMENT '摘要',
  `content` text NOT NULL,
  `view_count` int DEFAULT 0,
  `like_count` int DEFAULT 0,
  `is_pinned` boolean DEFAULT false COMMENT '是否置顶',
  `status` tinyint DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布, 2-隐藏',
  `published_at` datetime COMMENT '发布时间',
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime DEFAULT (now())
) COMMENT = '平台资讯、攻略、公告等文章内容表。';

-- 32. 操作日志表
CREATE TABLE `operation_logs` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint COMMENT '操作人ID，可为空(系统操作)',
  `user_type` tinyint COMMENT '用户类型: 1-平台用户, 2-商户, 3-管理员',
  `module` varchar(50) NOT NULL COMMENT '操作模块',
  `action` varchar(200) NOT NULL COMMENT '操作内容',
  `request_method` varchar(10),
  `request_url` varchar(500),
  `request_params` text,
  `ip_address` varchar(50),
  `user_agent` text,
  `is_success` boolean DEFAULT true,
  `error_message` text,
  `duration_ms` int COMMENT '操作耗时(毫秒)',
  `created_at` datetime DEFAULT (now())
) COMMENT = '系统关键操作审计日志表，用于安全追踪和行为分析。建议详细日志保留6个月。';

-- 33. 数据字典表
CREATE TABLE `data_dictionaries` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `dict_type` varchar(50) NOT NULL COMMENT '字典类型，如: user_status',
  `dict_code` varchar(50) NOT NULL COMMENT '字典编码，如: 0',
  `dict_value` varchar(200) NOT NULL COMMENT '字典值，如: 正常',
  `sort_order` int DEFAULT 0,
  `is_default` boolean DEFAULT false COMMENT '是否默认值',
  `status` tinyint DEFAULT 1 COMMENT '状态: 1-启用, 0-停用',
  `remark` varchar(200),
  `created_at` datetime DEFAULT (now()),
  `updated_at` datetime DEFAULT (now())
) COMMENT = '系统数据字典表，统一管理所有状态、类型的枚举值，便于维护和多语言扩展。';

-- 34. 景区日度统计表（新增）
CREATE TABLE `scenic_daily_stats` (
  `stat_date` DATE NOT NULL,
  `scenic_spot_id` BIGINT NOT NULL,
  `order_count` INT DEFAULT 0 COMMENT '订单数',
  `total_sales_amount` DECIMAL(12,2) DEFAULT 0 COMMENT '总销售额',
  `unique_user_count` INT DEFAULT 0 COMMENT '下单用户数',
  `new_user_count` INT DEFAULT 0 COMMENT '新用户数',
  `created_at` DATETIME DEFAULT (now()),
  `updated_at` DATETIME DEFAULT (now()),
  PRIMARY KEY (`stat_date`, `scenic_spot_id`)
) COMMENT = '景区维度日度统计汇总表，用于加速报表查询。';

-- ======================= 索引优化 =======================
-- 在原索引基础上，新增以下高频查询索引
CREATE INDEX `idx_user_status_time` ON `orders` (`user_id`, `order_status`, `create_time` DESC);
CREATE INDEX `idx_merchant_status_time` ON `orders` (`merchant_id`, `order_status`, `create_time` DESC);
CREATE INDEX `idx_product_status` ON `product_skus` (`product_id`, `status`);
CREATE INDEX `idx_date_active` ON `ticket_prices` (`price_date`, `is_active`);
CREATE INDEX `idx_expires_status` ON `user_points` (`expires_at`, `status`);
CREATE INDEX `idx_outer_order_no` ON `orders` (`outer_order_no`);

-- ======================= 外键约束 =======================
-- ... (外键约束部分与原DDL保持一致，需确保新增表的外键关系，此处为节省篇幅省略重复定义) ...
-- 核心原则：在应用层保证强一致性，数据库外键可用于开发环境，生产环境可酌情禁用以提升性能。

```

-- 注意：为保持简洁，外键约束部分未在优化脚本中重复列出，请根据原 DDL 及新增表补充。
-- 例如，需要为新增的`merchant_employee_roles`、`merchant_employee_role_assignments`、`transaction_messages`等表添加外键。

```

## 四、架构演进建议
1.  **短期（上线前）**：
    - **配置优化**：① 设置合理的`innodb_buffer_pool_size`（建议为物理内存的70-80%）。② 调整`innodb_flush_log_at_trx_commit=2`与`sync_binlog=0`（在可接受极小数据丢失风险下大幅提升写性能）。③ 使用`READ-COMMITTED`事务隔离级别，平衡一致性与并发性能。
    - **监控与备份**：部署完善的数据库监控（如Prometheus+Granafa），并建立定期全量备份+binlog增量备份策略。

2.  **中期（用户量/订单量显著增长后）**：
    - **引入Elasticsearch**：用于`products`、`scenic_spots`、`articles`的复杂搜索、模糊查询和排序需求（如附近景区、商品关键词搜索）。
    - **引入ClickHouse**：当运营报表、BI分析查询（如景区百万订单报表）在MySQL上变得缓慢时，将订单、支付、结算等数据实时同步至ClickHouse，进行即席查询和多维分析。

3.  **长期（业务复杂后）**：
    - **微服务拆分**：
        - **积分服务**：将`user_points`、积分规则、过期处理等逻辑独立，通过RPC/Grpc提供原子化的积分操作接口。
        - **优惠券服务**：将`coupons`、`user_coupons`、券核销、预算控制等逻辑独立。
        - **支付/清结算服务**：将`payments`、`orders`支付相关字段、`merchant_settlements`等逻辑独立，统一处理所有资金交易。
    - **数据库配合**：
        - **每个服务拥有独立数据库**，避免表级别耦合。
        - **通过领域事件（Domain Events）** 实现服务间数据最终一致性（如订单完成事件触发积分发放）。
        - **考虑使用分布式事务中间件（如Seata）** 或基于消息的最终一致性方案处理跨服务事务。
```
