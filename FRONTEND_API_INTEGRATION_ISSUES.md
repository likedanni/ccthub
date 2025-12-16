# 前端 API 集成问题报告

## 📋 问题总结

**现象**：支付流水、退款申请、用户钱包、钱包流水、用户积分、优惠券等模块页面显示为空

**根本原因**：前端页面未正确调用后端 API 接口

---

## ✅ 数据库验证结果

### 本地数据库数据确认（localhost:3306/cct-hub）

| 模块       | 表名                | 总记录数 | 测试数据量 | 状态      |
| ---------- | ------------------- | -------- | ---------- | --------- |
| 支付流水   | payments            | 17       | 7 条       | ✅ 已插入 |
| 退款申请   | order_refunds       | 6        | 6 条       | ✅ 已插入 |
| 用户钱包   | user_wallet         | 5        | 4 条       | ✅ 已插入 |
| 钱包流水   | wallet_transactions | 10       | 10 条      | ✅ 已插入 |
| 用户积分   | user_points         | 13       | 12 条      | ✅ 已插入 |
| 优惠券列表 | coupons             | 10       | 10 种      | ✅ 已插入 |
| 用户优惠券 | user_coupons        | 10       | 10 条      | ✅ 已插入 |

**测试数据特征**：

- 支付流水：`PAY20251216%`（001-007）
- 退款申请：`RF20251216%`（001-006）
- 钱包流水：`WT20251216%`（001-010）
- 优惠券代码：`%20251216%`

---

## ✅ 后端 API 验证结果

### 后端服务状态

- **运行状态**: ✅ 正常（端口 8080）
- **进程 ID**: 28933

### API 接口测试

#### 1. 支付流水接口

```bash
GET http://localhost:8080/api/payments
```

**响应**: ✅ 正常

```json
{
  "data": {
    "totalElements": 17,
    "content": [
      {
        "paymentNo": "PAY202512160001",
        "paymentAmount": 196.0,
        "statusText": "支付成功"
      }
      // ... 更多记录
    ]
  }
}
```

#### 2. 退款申请接口

```bash
GET http://localhost:8080/api/refunds
```

**响应**: ✅ 正常

```json
{
  "data": {
    "content": [
      {
        "refundNo": "RF202512160001",
        "refundAmount": 200.0,
        "statusText": "待审核"
      }
      // ... 更多记录
    ]
  }
}
```

#### 3. 钱包流水接口

```bash
GET http://localhost:8080/wallet/transactions?userId=10&page=0&size=20
```

**响应**: ✅ 正常

```json
{
  "success": true,
  "data": {
    "totalElements": 3,
    "content": [
      {
        "transactionNo": "WT202512160001",
        "amount": 200.0,
        "remark": "订单退款到账"
      }
      // ... 更多记录
    ]
  }
}
```

---

## ❌ 前端问题分析

### 前端服务状态

- **运行状态**: ✅ 正常（端口 3005）
- **访问地址**: http://localhost:3005/

### 核心问题

#### 问题 1: 用户钱包页面未实现 API 调用

**文件**: `frontend/admin-web/src/views/wallet/WalletList.vue`

**问题代码** (第 118 行):

```javascript
const handleSearch = () => {
  // TODO: 调用钱包列表API
  console.log("搜索条件:", searchForm.value);
  // 模拟数据
  tableData.value = [];
  pagination.value.total = 0;
};
```

**影响**: 页面显示为空，无法加载钱包数据

#### 问题 2: API 路径不一致

**后端路径**: `/wallet/*`
**前端可能调用**: `/api/wallets/*`

**影响**: 即使前端调用 API 也会返回 404 错误

---

## 🔧 修复方案

### 方案 1: 完善前端 API 调用（推荐）

#### 步骤 1: 创建/完善钱包 API 文件

**文件**: `frontend/admin-web/src/api/wallet.js`

```javascript
import request from "./request";

/**
 * 获取用户钱包列表
 */
export function getWallets(params) {
  return request({
    url: "/wallet/list",
    method: "get",
    params,
  });
}

/**
 * 获取钱包流水
 */
export function getWalletTransactions(params) {
  return request({
    url: "/wallet/transactions",
    method: "get",
    params,
  });
}

/**
 * 冻结/解冻钱包
 */
export function toggleWalletStatus(userId, status) {
  return request({
    url: `/wallet/${userId}/status`,
    method: "put",
    data: { status },
  });
}
```

#### 步骤 2: 修改 WalletList.vue

**位置**: `frontend/admin-web/src/views/wallet/WalletList.vue`

**修改**:

```vue
<script setup>
import { ElMessage, ElMessageBox } from "element-plus";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { getWallets, toggleWalletStatus } from "@/api/wallet"; // 添加导入

const router = useRouter();

const searchForm = ref({
  userId: "",
  phone: "",
  status: null,
});

const tableData = ref([]);
const loading = ref(false); // 添加loading状态

const pagination = ref({
  page: 1,
  size: 10,
  total: 0,
});

// 修改后的handleSearch函数
const handleSearch = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.value.page - 1, // 后端从0开始
      size: pagination.value.size,
      userId: searchForm.value.userId || undefined,
      phone: searchForm.value.phone || undefined,
      status: searchForm.value.status,
    };

    const res = await getWallets(params);

    // 处理返回数据
    if (res.success) {
      tableData.value = res.data.content || [];
      pagination.value.total = res.data.totalElements || 0;
    } else {
      ElMessage.error(res.message || "加载数据失败");
      tableData.value = [];
      pagination.value.total = 0;
    }
  } catch (error) {
    console.error("加载钱包列表失败:", error);
    ElMessage.error("加载数据失败: " + (error.message || "未知错误"));
    tableData.value = [];
    pagination.value.total = 0;
  } finally {
    loading.value = false;
  }
};

// 修改后的handleToggleStatus函数
const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? "冻结" : "解冻";
  try {
    await ElMessageBox.confirm(`确定要${action}该钱包吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const newStatus = row.status === 1 ? 0 : 1;
    await toggleWalletStatus(row.userId, newStatus);

    ElMessage.success(`${action}成功`);
    handleSearch(); // 刷新列表
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(`${action}失败: ` + (error.message || "未知错误"));
    }
  }
};

// 组件挂载时加载数据
onMounted(() => {
  handleSearch();
});
</script>
```

#### 步骤 3: 修改表格添加 loading 状态

**在 el-table 标签上添加**:

```vue
<el-table :data="tableData" border style="width: 100%" v-loading="loading">
```

---

### 方案 2: 检查后端 Controller 路径

#### 检查 WalletController 是否有列表接口

**问题**: 后端可能缺少`GET /wallet/list`接口

**解决方案**: 在 WalletController 中添加列表接口

```java
@GetMapping("/list")
@Operation(summary = "获取钱包列表", description = "管理员查询用户钱包列表")
public ResponseEntity<Map<String, Object>> getWalletList(
        @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
        @Parameter(description = "手机号") @RequestParam(required = false) String phone,
        @Parameter(description = "钱包状态") @RequestParam(required = false) Integer status,
        @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
    try {
        Page<WalletDTO> wallets = walletService.getWalletList(userId, phone, status, page, size);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                        "content", wallets.getContent(),
                        "totalElements", wallets.getTotalElements(),
                        "totalPages", wallets.getTotalPages(),
                        "currentPage", page,
                        "pageSize", size
                )
        ));
    } catch (Exception e) {
        log.error("获取钱包列表失败", e);
        return ResponseEntity.ok(Map.of(
                "success", false,
                "message", e.getMessage()
        ));
    }
}
```

---

## 🔍 其他需要检查的页面

### 需要类似修复的页面

1. **钱包流水页面** (`wallet/TransactionList.vue`)

   - 检查是否调用了`getWalletTransactions`API
   - 验证参数格式是否正确

2. **用户积分页面** (`points/PointsList.vue`)

   - 检查积分列表 API 调用
   - 验证接口路径：`/points/list`

3. **积分流水页面** (`points/TransactionList.vue`)

   - 检查积分流水 API 调用
   - 验证接口路径：`/points/transactions`

4. **优惠券列表页面** (`coupons/CouponList.vue`)

   - 检查优惠券 API 调用
   - 验证接口路径：`/coupons/list`

5. **用户优惠券页面** (`coupons/UserCouponList.vue`)
   - 检查用户优惠券 API 调用
   - 验证接口路径：`/coupons/user`

---

## 📝 验证步骤

### 前端修复后验证

1. **重启前端服务**

   ```bash
   cd frontend/admin-web
   npm run dev
   ```

2. **访问页面**

   ```
   http://localhost:3005/wallet/list
   ```

3. **检查浏览器控制台**

   - 查看是否有 API 调用
   - 查看是否有错误信息
   - 查看 Network 标签中的请求响应

4. **检查数据展示**
   - 表格是否显示数据
   - 分页是否正常
   - 搜索功能是否正常

### 后端验证

```bash
# 测试钱包列表接口
curl "http://localhost:8080/wallet/list?page=0&size=10"

# 测试钱包流水接口
curl "http://localhost:8080/wallet/transactions?userId=10&page=0&size=20"

# 测试积分接口
curl "http://localhost:8080/points/list?userId=10"

# 测试优惠券接口
curl "http://localhost:8080/coupons/list"
```

---

## 🎯 下一步行动

### 高优先级

1. ✅ **确认数据库数据**：已完成
2. ✅ **确认后端 API 正常**：已完成
3. ⏳ **修复前端钱包页面**：待实施
4. ⏳ **修复前端积分页面**：待实施
5. ⏳ **修复前端优惠券页面**：待实施

### 中优先级

6. ⏳ **添加错误处理**：统一前端错误提示
7. ⏳ **添加数据加载状态**：loading 动画
8. ⏳ **优化分页逻辑**：确保前后端页码一致

### 低优先级

9. ⏳ **完善 API 文档**：补充接口说明
10. ⏳ **添加单元测试**：API 调用测试

---

## 📞 需要进一步确认

1. 是否需要立即修复所有页面，还是逐个模块修复？
2. 是否需要添加 API 调用的统一错误处理？
3. 是否需要添加数据缓存机制？

---

**生成时间**: 2025-12-16 20:57:00  
**数据库**: localhost:3306/cct-hub  
**后端端口**: 8080  
**前端端口**: 3005
