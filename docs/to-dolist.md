# 开发流程计划（To-Do List）

> 说明：按 Sprint 单位执行，目标先交付核心四服务骨架（用户/票务/支付/商户）与公共组件（auth、ai-client、gateway、ops 配置），再做集成、性能与上线。

---

## 🚀 下一阶段开发计划（2025 年 12 月 - 2026 年 1 月）

### 📌 当前状态（2025-12-12）

✅ **已完成基础设施**

- user-service 基础架构（Spring Boot 3.1.4 + JDK 17）
- 用户注册/登录/JWT 认证体系
- Flyway 数据库迁移管理（V1-V3）
- CI/CD 管道（GitHub Actions + Testcontainers）
- 代码质量监控（JaCoCo + Codecov）
- Docker 容器化支持

✅ **技术栈确认**

- 后端：Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA
- 数据库：MySQL 8.0, Flyway migrations
- 测试：JUnit 5, Testcontainers, MockMvc
- CI/CD: GitHub Actions, Maven, JaCoCo, Codecov
- 容器化：Docker, Docker Compose

---

### 🎯 第一阶段：核心业务服务开发（4-6 周）

#### Week 1-2: 景点与内容管理服务

**目标**：实现景点基础信息管理和内容发布

**任务清单**：

- [ ] 创建 `attraction-service` 模块
  - [ ] 景点实体设计（id, name, description, location, category, price, images, status）
  - [ ] CRUD API 实现（管理员端）
  - [ ] 景点列表/详情查询 API（用户端）
  - [ ] 图片上传与存储（考虑 OSS 或本地存储）
  - [ ] 景点分类管理
  - [ ] Flyway 迁移脚本
- [ ] 集成搜索功能

  - [ ] 基础搜索（名称、分类、地区）
  - [ ] 排序（热度、价格、评分）
  - [ ] 分页支持

- [ ] 测试
  - [ ] 单元测试（Service 层）
  - [ ] 集成测试（Controller + Repository）
  - [ ] Testcontainers 集成

**验收标准**：

- 管理员可以创建/编辑/删除景点
- 用户可以浏览景点列表和查看详情
- 搜索和筛选功能正常
- 测试覆盖率 ≥ 70%
- CI 通过

---

#### Week 3-4: 订单与票务服务

**目标**：实现订单创建、票务管理和库存控制

**任务清单**：

- [ ] 创建 `order-service` 模块

  - [ ] 订单实体（order_id, user_id, attraction_id, quantity, total_price, status, created_at）
  - [ ] 订单状态机（PENDING → PAID → CONFIRMED → USED/CANCELLED）
  - [ ] 创建订单 API
  - [ ] 订单查询 API（用户订单列表、详情）
  - [ ] 订单取消逻辑
  - [ ] Flyway 迁移脚本

- [ ] 票务管理

  - [ ] 票种实体（ticket_type, price, validity, restrictions）
  - [ ] 库存管理（optimistic locking 防止超卖）
  - [ ] 票券生成（二维码/条形码）
  - [ ] 核销功能

- [ ] 订单与景点服务集成
  - [ ] REST 调用或消息队列（考虑后期引入）
  - [ ] 分布式事务处理方案（SAGA 或 2PC）

**验收标准**：

- 用户可以下单购票
- 库存正确扣减，无超卖
- 订单状态流转正确
- 票券可正常生成和核销
- 集成测试覆盖完整下单流程

---

#### Week 5-6: 支付服务与商户管理

**目标**：集成支付功能，支持商户入驻

**任务清单**：

- [ ] 创建 `payment-service` 模块

  - [ ] 支付订单创建
  - [ ] 第三方支付集成（支付宝/微信支付 sandbox）
  - [ ] 支付回调处理（webhook）
  - [ ] 退款功能
  - [ ] 支付状态查询
  - [ ] Flyway 迁移脚本

- [ ] 创建 `merchant-service` 模块

  - [ ] 商户注册/认证
  - [ ] 商户信息管理
  - [ ] 商品（景点/服务）上架管理
  - [ ] 订单分成与结算（初版）
  - [ ] RBAC 权限控制

- [ ] 支付与订单集成
  - [ ] 订单支付后状态更新
  - [ ] 支付超时自动取消
  - [ ] 异步通知处理

**验收标准**：

- 完整支付流程可走通（创建订单 → 支付 → 回调 → 订单确认）
- 商户可以入驻并管理自己的商品
- 支付模拟环境测试通过
- 异常场景处理完善（超时、失败、重试）

---

### 🎯 第二阶段：系统集成与优化（2-3 周）

#### Week 7-8: API 网关与服务编排

**任务清单**：

- [ ] 搭建 API Gateway（Spring Cloud Gateway）

  - [ ] 路由配置（/api/users/_, /api/attractions/_, /api/orders/\*, etc.）
  - [ ] JWT 认证拦截
  - [ ] 限流与熔断（Resilience4j）
  - [ ] 跨域配置
  - [ ] 日志与监控

- [ ] 服务间通信优化

  - [ ] 统一错误处理
  - [ ] API 版本管理
  - [ ] 接口文档（Swagger/OpenAPI）

- [ ] Docker Compose 联调环境
  - [ ] 编写 docker-compose.yml（所有服务 + MySQL + Redis）
  - [ ] 环境变量配置
  - [ ] 健康检查配置
  - [ ] 一键启动脚本

**验收标准**：

- 通过网关访问所有服务
- 认证和鉴权正常
- docker-compose up 可启动完整环境
- 端到端流程测试通过

---

#### Week 9: AI 功能集成

**任务清单**：

- [ ] 创建 `ai-client` 公共模块

  - [ ] 抽象 AI 服务接口
  - [ ] Claude API 集成
  - [ ] 模型切换配置（ai-defaults.yaml）
  - [ ] Mock 实现（用于测试）

- [ ] AI 功能应用
  - [ ] 景点智能推荐
  - [ ] 行程规划助手
  - [ ] 智能客服（问答）
  - [ ] 内容生成（景点描述优化）

**验收标准**：

- AI 推荐 API 可返回个性化结果
- 模型可通过配置切换
- Mock 模式下测试通过

---

### 🎯 第三阶段：质量保障与上线准备（2 周）

#### Week 10: 测试与性能优化

**任务清单**：

- [ ] 完善测试体系

  - [ ] E2E 测试（完整业务流程）
  - [ ] 压力测试（JMeter/Gatling）
  - [ ] 安全测试（OWASP Top 10）
  - [ ] 依赖漏洞扫描

- [ ] 性能优化
  - [ ] 数据库索引优化
  - [ ] 缓存策略（Redis）
  - [ ] 慢查询优化
  - [ ] 接口性能基线

**验收标准**：

- 核心接口响应时间 < 500ms
- 并发 100 QPS 稳定运行
- 无高危安全漏洞
- 测试覆盖率 ≥ 80%

---

#### Week 11: 监控与运维

**任务清单**：

- [ ] 监控系统搭建

  - [ ] Prometheus + Grafana
  - [ ] 关键指标监控（QPS, latency, error rate）
  - [ ] 告警配置
  - [ ] 日志聚合（ELK/Loki）

- [ ] 运维文档
  - [ ] 部署手册
  - [ ] 故障处理手册
  - [ ] 数据备份与恢复
  - [ ] 灰度发布方案

**验收标准**：

- 监控 Dashboard 可用
- 告警能正常触发
- 完整运维文档
- 回滚演练通过

---

### 📊 里程碑与交付物

| 里程碑           | 时间    | 交付物                                      |
| ---------------- | ------- | ------------------------------------------- |
| M1: 基础服务完成 | Week 6  | user/attraction/order/payment/merchant 服务 |
| M2: 系统集成完成 | Week 9  | API Gateway, AI 集成, Docker Compose        |
| M3: 质量保障完成 | Week 11 | 测试报告, 性能报告, 监控系统                |
| M4: 上线准备完成 | Week 12 | 部署文档, 运维手册, 灰度发布                |

---

### 🔄 开发工作流

**分支策略**：

- `main`: 生产分支
- `develop`: 开发集成分支
- `feature/*`: 功能分支
- `hotfix/*`: 紧急修复分支

**提交规范**：

```
feat: 新功能
fix: 修复
docs: 文档
test: 测试
refactor: 重构
chore: 构建/工具
```

**PR 流程**：

1. 创建 feature 分支
2. 完成开发 + 测试
3. 提交 PR（包含测试、文档更新）
4. Code Review
5. CI 通过后合并到 develop
6. 定期合并到 main

---

### 📝 注意事项

1. **数据库变更**：所有 schema 变更必须通过 Flyway 迁移，并在 PR 中说明
2. **API 兼容性**：新 API 版本需保持向后兼容，废弃 API 需提前通知
3. **安全要求**：敏感信息不可硬编码，使用环境变量或配置中心
4. **测试要求**：每个 PR 必须包含相应测试，覆盖率不能下降
5. **文档同步**：代码变更需同步更新 API 文档和使用说明

---

## 假设与节奏

- 每个 Sprint：1 周（可调整为 2 周）
- 技术栈：Java 17+ / Spring Boot，MySQL，Redis，Docker/Docker Compose，GitHub Actions，K8s（后期）
- 优先目标：交付 MVP → 迭代扩展

---

## 阶段概览（Sprint0 ~ Sprint8）

- Sprint 0（准备，1 周）：仓库结构、CI、开发规范、ops 文件
- Sprint 1（用户服务 MVP，2 周）：用户注册/登录/JWT/DB 迁移
- Sprint 2（票务与订单，2 周）：票务 CRUD、下单、支付模拟
- Sprint 3（商户服务与网关，1 周）：商户入驻、商品管理、API Gateway
- Sprint 4（AI 客户端与推荐，1 周）：ai-client 抽象 + 推荐 demo
- Sprint 5（联调容器化，1 周）：docker-compose 联调（MySQL/Redis/服务）
- Sprint 6（测试/性能/安全，1 周）：E2E、性能基线、安全扫描
- Sprint 7（监控与运维，1 周）：Prometheus/Grafana、日志集中化、SLA
- Sprint 8（预发布与上线，1 周）：灰度、回滚、运维文档

---

## 每个 Sprint 的标准流程

1. Sprint Planning（Mon）
2. 日会（15min）
3. feature/\* → PR → dev → code review → merge
4. QA（单元/集成测试应随 PR 提交）
5. Demo & Retro（Sprint 末）

验收标准：

- 单元覆盖率初期 ≥ 60%（长期目标 80%）
- CI 通过（build/test/lint）
- 关键流程 E2E 通过：注册 → 下单 → 支付 → 查询
- 无 High/Critical 依赖漏洞

---

## 详细 Sprint 任务要点

### Sprint 0 — 准备（1 周）

目标：仓库骨架、CI、ops、开发规范
任务：

- 初始化 monorepo（/backend、/frontend、/ops、/database、/docs）
- 添加 ops/ai-defaults.yaml（AI_DEFAULT_MODEL 默认 claude-haiku-4.5）
- 建立 GitHub Actions baseline（mvn build/test）
- 制定代码规范（formatter/checkstyle/branch policy）
  产出：仓库骨架、CI pipeline、开发规范文档
  验收：CI 能在 PR 上运行并通过简单 build/test

### Sprint 1 — 用户服务 MVP（2 周）

目标：user-service 支持 Register/Login/Profile、JWT、持久化
任务：

- 完成 user-service：Register/Login/Profile API、Spring Data JPA、Flyway/Liquibase 迁移
- 密码加密（BCrypt）、JWT（access/refresh）
- 单元测试 + 集成测试（SpringBootTest）
- application.yml 支持从环境变量读取 AI_DEFAULT_MODEL
  产出：可在本地与容器中运行的 user-service
  验收：POST /api/users/register 返回 201 且包含 token；mvn test 通过

### Sprint 2 — 票务与订单（2 周）

目标：ticket-service 与 order-service，订单链路通畅
任务：

- ticket-service：景区/票种 CRUD、库存预占
- order-service：创建订单、简单状态机
- payments：模拟第三方支付回调（webhook）
- 对应 DB 迁移脚本
  验收：本地 docker-compose 环境能完成购票 → 支付流程

### Sprint 3 — 商户服务与 API Gateway（1 周）

任务：

- merchant-service：入驻、商品管理 CRUD
- API Gateway（Spring Cloud Gateway / Nginx + JWT）
- RBAC 初版
  验收：商户登录并能创建商品，未授权访问返回 401

### Sprint 4 — AI 客户端与个性化（1 周）

任务：

- ai-client module：接口 + Mock + 实际 adapter（可切换模型）
- 示例 API（/api/recommendations）使用 ai-client 返回推荐
  验收：env 切换 AI_DEFAULT_MODEL 不影响逻辑；mock 推荐可返回

### Sprint 5 — 联调容器化（1 周）

任务：

- 完成 docker-compose.yml（user/ticket/order/merchant/mysql/redis）
- remote-debug 配置说明（IDE）
  验收：一键 docker-compose up 启动联调环境并完成端到端流程

### Sprint 6 — 测试/性能/安全（1 周）

任务：

- E2E 集成测试（Postman/Newman 或 Testcontainers）
- 性能测试基线（JMeter/Locust）
- 依赖漏洞扫描与静态安全检查
  验收：关键路径 E2E 通过；无高危漏洞

### Sprint 7 — 监控与运维（1 周）

任务：

- Prometheus + Grafana 指标采集（QPS、latency、errors）
- 日志集中化（ELK/Loki）
- 备份与灾备（RPO/RTO 文档）
  验收：关键告警能触发并在 dashboard 可视化

### Sprint 8 — 预发布与上线（1 周）

任务：

- 预发布环境与灰度策略
- 回滚验证、SLA 文档、运维演练
  验收：灰度稳定，回滚流程验证通过

---

## 立刻开始的第一步（建议）

- 验证 CI 与仓库结构（Sprint 0）
- 必做命令（在 /Users/like/CCTHub）：

```bash
# 创建本地分支并构建 user-service
cd /Users/like/CCTHub
git checkout -b feature/sprint0-setup origin/dev || git checkout -b feature/sprint0-setup

cd backend/user-service
mvn -DskipTests package

# 构建并运行 Docker 镜像（若需要）
docker build -t ccthub/user-service:local .
docker run --rm -e AI_DEFAULT_MODEL=claude-haiku-4.5 -p 8080:8080 ccthub/user-service:local
```

---

## 推荐下一步（请选择）

- A: 我现在执行 Sprint 0 剩余任务（创建 CI workflow、ops/ai-defaults），并提交本地分支改动。
- B: 直接进入 Sprint 1（完善 user-service JWT、DB 迁移）。
- C: 生成 docker-compose 联调模板（MySQL + Redis + core services）并放在 /ops 或 /examples。

请回复选择（A/B/C 或组合），我将按选项继续实施并更新 TODO 列表。

---

## 最近已完成的工作（更新记录）

下面是近期在 `backend/user-service` 上完成的重要项，已同步到分支 `ci/migration-jwt-test`：

- **创建分支并提交**：在 `ci/migration-jwt-test` 分支提交了本次修复与文档更新（包括测试清理、JWT 处理修复、README 更新）。
- **修复 JWT 密钥处理**：修复 `JwtTokenProvider`，在初始化时如果 `jwt.secret` 太短会生成并缓存一个合规密钥，避免 HS512 的 WeakKeyException（文件：`src/main/java/com/ccthub/userservice/util/JwtTokenProvider.java`）。生产环境请提供持久且 >=64 字节的 secret。
- **改进测试幂等性**：在集成测试 `UserControllerIntegrationTest` 中添加 `@BeforeEach` 清理 `users` 表，确保每次测试从干净状态开始（文件：`src/test/java/com/ccthub/userservice/controller/UserControllerIntegrationTest.java`）。
- **验证并应用 Flyway V2**：在 `127.0.0.1:3307`（docker 映射）上 Flyway 成功应用 V2 迁移并通过测试；在 `127.0.0.1:3306` 上发现 `users.status` 列为 `tinyint`，与实体期望的 `varchar` 不匹配，需通过新增 Flyway 迁移或调整实体以统一 schema（建议通过 Flyway 做变更）。
- **README 增加 JWT 指南**：在 `backend/user-service/README.md` 中加入了 `jwt.secret` 推荐（HS512 要求 >=512 bits / >=64 字节）以及生成示例（`openssl rand -base64 64`）。

以上工作已提交到本地分支 `ci/migration-jwt-test`（commit 示例：`cb1e96c`、`2faf9d7`）。

## 流程规则（新增） — 完成功能后必须更新 `to-dolist.md`

为保证项目进度透明与变更可追溯，添加以下必须遵守的轻量规则：

1. 每当完成一个功能/修复（包括迁移、重大配置或测试相关改动），提交 PR 前必须在 `docs/to-dolist.md` 中追加一条“最近已完成的工作”，包含：

- **短标题**（一行）：如 `修复 JWT 密钥处理`。
- **分支 / commit**：例如 `branch: ci/migration-jwt-test, commit: cb1e96c`。
- **受影响文件**：列出修改的主要文件路径。
- **简短说明**：为什么要改、对哪个环境影响、是否需要后续迁移或回滚说明。

2. `to-dolist.md` 的更新应放在文件的“最近已完成的工作”节顶部，最近的改动最先列出。更新可以由 PR 作者完成，也可在代码合并后由合并者补充。

3. CI/PR Review 要求：PR description 中应引用 `to-dolist.md` 相应条目（格式或行号），方便 reviewer 验证变更是否已记录。

4. 若变更涉及数据库 schema（Flyway 迁移），在 README 或 `docs` 中同时注明执行顺序（例如在主库上先做备份、然后执行 Flyway migrate）。

下次我会把此条目也补齐到 PR 描述里；若你接受该规则，我会把所有尚未记录的历史关键改动补充进 `to-dolist.md`。
