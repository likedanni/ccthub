# 开发流程计划（To-Do List）

> 说明：按 Sprint 单位执行，目标先交付核心四服务骨架（用户/票务/支付/商户）与公共组件（auth、ai-client、gateway、ops 配置），再做集成、性能与上线。

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
