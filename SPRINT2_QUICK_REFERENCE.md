# Sprint 2.1 景区管理功能 - 快速参考

## 🚀 快速启动

### 后端服务

```bash
cd /Users/like/CCTHub/backend/user-service
mvn spring-boot:run
```

访问: http://localhost:8080

### 前端服务

```bash
cd /Users/like/CCTHub/frontend/admin-web
npm run dev
```

访问: http://localhost:3001

### 测试账号

- **管理员**: 13900000001 / admin123
- **普通用户**: 13900000002 / password123

---

## 📡 API 接口速查

### 1. 获取景区列表

```bash
GET /api/scenic-spots/list?page=0&pageSize=10&city=长治市
```

### 2. 获取景区详情

```bash
GET /api/scenic-spots/1
```

### 3. 创建景区（需要 JWT Token）

```bash
POST /api/scenic-spots
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "新景区",
  "level": "AAAA",
  "province": "山西省",
  "city": "长治市",
  "address": "详细地址",
  ...
}
```

### 4. 更新景区状态

```bash
PUT /api/scenic-spots/1/status?status=INACTIVE
Authorization: Bearer <token>
```

### 5. 删除景区

```bash
DELETE /api/scenic-spots/1
Authorization: Bearer <token>
```

---

## 🗄️ 数据库速查

### 查看景区列表

```sql
USE `cct-hub`;
SELECT id, name, level, city, status, view_count FROM scenic_spots;
```

### 插入测试数据

```sql
INSERT INTO scenic_spots (name, level, province, city, address, status)
VALUES ('测试景区', 'AAAA', '山西省', '长治市', '测试地址', 'ACTIVE');
```

---

## 📂 文件位置速查

### 后端文件

```
backend/user-service/src/main/java/com/ccthub/userservice/
├── model/ScenicSpot.java                    # 实体
├── dto/
│   ├── ScenicSpotRequest.java              # 请求 DTO
│   ├── ScenicSpotResponse.java             # 列表 DTO
│   └── ScenicSpotDetailResponse.java       # 详情 DTO
├── repository/ScenicSpotRepository.java     # 数据访问
├── service/ScenicSpotService.java          # 业务逻辑
└── controller/ScenicSpotController.java    # REST API

backend/user-service/src/main/resources/db/migration/
└── V7__create_scenic_spots.sql             # 数据库迁移
```

### 前端文件

```
frontend/admin-web/src/
├── api/scenic.js                           # API 客户端
├── views/scenic/
│   ├── ScenicList.vue                      # 列表页
│   ├── ScenicForm.vue                      # 表单页
│   └── ScenicDetail.vue                    # 详情页
└── router/index.js                         # 路由配置（已修改）
```

---

## 🔍 常见问题

### Q1: 后端启动失败怎么办？

检查:

1. MySQL 是否运行: `mysql -uroot -p12345678 -e "SELECT 1"`
2. 端口 8080 是否被占用: `lsof -i :8080`
3. 查看日志: `tail -f /tmp/backend_scenic7.log`

### Q2: 前端页面空白？

检查:

1. 后端是否启动
2. 浏览器控制台是否有错误
3. 网络请求是否成功（F12 Network）

### Q3: 景区列表为空？

执行:

```sql
SELECT COUNT(*) FROM scenic_spots;
```

如果为 0，重新插入测试数据。

### Q4: 图片上传失败？

检查:

1. `uploads/avatars/` 目录是否存在
2. JWT Token 是否有效
3. 文件大小是否超过限制

---

## 🎨 前端页面导航

1. 登录: http://localhost:3001/login
2. 仪表盘: http://localhost:3001/dashboard
3. 景区列表: http://localhost:3001/scenic/list
4. 用户列表: http://localhost:3001/user/list

---

## 📊 测试数据

### 景区 1: 太行山大峡谷

- ID: 1
- 等级: AAAA
- 城市: 长治市 壶关县
- 标签: 自然风光, 峡谷, 徒步

### 景区 2: 八路军太行纪念馆

- ID: 2
- 等级: AAAA
- 城市: 长治市 武乡县
- 标签: 红色旅游, 历史文化, 爱国教育

### 景区 3: 通天峡景区

- ID: 3
- 等级: AAAA
- 城市: 长治市 平顺县
- 标签: 峡谷, 瀑布, 玻璃栈道

---

## 🛠️ 开发命令

### Maven 命令

```bash
mvn clean compile    # 编译
mvn test            # 运行测试
mvn spring-boot:run # 启动服务
```

### Git 命令

```bash
git status          # 查看状态
git add .           # 添加所有更改
git commit -m "..." # 提交
git push origin main # 推送
```

### 数据库命令

```bash
mysql -uroot -p12345678 "cct-hub"  # 连接数据库
```

---

## 📞 技术支持

- **GitHub**: https://github.com/likedanni/ccthub
- **提交哈希**: 6c663b80
- **完成日期**: 2025-12-15
