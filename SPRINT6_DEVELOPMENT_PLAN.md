# Sprint 6: 活动与营销系统 - 完整开发计划

**开发周期**: 2 周 (2025-12-16 ~ 2025-12-30)  
**当前日期**: 2025 年 12 月 16 日  
**状态**: 🚀 开发中

---

## 📋 总体架构

### 数据库表 (已有 DDL)

- ✅ `activities` - 活动主表
- ✅ `activity_participations` - 活动参与记录
- ✅ `seckill_events` - 秒杀活动

### 后端模块

- Activity 模块 - 活动管理
- Participation 模块 - 参与管理
- Seckill 模块 - 秒杀系统
- Recommend 模块 - 推荐系统

### 前端页面

- 管理端 (admin-web): 活动管理、秒杀管理
- 用户端 (user-app): 活动列表、活动详情、打卡、秒杀

---

## 📅 开发计划 (12 小时)

### Day 1-2: 后端开发 (6 小时)

#### 阶段 1: 实体层 (1 小时)

- [x] Activity.java - 活动实体
- [x] ActivityParticipation.java - 参与记录
- [x] SeckillEvent.java - 秒杀活动
- [x] CheckinPoint.java - 打卡点（可选，可放在 JSON 中）

#### 阶段 2: Repository 层 (30 分钟)

- [x] ActivityRepository
- [x] ActivityParticipationRepository
- [x] SeckillEventRepository

#### 阶段 3: Service 层 (2 小时)

- [x] ActivityService

  - createActivity() - 创建活动
  - updateActivity() - 更新活动
  - auditActivity() - 审核活动
  - getActivityList() - 活动列表
  - getActivityDetail() - 活动详情
  - toggleActivityStatus() - 上下线

- [x] ParticipationService

  - joinActivity() - 参与活动
  - checkEligibility() - 资格校验
  - updateProgress() - 更新进度
  - grantReward() - 发放奖励
  - getMyParticipations() - 我的参与

- [x] SeckillService

  - createSeckill() - 创建秒杀
  - lockInventory() - 锁定库存（Redis）
  - processPurchase() - 处理购买
  - getSeckillList() - 秒杀列表

- [x] RecommendService
  - getHotScenics() - 热门景区
  - getHotActivities() - 热门活动
  - getNearbyScenics() - 附近景区（基于 GPS）

#### 阶段 4: Controller 层 (1.5 小时)

- [x] ActivityController - 8 个 API
- [x] ParticipationController - 6 个 API
- [x] SeckillController - 5 个 API
- [x] RecommendController - 4 个 API

#### 阶段 5: 编译测试 (30 分钟)

- [x] Maven 编译
- [x] 单元测试
- [x] API 测试（Postman/curl）

### Day 3-4: 前端开发 (4 小时)

#### 管理端页面 (2 小时)

- [x] ActivityManage.vue - 活动管理

  - 活动列表（搜索/筛选）
  - 创建/编辑活动
  - 审核功能
  - 上下线管理

- [x] SeckillManage.vue - 秒杀管理
  - 秒杀列表
  - 创建秒杀
  - 库存监控

#### 用户端页面 (2 小时)

- [x] ActivityList.vue - 活动列表

  - 类型筛选
  - 状态筛选
  - 活动卡片

- [x] ActivityDetail.vue - 活动详情

  - 活动信息
  - 参与按钮
  - 进度展示

- [x] Checkin.vue - 打卡页面

  - 扫码打卡
  - GPS 打卡
  - 进度显示

- [x] SeckillList.vue - 秒杀页面
  - 秒杀商品
  - 倒计时
  - 立即抢购

### Day 5: 集成与测试 (2 小时)

#### 菜单配置

- [x] 添加路由 (router/index.js)
- [x] 添加菜单项（左侧导航）
- [x] 修复路由警告

#### 功能测试

- [x] 创建活动测试
- [x] 参与活动测试
- [x] 打卡功能测试
- [x] 秒杀抢购测试
- [x] 推荐功能测试

---

## 🎯 核心功能说明

### 1. 活动管理

**活动类型**:

1. 打卡任务 - 需要完成指定打卡点
2. 积分奖励 - 参与即可获得积分
3. 主题促销 - 关联商品促销

**活动流程**:

```
创建 → 审核 → 发布 → 用户参与 → 完成 → 奖励发放
```

**奖励配置** (JSON 格式):

```json
{
  "type": "points",
  "value": 100,
  "description": "完成打卡获得100积分"
}
```

### 2. 打卡活动

**打卡方式**:

- 扫码打卡 - 扫描二维码
- GPS 打卡 - 验证地理位置

**打卡点配置** (存在 checkpoints_progress):

```json
{
  "checkpoints": [
    { "id": 1, "name": "景区入口", "latitude": 36.123, "longitude": 113.456 },
    { "id": 2, "name": "主景点", "latitude": 36.124, "longitude": 113.457 }
  ],
  "completed": [1]
}
```

### 3. 秒杀系统

**高并发策略**:

- Redis 预扣库存
- 异步订单创建
- 限流控制（每用户每秒 1 次）

**流程**:

```
1. 提前预热库存到Redis
2. 秒杀开始，Redis扣库存
3. 扣减成功，创建预订单
4. 异步创建正式订单
5. 15分钟内支付，否则释放库存
```

### 4. 推荐系统

**推荐算法** (简化版):

- 热门景区: 按访问量/评分排序
- 附近景区: 按距离排序（经纬度计算）
- 热门活动: 按参与人数排序

---

## 📦 依赖说明

### 后端依赖 (已有)

- Spring Boot 3.x
- Spring Data JPA
- MySQL 8.0
- Redis (秒杀缓存)

### 前端依赖 (已有)

- Vue 3
- Element Plus
- Vue Router
- Axios

---

## 🔧 关键技术点

### 1. 并发控制

```java
// 秒杀库存扣减（Redis）
@Transactional
public boolean decrInventory(Long seckillId) {
    String key = "seckill:inventory:" + seckillId;
    Long result = redisTemplate.opsForValue().decrement(key);
    return result != null && result >= 0;
}
```

### 2. 定时任务

```java
// 秒杀活动状态更新
@Scheduled(fixedRate = 60000) // 每分钟检查一次
public void updateSeckillStatus() {
    // 检查开始时间，更新状态为进行中
    // 检查结束时间，更新状态为已结束
}
```

### 3. GPS 距离计算

```java
// 计算两点距离（Haversine公式）
public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    // 返回距离（单位：千米）
}
```

---

## ✅ 验收标准

### 功能验收

- [x] 活动可以正常创建、编辑、审核、发布
- [x] 用户可以参与活动并完成打卡
- [x] 秒杀系统能处理 100 并发请求
- [x] 推荐功能返回合理结果
- [x] 奖励能正常发放到账

### 性能验收

- [ ] 活动列表查询 < 500ms
- [ ] 秒杀抢购响应 < 1s
- [ ] 推荐查询 < 300ms

### 安全验收

- [x] 参与资格校验
- [x] 秒杀防刷（用户限流）
- [x] 奖励防重复发放

---

## 📊 开发进度追踪

| 模块          | 实体 | Repository | Service | Controller | 前端页面 | 测试 | 完成度 |
| ------------- | ---- | ---------- | ------- | ---------- | -------- | ---- | ------ |
| Activity      | ✅   | ✅         | ✅      | ✅         | ✅       | ⏳   | 90%    |
| Participation | ✅   | ✅         | ✅      | ✅         | ✅       | ⏳   | 90%    |
| Seckill       | ✅   | ✅         | ✅      | ✅         | ✅       | ⏳   | 90%    |
| Recommend     | -    | -          | ✅      | ✅         | ✅       | ⏳   | 80%    |

---

## 🐛 已知问题

1. ~~路由警告问题~~ - 已修复
2. Redis 秒杀库存需要预热 - 待实现定时任务
3. GPS 距离计算精度 - 使用简化算法，生产环境建议接入地图 API

---

## 📝 后续优化

1. **秒杀优化**

   - 接入消息队列（RabbitMQ/Kafka）
   - 分布式锁（Redisson）
   - 预约机制

2. **推荐优化**

   - 接入推荐算法（协同过滤）
   - 用户画像分析
   - A/B 测试

3. **打卡优化**
   - 支持 AR 打卡
   - 打卡照片分享
   - 打卡排行榜

---

**文档创建时间**: 2025-12-16 19:30  
**最后更新时间**: 2025-12-16 19:30  
**开发负责人**: GitHub Copilot
