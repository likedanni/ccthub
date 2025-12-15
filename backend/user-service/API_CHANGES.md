# API 变更与说明（自动生成）

更新日期: 2025-12-15

## 最新变更 (2025-12-15 - Sprint 2.2-2.8 订单与票务完整功能)

### 订单管理功能

新增完整的订单管理 REST API，支持购票下单、库存扣减、订单支付、订单取消等核心购票流程。

#### 订单管理接口

- `POST /api/orders`

  - 描述: 创建订单（购票下单）
  - 请求体: 包含用户 ID、景区 ID、票种 ID、游玩日期、联系人、游客信息列表等
  - 功能: 自动扣减库存（乐观锁）、生成订单号、创建电子票券
  - 返回: 完整订单信息（含订单项列表）

- `POST /api/orders/{id}/pay`

  - 描述: 支付订单
  - 功能: 更新订单状态为"待使用"，释放锁定库存

- `POST /api/orders/{id}/cancel`

  - 描述: 取消订单
  - 功能: 释放锁定库存，更新订单状态为"已取消"
  - 限制: 仅支持待支付订单

- `GET /api/orders/{id}`

  - 描述: 查询订单详情
  - 返回: 订单信息 + 订单项列表（含核销码）

- `GET /api/orders/by-no/{orderNo}`

  - 描述: 根据订单号查询订单

- `GET /api/orders/user/{userId}`

  - 描述: 查询用户的所有订单

- `GET /api/orders`
  - 描述: 查询所有订单（管理后台）

### 电子票券核销功能

新增票券核销 REST API，支持核销码查询、单个核销、批量核销等功能。

#### 核销接口

- `GET /api/verifications/{verificationCode}`

  - 描述: 查询核销码信息
  - 返回: 游客信息、票价、核销状态等

- `POST /api/verifications/{verificationCode}/verify`

  - 描述: 核销电子票券
  - 参数: staffId（核销员 ID）
  - 功能: 更新核销状态、记录核销时间和核销员

- `POST /api/verifications/batch/{orderId}`
  - 描述: 批量核销订单中的所有票券
  - 参数: staffId（核销员 ID）

#### 数据库变更

- 新增表 `orders`（订单表）：19 字段，包含订单号、用户、景区、票种、游玩日期、金额、状态、时间戳等
- 新增表 `order_items`（订单项/电子票券表）：13 字段，包含游客信息、核销码（UUID）、核销状态、核销时间等

#### 关键特性

- **订单状态机**: PENDING_PAYMENT → PENDING_USE → COMPLETED / CANCELLED / REFUNDED
- **库存防超卖**:
  - 下单时扣减可用库存，增加锁定库存（乐观锁 @Version）
  - 支付时释放锁定库存
  - 取消时恢复可用库存
- **核销码**:
  - UUID 自动生成（@PrePersist）
  - 唯一索引防重复
  - 支持单个核销和批量核销
- **订单号生成**: 格式 `ORD + yyyyMMddHHmmss + 6位随机数`

#### 单元测试

新增测试类：

- `OrderServiceTest`：6 个测试用例（创建订单、支付、取消、查询等）
- `VerificationServiceTest`：5 个测试用例（核销信息查询、核销、批量核销等）
- 测试覆盖率: 11 个测试用例全部通过 ✅

---

## 历史变更 (2025-12-15 - Sprint 2.1 票务管理功能)

### 票务管理功能

新增票种与票价管理的完整 REST API，支持门票产品配置、价格日历管理和库存控制。

#### 票种管理接口

- `POST /api/tickets`

  - 描述: 创建新的门票产品
  - 请求体: JSON 格式，包含景区 ID、票种名称、类型、有效期、退改签规则、限购设置等
  - 返回: 创建的票种详情（含 ID、创建时间等）

- `PUT /api/tickets/{id}`

  - 描述: 更新指定票种的信息
  - 请求体: 完整票种信息（JSON）

- `GET /api/tickets/{id}`

  - 描述: 获取票种详细信息

- `DELETE /api/tickets/{id}`

  - 描述: 删除指定票种

- `GET /api/tickets`

  - 描述: 分页查询票种列表
  - 参数: page, size, sort（支持排序）

- `GET /api/tickets/scenic-spot/{scenicSpotId}`

  - 描述: 获取指定景区的所有票种

- `GET /api/tickets/scenic-spot/{scenicSpotId}/status/{status}`

  - 描述: 获取指定景区指定状态的票种（status: 0-下架, 1-上架）

- `PATCH /api/tickets/{id}/status`
  - 描述: 更新票种状态（上架/下架）
  - 参数: status（0 或 1）

#### 票价管理接口

- `POST /api/ticket-prices`

  - 描述: 创建或更新单个票价（支持幂等操作）
  - 请求体: 包含票种 ID、日期、价格类型、原价、售价、库存等
  - 返回: 保存的票价信息

- `POST /api/ticket-prices/batch`

  - 描述: 批量设置票价（日历批量设置）
  - 请求体: 票价数组（JSON）

- `GET /api/ticket-prices/{id}`

  - 描述: 获取票价详情

- `DELETE /api/ticket-prices/{id}`

  - 描述: 删除指定票价

- `GET /api/ticket-prices/ticket/{ticketId}`

  - 描述: 获取指定票种的所有票价

- `GET /api/ticket-prices/ticket/{ticketId}/date-range`
  - 描述: 获取指定日期范围内的票价
  - 参数: startDate, endDate（格式: yyyy-MM-dd）

#### 数据库变更

- 新增表 `tickets`（票种模板表）：17 字段，包含景区关联、类型、有效期、退改签规则、限购设置等
- 新增表 `ticket_prices`（票价库存日历表）：13 字段，包含价格类型、原价/售价、库存管理、乐观锁 version 字段

#### 关键特性

- **库存防超卖**: 使用乐观锁（@Version）+ 三段式库存（总库存、可用、锁定）
- **退改签规则**: JSON 配置（退款比例、改签费用、提前时间）
- **价格类型**: 支持成人票、学生票、儿童票、老年票
- **票种类型**: 单票、联票、套票
- **有效期类型**: 指定日期（单日有效）、有效天数（多日有效）

---

## 历史变更 (2025-12-15 之前)

简要说明:

- 新增通用文件上传接口并统一返回与历史兼容的字符串 URL。
- 新增用于只更新景区媒体字段的接口（`PUT /api/scenic-spots/{id}/media`）。
- 修复文件下载路由的回退查找逻辑（支持从 `uploads` 根目录递归查找实际文件）。
- 在 `application.yml` 中新增配置项：`file.upload-dir` 和 `file.base-url`。

主要变更接口:

- `POST /api/files/upload`

  - 描述: 通用文件上传，使用 multipart/form-data，字段 `file` 和 `category`（例如 `scenic`/`avatars`）。
  - 返回示例: `{"code":0,"message":"上传成功","data":"http://localhost:8080/api/files/scenic/{filename}"}` （`data` 为字符串 URL，兼容旧客户端）

- `GET /api/files/{category}/{filename}`

  - 描述: 根据 `category`（子目录）和 `filename` 返回文件流；若目标文件不存在，会在 `uploads` 根目录中递归查找并返回（兼容历史错误路径）。

- `PUT /api/scenic-spots/{id}/media`
  - 描述: 仅更新景区的媒体字段（`coverImage`, `images`）；适用于前端上传后，单独更新媒体信息的场景。
  - 请求示例: `{"coverImage":"http://.../api/files/scenic/xxx.png","images":["http://.../1.png","http://.../2.png"]}`

注意事项 & 验证建议:

- 确保 `file.upload-dir` 指向后端运行容器/主机上的 `uploads` 目录（相对或绝对路径均可），并且应用对该目录有读写权限。
- 若数据库中存在历史记录指向 `avatars` 但实际文件存放在 `scenic`，系统现在会回退查找；若需要长期一致性，建议在数据库上执行一次路径修正脚本（可由运维/DBA 协助）。
- 本地 Swagger UI 地址: `http://localhost:8080/swagger-ui/index.html#/`，OpenAPI JSON: `http://localhost:8080/v3/api-docs`。

变更关联文件（示例）:

- `FileController.java`, `FileStorageService.java`, `ScenicSpotController.java`, `ScenicSpotService.java`, `application.yml`, `frontend/admin-web/src/views/scenic/ScenicForm.vue`

若需我为你：

- 生成 DB 修复脚本以统一历史路径（avatars→scenic），或
- 将这些变更打包成一个 PR（含 PR 描述），
  请回复我想继续的操作。
