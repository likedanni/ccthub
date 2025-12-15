# API 变更与说明（自动生成）

更新日期: 2025-12-15

## 最新变更 (2025-12-15)

### 票务管理功能

新增票种与票价管理的完整 REST API，支持门票产品配置、价格日历管理和库存控制。

#### 票种管理接口

- `POST /api/tickets`

  - 描述: 创建新的门票产品
  - 请求体: JSON格式，包含景区ID、票种名称、类型、有效期、退改签规则、限购设置等
  - 返回: 创建的票种详情（含ID、创建时间等）

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
  - 参数: status（0或1）

#### 票价管理接口

- `POST /api/ticket-prices`

  - 描述: 创建或更新单个票价（支持幂等操作）
  - 请求体: 包含票种ID、日期、价格类型、原价、售价、库存等
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

- 新增表 `tickets`（票种模板表）：17字段，包含景区关联、类型、有效期、退改签规则、限购设置等
- 新增表 `ticket_prices`（票价库存日历表）：13字段，包含价格类型、原价/售价、库存管理、乐观锁 version 字段

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
