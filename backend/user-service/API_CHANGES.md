# API 变更与说明（自动生成）

更新日期: 2025-12-15

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
