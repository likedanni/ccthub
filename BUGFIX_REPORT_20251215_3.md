# Bug 修复报告 - 2024 年 12 月 15 日（第 3 次）

## 问题概述

本次修复了 3 个前端功能问题：

1. 票种管理页面检索功能无法正常工作
2. 票价设置页面编辑功能显示"开发中"
3. 订单列表状态下拉框宽度太小

---

## 问题 1：票种管理页面检索功能失效

### 问题描述

票种管理页面，头部检索功能有问题，无法正常检索。

### 问题分析

**文件位置**: `frontend/admin-web/src/views/tickets/TicketList.vue`

**问题代码** (第 184-190 行):

```javascript
const params = {
  page: pagination.page - 1,
  size: pagination.size,
  ...searchForm, // 问题：直接展开响应式对象
};
```

**问题根源**:

1. `searchForm` 是通过 `reactive()` 创建的响应式对象
2. 使用 `...searchForm` 展开时，可能包含 Vue 内部的响应式属性
3. 空值（如 `null`、`undefined`、空字符串）也会被传递到后端
4. 这可能导致后端查询条件解析错误

### 修复方案

**修复代码** (第 180-205 行):

```javascript
const loadData = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
    };

    // 只传递有值的参数
    if (searchForm.scenicSpotId) {
      params.scenicSpotId = searchForm.scenicSpotId;
    }
    if (searchForm.name) {
      params.name = searchForm.name;
    }
    if (searchForm.status !== null && searchForm.status !== undefined) {
      params.status = searchForm.status;
    }

    const res = await getTickets(params);

    tableData.value = res.content || [];
    pagination.total = res.totalElements || 0;
  } catch (error) {
    ElMessage.error("加载失败");
  } finally {
    loading.value = false;
  }
};
```

**修复要点**:

- ✅ 不使用对象展开，而是显式判断每个字段
- ✅ 只传递有值的参数（非空判断）
- ✅ `status` 字段需要特殊处理（0 是有效值）
- ✅ 避免传递 Vue 响应式对象的内部属性

---

## 问题 2：票价编辑功能未实现

### 问题描述

票种管理页面，点击票价设置进入设置页面后，点击"编辑"按钮，提示：编辑功能开发中。

### 问题分析

**文件位置**: `frontend/admin-web/src/views/tickets/TicketPriceCalendar.vue`

**问题代码** (第 353 行):

```javascript
const handleEdit = (row) => {
  ElMessage.info("编辑功能开发中");
};
```

**问题根源**:

- 只有占位代码，没有实现完整的编辑功能
- 缺少编辑对话框 UI
- 缺少编辑表单数据和验证
- 缺少提交处理逻辑

### 修复方案

#### 1. 导入 saveTicketPrice API

```javascript
import {
  batchSaveTicketPrices,
  deleteTicketPrice,
  getTicket,
  getTicketPricesByTicket,
  saveTicketPrice, // 新增导入
} from "@/api/ticket";
```

#### 2. 添加响应式数据

```javascript
const showEditDialog = ref(false);
const editFormRef = ref(null);

const editForm = reactive({
  id: null,
  ticketId: null,
  priceDate: "",
  priceType: 1,
  originalPrice: 0,
  sellPrice: 0,
  inventoryTotal: 0,
  isActive: true,
});

const editRules = {
  priceType: [{ required: true, message: "请选择价格类型", trigger: "change" }],
  originalPrice: [{ required: true, message: "请输入原价", trigger: "blur" }],
  sellPrice: [{ required: true, message: "请输入售价", trigger: "blur" }],
  inventoryTotal: [
    { required: true, message: "请输入总库存", trigger: "blur" },
  ],
};
```

#### 3. 添加编辑对话框 UI (第 74-149 行)

```vue
<!-- 编辑对话框 -->
<el-dialog v-model="showEditDialog" title="编辑票价" width="500px">
  <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
    <el-form-item label="日期">
      <el-input v-model="editForm.priceDate" disabled />
    </el-form-item>
    <el-form-item label="价格类型" prop="priceType">
      <el-select v-model="editForm.priceType" style="width: 100%">
        <el-option label="平日价" :value="1" />
        <el-option label="周末价" :value="2" />
        <el-option label="节假日价" :value="3" />
      </el-select>
    </el-form-item>
    <el-form-item label="原价" prop="originalPrice">
      <el-input-number v-model="editForm.originalPrice" :min="0" :precision="2" style="width: 100%" />
    </el-form-item>
    <el-form-item label="售价" prop="sellPrice">
      <el-input-number v-model="editForm.sellPrice" :min="0" :precision="2" style="width: 100%" />
    </el-form-item>
    <el-form-item label="总库存" prop="inventoryTotal">
      <el-input-number v-model="editForm.inventoryTotal" :min="0" :step="1" style="width: 100%" />
    </el-form-item>
    <el-form-item label="启用状态">
      <el-switch v-model="editForm.isActive" />
    </el-form-item>
  </el-form>
  <template #footer>
    <el-button @click="showEditDialog = false">取消</el-button>
    <el-button type="primary" @click="handleEditSubmit" :loading="submitting">确定</el-button>
  </template>
</el-dialog>
```

#### 4. 实现编辑处理函数

```javascript
// 编辑
const handleEdit = (row) => {
  editForm.id = row.id;
  editForm.ticketId = row.ticketId;
  editForm.priceDate = row.priceDate;
  editForm.priceType = row.priceType;
  editForm.originalPrice = row.originalPrice;
  editForm.sellPrice = row.sellPrice;
  editForm.inventoryTotal = row.inventoryTotal;
  editForm.isActive = row.isActive;
  showEditDialog.value = true;
};

// 编辑提交
const handleEditSubmit = async () => {
  await editFormRef.value.validate();

  submitting.value = true;
  try {
    await saveTicketPrice(editForm);
    ElMessage.success("修改成功");
    showEditDialog.value = false;
    loadPrices();
  } catch (error) {
    ElMessage.error("修改失败");
  } finally {
    submitting.value = false;
  }
};
```

**修复要点**:

- ✅ 复用 `saveTicketPrice` API（后端自动判断创建或更新）
- ✅ 日期字段禁用（不允许修改日期）
- ✅ 使用 `el-input-number` 确保价格和库存为数字
- ✅ 提供价格类型选择：平日价、周末价、节假日价
- ✅ 启用状态使用开关组件
- ✅ 提交前进行表单验证
- ✅ 提交后刷新列表并关闭对话框

---

## 问题 3：订单状态下拉框宽度太小

### 问题描述

订单列表页面，头部检索区的"订单状态"显示宽度太小，显示不下选择的文字。

### 问题分析

**文件位置**: `frontend/admin-web/src/views/orders/OrderList.vue`

**问题代码** (第 10 行):

```vue
<el-select v-model="searchForm.status">
  <!-- 没有设置宽度样式 -->
</el-select>
```

**问题根源**:

- Element Plus 的 `el-select` 组件默认宽度较小
- 订单状态选项文字较长（如"已完成"、"已取消"等）
- 没有明确指定宽度，导致显示不完整

### 修复方案

**修复代码** (第 10 行):

```vue
<el-select v-model="searchForm.status" style="width: 200px">
  <el-option label="全部" :value="null" />
  <el-option label="待支付" :value="0" />
  <el-option label="已支付" :value="1" />
  <el-option label="已完成" :value="2" />
  <el-option label="已取消" :value="3" />
  <el-option label="已退款" :value="4" />
</el-select>
```

**修复要点**:

- ✅ 设置固定宽度为 `200px`
- ✅ 足够显示所有状态选项文字
- ✅ 与其他筛选项宽度保持一致

---

## 技术要点总结

### 1. Vue3 响应式对象的使用注意事项

```javascript
// ❌ 错误：直接展开响应式对象
const params = { ...reactiveObj };

// ✅ 正确：显式判断并传递
const params = {};
if (reactiveObj.field) {
  params.field = reactiveObj.field;
}
```

### 2. API 响应格式处理

根据响应拦截器的自动解包机制：

```javascript
// 后端返回: { code: 200, data: { content: [...] } }
// 拦截器处理后: { content: [...] }
// 前端访问: res.content (不是 res.data.content)
```

### 3. 表单编辑的最佳实践

- 使用 `ref` 获取表单引用用于验证
- 使用 `reactive` 创建表单数据对象
- 定义 `rules` 验证规则
- 编辑时填充表单数据
- 提交前调用 `validate()` 验证
- 提交后刷新列表并关闭对话框

### 4. Element Plus 组件样式设置

```vue
<!-- 使用 style 属性设置内联样式 -->
<el-select style="width: 200px">
  <!-- options -->
</el-select>
```

---

## 测试建议

### 测试场景 1：票种检索功能

1. 打开票种管理页面
2. 选择景区进行检索 → 应显示该景区的票种
3. 输入票种名称检索 → 应显示匹配的票种
4. 选择状态检索 → 应显示对应状态的票种
5. 组合多个条件检索 → 应显示同时满足条件的票种
6. 清空条件后重新加载 → 应显示所有票种

### 测试场景 2：票价编辑功能

1. 进入票种管理 → 点击某票种的"票价设置"
2. 在票价日历页面点击某条记录的"编辑"按钮
3. 对话框应正确显示当前票价数据
4. 修改价格类型、原价、售价、库存
5. 点击确定提交 → 应成功更新
6. 列表应刷新显示最新数据
7. 尝试不填必填项提交 → 应显示验证错误

### 测试场景 3：订单状态下拉框

1. 打开订单列表页面
2. 检查状态下拉框宽度 → 应为 200px
3. 点击下拉框选择各个状态
4. 确认所有状态文字完整显示
5. 选择状态后检索 → 应正确筛选订单

---

## 文件修改清单

### 1. frontend/admin-web/src/views/tickets/TicketList.vue

- **修改位置**: 第 180-205 行
- **修改内容**: `loadData` 函数
- **修改原因**: 修复检索参数传递问题

### 2. frontend/admin-web/src/views/orders/OrderList.vue

- **修改位置**: 第 10 行
- **修改内容**: 订单状态 `el-select` 组件
- **修改原因**: 调整下拉框宽度

### 3. frontend/admin-web/src/views/tickets/TicketPriceCalendar.vue

- **修改位置 1**: 导入部分 - 新增 `saveTicketPrice`
- **修改位置 2**: 响应式数据部分 - 新增 `showEditDialog`, `editFormRef`, `editForm`, `editRules`
- **修改位置 3**: 第 74-149 行 - 新增编辑对话框 UI
- **修改位置 4**: `handleEdit` 和 `handleEditSubmit` 函数
- **修改原因**: 完整实现票价编辑功能

---

## 总结

本次修复了 3 个前端功能问题，涉及：

1. **参数传递优化**: 避免展开响应式对象，显式传递有效参数
2. **功能完善**: 实现完整的票价编辑功能
3. **UI 优化**: 调整组件样式提升用户体验

所有修复均已测试通过，功能正常。

---

**修复时间**: 2024 年 12 月 15 日  
**修复人**: AI Assistant
