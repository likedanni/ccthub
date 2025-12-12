# 测试账号信息

## 快速开始指南

### 1. 启动环境

```bash
# 1. 启动Docker Desktop (如果未启动)
open -a Docker

# 2. 启动MySQL数据库
cd /Users/like/CCTHub/backend/user-service
docker-compose up -d

# 3. 启动后端服务
cd /Users/like/CCTHub/backend/user-service
mvn spring-boot:run

# 4. 启动PC管理后台
cd /Users/like/CCTHub/frontend/admin-web
npm run dev
```

### 2. 访问系统

- **后端 API**: http://localhost:8080
- **PC 管理后台**: http://localhost:3000

### 3. 登录测试

使用以下测试账号登录 PC 管理后台：

**管理员账号**

- 手机号: `13800138000`
- 密码: `test123456`
- 会员等级: 钻石会员
- 钱包余额: ¥1888.88
- 可用积分: 3000 分
- 支付密码: `888888`

### 普通用户账号

- 手机号: `13900139000`
- 密码: `user123456`
- 会员等级: 普通会员
- 钱包余额: ¥99.99
- 可用积分: 150 分

## 微信小程序测试账号

使用上述任一账号登录即可

## API 测试说明

### 登录接口

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"test123456"}'
```

### 获取用户信息

```bash
curl -X GET http://localhost:8080/api/users/1/profile \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 数据库说明

数据库名: `cct-hub`
用户表: `users`

测试用户已通过 V6 迁移脚本插入数据库。

## 注意事项

1. 后端服务地址: http://localhost:8080
2. PC 管理后台地址: http://localhost:3000 (需先运行 `npm run dev`)
3. 所有密码均使用 BCrypt 加密存储
4. 测试账号仅用于开发环境，生产环境需重新创建
5. JWT Token 有效期: 1 小时
6. Refresh Token 有效期: 7 天

## 问题排查

如果登录失败，请检查:

1. 后端服务是否正常运行 (http://localhost:8080)
2. 数据库连接是否正常
3. 测试用户数据是否已插入数据库
4. 浏览器开发者工具查看 Network 请求详情

## 手动插入测试用户 (如果数据库中没有)

```sql
-- 连接到MySQL数据库
USE `cct-hub`;

-- 插入管理员测试账号
INSERT INTO users (
    phone, password, phone_encrypted, nickname, avatar_url,
    member_level, growth_value, total_points, available_points, wallet_balance,
    payment_password, status, register_time, data_version
) VALUES (
    '13800138000',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- test123456
    'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
    '测试管理员',
    'https://avatars.githubusercontent.com/u/1?v=4',
    4, 10000, 5000, 3000, 1888.88,
    '$2a$10$rN9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- 888888
    'ACTIVE', NOW(), 1
);
```
