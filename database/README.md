# 本地数据库初始化指南

## 概述

本项目使用本地 MySQL 8.0 数据库，通过 `init_local_db.sql` 脚本一键初始化所有表结构和测试数据。

**重要提示**: 本项目已弃用 Docker 和 Flyway 迁移方案，改为直接使用 SQL 脚本初始化。

## 前置条件

1. **安装 MySQL 8.0**

   - macOS: `brew install mysql@8.0`
   - Windows: 下载官方安装包
   - Linux: `sudo apt install mysql-server`

2. **启动 MySQL 服务**

   ```bash
   # macOS
   brew services start mysql@8.0

   # Linux
   sudo systemctl start mysql

   # Windows
   # 在服务管理器中启动 MySQL 服务
   ```

3. **设置 root 密码**（如果未设置）
   ```bash
   mysql_secure_installation
   ```

## 初始化步骤

### 方法一：使用 MySQL 命令行（推荐）

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 执行初始化脚本
source /Users/like/CCTHub/database/init_local_db.sql

# 3. 验证数据库创建
SHOW DATABASES;
USE cct_hub;
SHOW TABLES;

# 4. 验证测试数据
SELECT COUNT(*) FROM scenic_spots;    -- 应返回3（太行山大峡谷、八路军纪念馆、通天峡）
SELECT COUNT(*) FROM tickets;         -- 应返回3（成人票、优惠票、儿童票）
SELECT COUNT(*) FROM ticket_prices;   -- 应返回7（未来7天的成人票价）
SELECT * FROM tickets;
SELECT * FROM ticket_prices;

# 5. 退出
EXIT;
```

### 方法二：使用 MySQL Workbench（图形化）

1. 打开 MySQL Workbench
2. 连接到本地 MySQL 服务器（root 用户）
3. 点击 File → Open SQL Script
4. 选择 `/Users/like/CCTHub/database/init_local_db.sql`
5. 点击 Execute（闪电图标）
6. 在左侧 Schemas 面板中刷新，应看到 `cct_hub` 数据库

### 方法三：使用终端一键执行

```bash
cd /Users/like/CCTHub
mysql -u root -p < database/init_local_db.sql
```

## 数据库结构

### cct_hub 数据库包含以下表：

| 表名          | 说明              | 字段数 | 主要功能               |
| ------------- | ----------------- | ------ | ---------------------- |
| scenic_spots  | 景区信息表        | 14     | 景区基本信息、媒体资源 |
| tickets       | 票种模板表        | 19     | 票种配置、退改签规则   |
| ticket_prices | 票价日历表        | 13     | 价格日历、库存管理     |
| orders        | 订单表            | 19     | 订单信息、状态管理     |
| order_items   | 订单项/电子票券表 | 13     | 游客信息、核销码       |

### 测试数据清单

#### 1. 景区数据（3 条）

- 太行山大峡谷（id=1）
- 八路军太行纪念馆（id=2）
- 通天峡（id=3）

#### 2. 票种数据（3 条）

- 成人票（id=1，原价 120 元，售价 98 元，关联景区 1）
- 优惠票（id=2，原价 90 元，售价 68 元，关联景区 1）
- 儿童票（id=3，原价 60 元，售价 38 元，关联景区 1）

#### 3. 票价数据（7 条）

- 未来 7 天的成人票价（id=1 票种）
- 每天库存 1000 张
- 售价 98 元/张

## 配置 Spring Boot 连接

编辑 `backend/user-service/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cct_hub?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 你的MySQL密码
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: none # 重要：禁用自动建表，使用SQL脚本初始化
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
```

## 常见问题

### Q1: 执行脚本时报错 "Access denied for user 'root'@'localhost'"

**解决方案**:

```bash
# 重置 root 密码
mysql -u root
ALTER USER 'root'@'localhost' IDENTIFIED BY '新密码';
FLUSH PRIVILEGES;
```

### Q2: 脚本执行后数据库不存在

**解决方案**:

```bash
# 手动创建数据库
mysql -u root -p
CREATE DATABASE cct_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cct_hub;
SOURCE /Users/like/CCTHub/database/init_local_db.sql;
```

### Q3: 表已存在，如何重新初始化？

**解决方案**:

```sql
-- 删除整个数据库重建（⚠️ 注意：会清空所有数据）
DROP DATABASE IF EXISTS cct_hub;
SOURCE /Users/like/CCTHub/database/init_local_db.sql;
```

### Q4: 如何添加新的测试数据？

**解决方案**:

```sql
-- 在 init_local_db.sql 文件末尾添加 INSERT 语句
-- 或者手动执行 SQL
USE cct_hub;
INSERT INTO tickets (scenic_spot_id, ticket_name, ...) VALUES (...);
```

### Q5: 如何导出当前数据库结构？

**解决方案**:

```bash
# 导出结构和数据
mysqldump -u root -p cct_hub > backup.sql

# 仅导出结构
mysqldump -u root -p --no-data cct_hub > structure.sql
```

## 数据库维护

### 备份数据库

```bash
# 备份到 backups 目录
mysqldump -u root -p cct_hub > backups/cct-hub-$(date +%Y%m%d%H%M%S).sql
```

### 恢复数据库

```bash
mysql -u root -p cct_hub < backups/cct-hub-20251215000000.sql
```

### 查看数据库状态

```sql
-- 查看所有表
SHOW TABLES;

-- 查看表结构
DESCRIBE tickets;
DESCRIBE ticket_prices;
DESCRIBE orders;
DESCRIBE order_items;

-- 查看索引
SHOW INDEX FROM tickets;
SHOW INDEX FROM ticket_prices;

-- 查看数据统计
SELECT
    TABLE_NAME,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH,
    ROUND((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024, 2) AS 'SIZE_MB'
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'cct_hub';
```

## 注意事项

1. **字符集**: 所有表使用 `utf8mb4_unicode_ci` 字符集，支持 emoji 和多语言
2. **索引**: 每个表都有合理的索引设计，确保查询性能
3. **约束**:
   - 主键自增（AUTO_INCREMENT）
   - 外键关联（虽未强制，但逻辑关联明确）
   - 唯一索引（order_no、verification_code）
   - NOT NULL 约束（关键字段）
4. **乐观锁**: ticket_prices 表使用`version`字段防止超卖
5. **JSON 字段**: tickets 表的 refund_policy/change_policy 使用 JSON 类型

## 后续开发

当需要新增表或修改表结构时：

1. **直接修改 init_local_db.sql**（不使用 Flyway）
2. **测试 SQL 脚本**：在测试数据库中验证
3. **备份生产数据**：执行前务必备份
4. **执行变更**：生产环境执行 SQL
5. **更新文档**：同步更新本文档和 API_CHANGES.md

## 联系方式

如有问题，请联系：

- 项目负责人: @likedanni
- GitHub Issues: https://github.com/likedanni/ccthub/issues

---

最后更新: 2025-12-15  
维护者: GitHub Copilot
