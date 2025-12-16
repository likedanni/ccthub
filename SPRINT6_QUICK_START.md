# Sprint 6: 活动与营销系统 - 快速启动指南

## 🚀 快速启动

### 1. 启动后端服务
```bash
cd /Users/like/CCTHub/backend/user-service
mvn spring-boot:run
```

### 2. 启动前端管理端
```bash
cd /Users/like/CCTHub/frontend/admin-web
npm run dev
```

### 3. 访问系统
- 管理端: http://localhost:5173
- 活动管理: http://localhost:5173/activities/list
- 秒杀管理: http://localhost:5173/seckills/list

---

## 📋 功能测试清单

### 活动管理测试

**1. 创建活动**
- [ ] 点击"创建活动"按钮
- [ ] 填写活动信息:
  - 活动名称: "长治太行山打卡活动"
  - 活动类型: 打卡任务
  - 开始时间: 2025-12-20 08:00:00
  - 结束时间: 2025-12-30 20:00:00
  - 奖励配置: `{"type":"points","value":100}`
- [ ] 点击"确定"创建
- [ ] 检查列表中是否出现新活动

**2. 审核活动**
- [ ] 找到"待审核"状态的活动
- [ ] 点击"通过"按钮
- [ ] 检查审核状态变为"审核通过"

**3. 上线活动**
- [ ] 找到"审核通过"且"未开始"的活动
- [ ] 点击"上线"按钮
- [ ] 检查活动状态变为"进行中"

**4. 搜索筛选**
- [ ] 在"活动名称"框输入关键字
- [ ] 选择活动类型 = "打卡任务"
- [ ] 选择审核状态 = "审核通过"
- [ ] 点击"查询"
- [ ] 检查列表只显示符合条件的活动

**5. 编辑活动**
- [ ] 点击某个活动的"编辑"按钮
- [ ] 修改活动描述
- [ ] 点击"确定"
- [ ] 检查活动信息已更新

**6. 下线活动**
- [ ] 找到"进行中"的活动
- [ ] 点击"下线"按钮
- [ ] 检查状态变为"已结束"

**7. 删除活动**
- [ ] 找到"已结束"或"未开始"的活动
- [ ] 点击"删除"按钮
- [ ] 确认删除
- [ ] 检查活动已从列表移除

---

### 秒杀管理测试

**1. 创建秒杀**
- [ ] 点击"创建秒杀"按钮
- [ ] 填写秒杀信息:
  - 秒杀标题: "景区门票限时秒杀"
  - 商品ID: 1
  - 秒杀价: 29.90
  - 原价: 99.00
  - 总库存: 100
  - 每人限购: 2
  - 开始时间: 2025-12-20 10:00:00
  - 结束时间: 2025-12-20 12:00:00
- [ ] 点击"确定"创建
- [ ] 检查列表中是否出现新秒杀

**2. 开始秒杀**
- [ ] 找到"未开始"的秒杀
- [ ] 点击"开始"按钮
- [ ] 检查状态变为"进行中"

**3. 编辑秒杀**
- [ ] 点击某个秒杀的"编辑"按钮
- [ ] 修改总库存为200
- [ ] 点击"确定"
- [ ] 检查库存已更新

**4. 结束秒杀**
- [ ] 找到"进行中"的秒杀
- [ ] 点击"结束"按钮
- [ ] 检查状态变为"已结束"

**5. 删除秒杀**
- [ ] 找到"已结束"的秒杀
- [ ] 点击"删除"按钮
- [ ] 确认删除
- [ ] 检查秒杀已从列表移除

---

## 🧪 API测试 (使用curl)

### 活动API测试

**1. 创建活动**
```bash
curl -X POST http://localhost:8080/api/activities \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": 1,
    "name": "长治太行山打卡活动",
    "type": 1,
    "description": "完成3个打卡点即可获得100积分",
    "startsAt": "2025-12-20 08:00:00",
    "endsAt": "2025-12-30 20:00:00",
    "location": "长治市太行山景区",
    "participationLimit": 1000,
    "requirementType": 1,
    "rewardConfig": "{\"type\":\"points\",\"value\":100}"
  }'
```

**2. 查询活动列表**
```bash
curl -X GET "http://localhost:8080/api/activities?page=0&size=10"
```

**3. 审核活动**
```bash
curl -X PUT "http://localhost:8080/api/activities/1/audit?auditStatus=1"
```

**4. 上线活动**
```bash
curl -X PUT "http://localhost:8080/api/activities/1/status?status=1"
```

**5. 查询进行中的活动**
```bash
curl -X GET "http://localhost:8080/api/activities/ongoing"
```

---

### 参与API测试

**1. 参与活动**
```bash
curl -X POST "http://localhost:8080/api/participations?activityId=1&userId=1"
```

**2. 更新打卡进度**
```bash
curl -X PUT "http://localhost:8080/api/participations/1/progress?checkpointId=checkpoint1"
```

**3. 完成活动**
```bash
curl -X PUT "http://localhost:8080/api/participations/1/complete"
```

**4. 发放奖励**
```bash
curl -X POST "http://localhost:8080/api/participations/1/grant-reward"
```

**5. 查询用户参与记录**
```bash
curl -X GET "http://localhost:8080/api/participations/user/1?page=0&size=10"
```

---

### 秒杀API测试

**1. 创建秒杀**
```bash
curl -X POST http://localhost:8080/api/seckills \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "title": "景区门票限时秒杀",
    "seckillPrice": 29.90,
    "originalPrice": 99.00,
    "totalInventory": 100,
    "limitPerUser": 2,
    "startsAt": "2025-12-20 10:00:00",
    "endsAt": "2025-12-20 12:00:00"
  }'
```

**2. 查询秒杀列表**
```bash
curl -X GET "http://localhost:8080/api/seckills?page=0&size=10"
```

**3. 开始秒杀**
```bash
curl -X PUT "http://localhost:8080/api/seckills/1/status?status=1"
```

**4. 处理秒杀购买**
```bash
curl -X POST "http://localhost:8080/api/seckills/1/purchase?userId=1&quantity=1"
```

**5. 查询进行中的秒杀**
```bash
curl -X GET "http://localhost:8080/api/seckills/ongoing"
```

---

## 🔍 常见问题

### 1. 后端启动失败
**问题**: 端口8080已被占用  
**解决**: 修改`application.yml`中的`server.port`

**问题**: 数据库连接失败  
**解决**: 检查`application.yml`中的数据库配置，确保MySQL已启动

### 2. 前端启动失败
**问题**: 端口5173已被占用  
**解决**: 修改`vite.config.js`中的`server.port`

**问题**: npm install失败  
**解决**: 删除`node_modules`和`package-lock.json`，重新执行`npm install`

### 3. 创建活动失败
**问题**: 奖励配置JSON格式错误  
**解决**: 确保rewardConfig为有效JSON，如`{"type":"points","value":100}`

**问题**: 时间格式错误  
**解决**: 使用格式`YYYY-MM-DD HH:mm:ss`，如`2025-12-20 08:00:00`

### 4. 参与活动失败
**问题**: 用户等级不足  
**解决**: 检查活动的requirementType和requirementValue，确保用户满足条件

**问题**: 活动未开始  
**解决**: 检查活动状态是否为"进行中"，审核状态是否为"审核通过"

### 5. 秒杀购买失败
**问题**: 库存不足  
**解决**: 检查availableInventory是否大于0

**问题**: 超过限购数量  
**解决**: 检查quantity是否超过limitPerUser

---

## 📊 数据库查询

### 查询活动数据
```sql
-- 查询所有活动
SELECT * FROM activities ORDER BY created_at DESC;

-- 查询进行中的活动
SELECT * FROM activities 
WHERE status = 1 AND audit_status = 1
  AND starts_at <= NOW() AND ends_at >= NOW();

-- 查询活动统计
SELECT 
  a.id, a.name, a.type,
  COUNT(p.id) as participant_count,
  SUM(CASE WHEN p.participation_status = 2 THEN 1 ELSE 0 END) as completed_count
FROM activities a
LEFT JOIN activity_participations p ON a.id = p.activity_id
GROUP BY a.id;
```

### 查询参与记录
```sql
-- 查询某用户的参与记录
SELECT * FROM activity_participations 
WHERE user_id = 1 
ORDER BY joined_at DESC;

-- 查询某活动的参与记录
SELECT 
  p.*, u.nickname, u.phone
FROM activity_participations p
LEFT JOIN users u ON p.user_id = u.id
WHERE p.activity_id = 1;

-- 查询待发放奖励的记录
SELECT * FROM activity_participations
WHERE participation_status = 2 AND reward_status = 0;
```

### 查询秒杀数据
```sql
-- 查询所有秒杀
SELECT * FROM seckill_events ORDER BY created_at DESC;

-- 查询进行中的秒杀
SELECT * FROM seckill_events
WHERE status = 1 
  AND starts_at <= NOW() AND ends_at >= NOW()
  AND available_inventory > 0;

-- 查询秒杀库存情况
SELECT 
  id, title, 
  total_inventory,
  available_inventory,
  (total_inventory - available_inventory) as sold_count,
  ROUND((total_inventory - available_inventory) * 100.0 / total_inventory, 2) as sold_percentage
FROM seckill_events;
```

---

## 📈 性能监控

### 数据库慢查询
```sql
-- 开启慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;

-- 查看慢查询日志
SHOW VARIABLES LIKE 'slow_query_log_file';
```

### 活动参与统计
```sql
-- 统计每日参与人数
SELECT 
  DATE(joined_at) as date,
  COUNT(*) as participant_count
FROM activity_participations
GROUP BY DATE(joined_at)
ORDER BY date DESC;

-- 统计活动类型分布
SELECT 
  type,
  COUNT(*) as activity_count
FROM activities
GROUP BY type;
```

### 秒杀库存监控
```sql
-- 监控库存紧张的秒杀
SELECT 
  id, title, 
  available_inventory,
  total_inventory,
  ROUND(available_inventory * 100.0 / total_inventory, 2) as inventory_percentage
FROM seckill_events
WHERE status = 1 
  AND available_inventory < total_inventory * 0.2;
```

---

## 🎯 下一步计划

### 即将实现的功能
1. **用户端页面** (优先级: 高)
   - 活动列表页
   - 活动详情页
   - 打卡页面
   - 秒杀页面

2. **打卡功能增强** (优先级: 高)
   - GPS打卡验证
   - 扫码打卡
   - 打卡照片上传
   - 打卡排行榜

3. **秒杀性能优化** (优先级: 中)
   - Redis预扣库存
   - 消息队列处理订单
   - 分布式锁
   - 限流控制

4. **推荐系统** (优先级: 中)
   - 热门景区推荐
   - 附近景区推荐
   - 热门活动推荐
   - 个性化推荐

---

**文档版本**: v1.0  
**最后更新**: 2025-12-16 21:10  
**维护人**: GitHub Copilot
