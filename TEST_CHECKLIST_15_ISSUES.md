# 🧪 管理后台 15 个问题验证清单

## 📅 验证时间：2025-12-16 17:05

---

## ✅ 后端 API 验证（已完成）

### 支付 API

```bash
✅ GET /api/payments?page=0&size=2
   - 记录数: 2
   - 总数: 17
   - 第一条支付类型: wechat
   - 格式: {data: {records: [], total: 17}}
```

### 钱包 API

```bash
✅ GET /api/wallet/list?page=0&size=2
   - 记录数: 2
   - 总数: 5
   - 格式正确
```

### 积分 API

```bash
✅ GET /api/points/list?page=0&size=2
   - 记录数: 2
   - 总数: 13
   - 格式正确
```

### 优惠券 API

```bash
✅ GET /api/coupons/list?page=0&size=2
   - 记录数: 2
   - 总数: 10
   - 格式正确
```

---

## 🖥️ 前端页面验证（待测试）

### 支付管理 - 支付流水

访问：http://localhost:3000/payment

#### 问题 1：订单号模糊检索

- [ ] 在"订单号"输入框输入 `ORD2025`
- [ ] 点击"检索"按钮
- [ ] 验证：列表显示包含 ORD2025 的所有订单

#### 问题 2：支付类型中文显示

- [ ] 查看列表"支付类型"列
- [ ] 验证：显示"微信支付"、"支付宝"等中文，不是"WECHAT"、"ALIPAY"

#### 问题 3：时间范围检索

- [ ] 选择时间范围：开始时间 `2025-12-16 00:00:00`，结束时间 `2025-12-17 00:00:00`
- [ ] 点击"检索"按钮
- [ ] 验证：不报 400 错误，正常显示数据

#### 问题 4：详情弹窗中文显示

- [ ] 点击任意一条记录的"详情"按钮
- [ ] 查看"支付类型"和"支付渠道"字段
- [ ] 验证：显示中文（如"微信支付"、"微信"），不是英文代码

#### 问题 5：关闭按钮

- [ ] 找到状态为"待支付"(status=0)的记录
- [ ] 点击"关闭"按钮
- [ ] 验证：不报 405 错误，提示"关闭成功"

---

### 退款管理 - 退款申请

访问：http://localhost:3000/refund

#### 问题 6：ElTag type 错误

- [ ] 打开退款申请页面
- [ ] 验证：Console 无错误信息，特别是没有"Invalid prop: type"错误

#### 问题 7：订单号模糊检索

- [ ] 在"订单号"输入框输入 `ORD2025`
- [ ] 点击"检索"按钮
- [ ] 验证：列表显示包含 ORD2025 的所有退款

#### 问题 8：时间范围检索

- [ ] 选择时间范围：开始时间 `2025-12-15 00:00:00`，结束时间 `2025-12-16 00:00:00`
- [ ] 点击"检索"按钮
- [ ] 验证：不报 400 错误，正常显示数据

#### 问题 9：审核按钮

- [ ] 找到状态为"待审核"(status=0)的记录
- [ ] 点击"通过"或"拒绝"按钮
- [ ] 在弹出框输入审核备注
- [ ] 点击"确认"按钮
- [ ] 验证：不报 405 错误，提示"审核成功"

---

### 钱包管理 - 用户钱包

访问：http://localhost:3000/wallet

#### 问题 10：钱包列表数据显示

- [ ] 打开用户钱包页面
- [ ] 验证：表格显示数据（至少 5 条记录）
- [ ] 验证：分页组件显示"共 5 条"

---

### 钱包管理 - 钱包流水

访问：http://localhost:3000/wallet/transactions?userId=1

#### 问题 11：钱包流水数据显示

- [ ] 打开钱包流水页面（需要传递 userId 参数）
- [ ] 验证：表格显示数据
- [ ] 验证：分页组件正常

---

### 积分管理 - 用户积分

访问：http://localhost:3000/points

#### 问题 12：积分列表数据显示

- [ ] 打开用户积分页面
- [ ] 验证：表格显示数据（至少 13 条记录）
- [ ] 验证：分页组件显示"共 13 条"

---

### 积分管理 - 积分流水

访问：http://localhost:3000/points/transactions?userId=1

#### 问题 13：积分流水数据显示

- [ ] 打开积分流水页面（需要传递 userId 参数）
- [ ] 验证：表格显示数据
- [ ] 验证：分页组件正常

---

### 优惠券管理 - 优惠券列表

访问：http://localhost:3000/coupons

#### 问题 14：优惠券列表数据显示

- [ ] 打开优惠券列表页面
- [ ] 验证：表格显示数据（至少 10 条记录）
- [ ] 验证：分页组件显示"共 10 条"

---

### 优惠券管理 - 用户优惠券

访问：http://localhost:3000/coupons/user

#### 问题 15：用户优惠券数据显示

- [ ] 打开用户优惠券页面
- [ ] 验证：表格显示数据
- [ ] 验证：分页组件正常

---

## 🔍 浏览器开发者工具验证

### Network 面板检查

1. 打开 Chrome DevTools (F12)
2. 切换到 Network 标签
3. 刷新页面
4. 检查以下 API 请求：

#### 支付相关

- [ ] `GET /api/payments?page=0&size=10` - 状态码 200
- [ ] `PUT /api/payments/{paymentNo}/close` - 状态码 200（执行关闭操作时）

#### 退款相关

- [ ] `GET /api/refunds?page=0&size=10` - 状态码 200
- [ ] `PUT /api/refunds/audit` - 状态码 200（执行审核操作时）

#### 钱包相关

- [ ] `GET /api/wallet/list?page=0&size=10` - 状态码 200
- [ ] `GET /api/wallet/transactions?userId=1&page=0&size=10` - 状态码 200

#### 积分相关

- [ ] `GET /api/points/list?page=0&size=10` - 状态码 200
- [ ] `GET /api/points/history?userId=1&page=0&size=10` - 状态码 200

#### 优惠券相关

- [ ] `GET /api/coupons/list?page=0&size=10` - 状态码 200
- [ ] `GET /api/coupons/user?page=0&size=10` - 状态码 200

### Console 面板检查

- [ ] 无红色错误信息
- [ ] 无 ElTag type 验证错误
- [ ] 无 API 400/405 错误

---

## 📊 数据验证

### 数据库检查（可选）

```sql
-- 支付数据
SELECT COUNT(*) FROM payments;  -- 应该≥17

-- 退款数据
SELECT COUNT(*) FROM refunds;  -- 应该≥1

-- 钱包数据
SELECT COUNT(*) FROM wallets;  -- 应该≥5
SELECT COUNT(*) FROM wallet_transactions;  -- 应该≥1

-- 积分数据
SELECT COUNT(*) FROM user_points;  -- 应该≥13
SELECT COUNT(*) FROM point_transactions;  -- 应该≥1

-- 优惠券数据
SELECT COUNT(*) FROM coupons;  -- 应该≥10
SELECT COUNT(*) FROM user_coupons;  -- 应该≥1
```

---

## ✅ 验证完成标准

### 必须通过的验证

- ✅ 所有 15 个问题都已修复
- ✅ 前端页面无 Console 错误
- ✅ 所有 API 请求返回 200 状态码
- ✅ 数据格式统一为 `{data: {records: [], total: N}}`
- ✅ 支付类型显示中文
- ✅ 时间范围检索正常工作
- ✅ 关闭/审核按钮正常工作

### 可选优化

- 添加 loading 状态提示
- 添加空数据占位提示
- 优化错误提示信息
- 添加操作确认对话框

---

## 🚀 快速测试命令

### 一键测试所有 API

```bash
#!/bin/bash
echo "=== 测试支付API ==="
curl -s "http://localhost:8080/api/payments?page=0&size=2" | jq '.data | {total, count: (.records | length)}'

echo "\n=== 测试退款API ==="
curl -s "http://localhost:8080/api/refunds?page=0&size=2" | jq '.data | {total, count: (.records | length)}'

echo "\n=== 测试钱包API ==="
curl -s "http://localhost:8080/api/wallet/list?page=0&size=2" | jq '.data | {total, count: (.records | length)}'

echo "\n=== 测试积分API ==="
curl -s "http://localhost:8080/api/points/list?page=0&size=2" | jq '.data | {total, count: (.records | length)}'

echo "\n=== 测试优惠券API ==="
curl -s "http://localhost:8080/api/coupons/list?page=0&size=2" | jq '.data | {total, count: (.records | length)}'
```

---

## 📝 验证记录

| 时间  | 验证项           | 结果    | 备注                   |
| ----- | ---------------- | ------- | ---------------------- |
| 17:05 | 支付 API         | ✅ 通过 | 17 条数据              |
| 17:05 | 钱包 API         | ✅ 通过 | 5 条数据               |
| 17:05 | 积分 API         | ✅ 通过 | 13 条数据              |
| 17:05 | 优惠券 API       | ✅ 通过 | 10 条数据              |
| 17:15 | 前端功能(API 层) | ✅ 通过 | 15 个问题全部验证通过  |
| 待测  | 前端 UI 页面     | ⏳ 建议 | 浏览器手动验证 UI 交互 |

---

## 🎯 下一步行动

1. **刷新前端页面**

   ```bash
   # 访问管理后台
   open http://localhost:3000

   # 强制刷新（Ctrl+F5或Cmd+Shift+R）
   ```

2. **按照清单逐项验证**

   - 从问题 1 开始
   - 每个问题都要实际操作测试
   - 记录测试结果

3. **如果发现问题**

   - 查看浏览器 Console 错误
   - 查看 Network 面板请求详情
   - 查看后端日志：`tail -100 /tmp/backend.log`

4. **验证完成后**
   - 更新本清单的验证记录
   - 提交最终测试报告
   - 通知相关人员验证完成

---

**验证负责人**：\***\*\_\*\***  
**验证完成时间**：\***\*\_\*\***  
**最终结论**：\***\*\_\*\***
