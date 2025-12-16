# 🔧 新 4 个问题修复计划

## 📅 创建时间：2025-12-16 17:20

---

## 📋 问题清单

### 问题 1: 支付管理 - 订单号模糊检索失效 ❌

**现状**: 检索"ORD2025"无结果  
**原因**: Repository 使用`=`精确匹配而非`LIKE`模糊匹配  
**影响**: 用户体验差,无法快速查找订单

### 问题 2: 退款管理 - 订单号模糊检索失效 ❌

**现状**: 检索"ORD2025"无结果  
**原因**: Repository 使用`=`精确匹配而非`LIKE`模糊匹配  
**影响**: 用户体验差,无法快速查找退款

### 问题 3: 用户 ID 显示改为用户名 🆕

**现状**: 后台管理系统中显示用户 ID  
**需求**: 改为显示用户名  
**影响范围**:

- 支付管理（payerId 字段）
- 退款管理（userId 字段）
- 钱包管理（userId 字段）
- 积分管理（userId 字段）
- 优惠券管理（userId 字段）

### 问题 4: 优惠券功能开发 🆕

**现状**:

- "编辑"功能未实现
- "发放"功能未实现
- "禁用"按钮无效果

**需求**:

- 实现编辑优惠券功能
- 实现发放优惠券功能
- 修复禁用功能

---

## 🎯 修复计划

### 阶段 1: 订单号模糊检索修复（问题 1-2）⏱️ 30 分钟

#### 1.1 修改 PaymentRepository（10 分钟）

**文件**: `PaymentRepository.java`

修改查询条件:

```java
// 修改前
"(:orderNo IS NULL OR p.orderNo = :orderNo)"

// 修改后
"(:orderNo IS NULL OR p.orderNo LIKE CONCAT('%', :orderNo, '%'))"
```

#### 1.2 修改 OrderRefundRepository（10 分钟）

**文件**: `OrderRefundRepository.java`

修改查询条件:

```java
// 修改前
"(:orderNo IS NULL OR r.orderNo = :orderNo)"

// 修改后
"(:orderNo IS NULL OR r.orderNo LIKE CONCAT('%', :orderNo, '%'))"
```

#### 1.3 编译测试（10 分钟）

- 后端编译
- API 测试
- 前端验证

---

### 阶段 2: 用户 ID 改为用户名显示（问题 3）⏱️ 90 分钟

#### 2.1 创建用户信息 DTO（15 分钟）

**新文件**: `UserInfoDTO.java`

```java
public class UserInfoDTO {
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
}
```

#### 2.2 修改 Response 对象（30 分钟）

**涉及文件**:

- `PaymentResponse.java` - 添加 payerName 字段
- `RefundResponse.java` - 添加 userName 字段
- `WalletResponse.java` - 添加 userName 字段
- `PointsResponse.java` - 添加 userName 字段
- `UserCouponResponse.java` - 添加 userName 字段

#### 2.3 修改 Service 层（30 分钟）

**涉及文件**:

- `PaymentService.java` - 查询用户信息
- `RefundService.java` - 查询用户信息
- `WalletService.java` - 查询用户信息
- `PointsService.java` - 查询用户信息
- `CouponService.java` - 查询用户信息

添加用户信息查询方法:

```java
private String getUserName(Long userId) {
    return userRepository.findById(userId)
        .map(User::getUsername)
        .orElse("未知用户");
}
```

#### 2.4 修改前端页面（15 分钟）

**涉及文件**:

- `PaymentList.vue` - 显示 payerName
- `RefundList.vue` - 显示 userName
- `WalletList.vue` - 显示 userName
- `PointsList.vue` - 显示 userName
- `UserCouponList.vue` - 显示 userName

修改列定义:

```vue
<!-- 修改前 -->
<el-table-column prop="userId" label="用户ID" />

<!-- 修改后 -->
<el-table-column prop="userName" label="用户" />
```

---

### 阶段 3: 优惠券功能开发（问题 4）⏱️ 120 分钟

#### 3.1 后端 API 开发（60 分钟）

##### 3.1.1 编辑功能（20 分钟）

**文件**: `CouponController.java`

已有`updateCoupon`方法,需确保前端正确调用:

```java
@PutMapping("/{id}")
public ResponseEntity<Map<String, Object>> updateCoupon(
    @PathVariable Long id,
    @RequestBody Coupon coupon)
```

##### 3.1.2 发放功能（20 分钟）

**文件**: `CouponController.java`

已有`grantCoupon`方法,需确保前端正确调用:

```java
@PostMapping("/{id}/grant")
public ResponseEntity<Map<String, Object>> grantCoupon(
    @PathVariable Long id,
    @RequestParam Long userId)
```

需添加批量发放功能:

```java
@PostMapping("/{id}/grant-batch")
public ResponseEntity<Map<String, Object>> grantCouponBatch(
    @PathVariable Long id,
    @RequestBody List<Long> userIds)
```

##### 3.1.3 禁用功能（20 分钟）

**文件**: `CouponController.java`

已有`updateCouponStatus`方法,需修复:

```java
@PutMapping("/{id}/status")
public ResponseEntity<Map<String, Object>> updateCouponStatus(
    @PathVariable Long id,
    @RequestParam Integer status)
```

**注意**: 需确认前端传递的 status 值正确（3=停用）

#### 3.2 前端页面开发（60 分钟）

##### 3.2.1 编辑功能（20 分钟）

**文件**: `frontend/admin-web/src/views/coupon/CouponList.vue`

添加编辑弹窗:

```vue
<el-dialog v-model="editDialogVisible" title="编辑优惠券">
  <el-form :model="editForm">
    <el-form-item label="优惠券名称">
      <el-input v-model="editForm.name" />
    </el-form-item>
    <!-- 其他字段 -->
  </el-form>
  <template #footer>
    <el-button @click="editDialogVisible = false">取消</el-button>
    <el-button type="primary" @click="submitEdit">确定</el-button>
  </template>
</el-dialog>
```

##### 3.2.2 发放功能（20 分钟）

**文件**: `CouponList.vue`

添加发放弹窗:

```vue
<el-dialog v-model="grantDialogVisible" title="发放优惠券">
  <el-form :model="grantForm">
    <el-form-item label="选择用户">
      <el-select v-model="grantForm.userId" filterable remote>
        <el-option 
          v-for="user in userList" 
          :key="user.id" 
          :label="user.username" 
          :value="user.id" 
        />
      </el-select>
    </el-form-item>
  </el-form>
  <template #footer>
    <el-button @click="grantDialogVisible = false">取消</el-button>
    <el-button type="primary" @click="submitGrant">确定</el-button>
  </template>
</el-dialog>
```

##### 3.2.3 禁用功能（20 分钟）

**文件**: `CouponList.vue`

修复禁用按钮:

```vue
// 添加禁用确认 const handleDisable = async (row) => { await
ElMessageBox.confirm('确定要禁用此优惠券吗?', '提示') const res = await
updateCouponStatus(row.id, 3) // 3=停用 if (res.success) {
ElMessage.success('禁用成功') loadData() } }
```

---

## 📝 修改文件清单

### 后端文件（7 个）

1. ✅ `PaymentRepository.java` - 模糊检索
2. ✅ `OrderRefundRepository.java` - 模糊检索
3. 🆕 `UserInfoDTO.java` - 用户信息 DTO
4. 🔧 `PaymentResponse.java` - 添加 payerName
5. 🔧 `RefundResponse.java` - 添加 userName
6. 🔧 `CouponController.java` - 批量发放
7. 🔧 `CouponService.java` - 批量发放逻辑

### 前端文件（6 个）

1. 🔧 `PaymentList.vue` - 显示 payerName
2. 🔧 `RefundList.vue` - 显示 userName
3. 🔧 `WalletList.vue` - 显示 userName
4. 🔧 `PointsList.vue` - 显示 userName
5. 🔧 `UserCouponList.vue` - 显示 userName
6. 🔧 `CouponList.vue` - 编辑/发放/禁用功能

---

## 🎯 执行顺序

### 第 1 步: 快速修复（问题 1-2）- 立即执行

- ✅ 修改 PaymentRepository（5 分钟）
- ✅ 修改 OrderRefundRepository（5 分钟）
- ✅ 编译测试（10 分钟）
- **预期时间**: 20 分钟

### 第 2 步: 用户名显示（问题 3）- 紧急需求

- 🔧 创建 DTO（15 分钟）
- 🔧 修改 Response（30 分钟）
- 🔧 修改 Service（30 分钟）
- 🔧 修改前端（15 分钟）
- **预期时间**: 90 分钟

### 第 3 步: 优惠券功能（问题 4）- 功能完善

- 🔧 后端 API（60 分钟）
- 🔧 前端页面（60 分钟）
- **预期时间**: 120 分钟

---

## ✅ 验收标准

### 问题 1-2: 模糊检索

- [ ] 输入"ORD"能检索到"ORD202512150001"
- [ ] 输入"2025"能检索到包含"2025"的所有订单
- [ ] 支付管理和退款管理都支持模糊检索

### 问题 3: 用户名显示

- [ ] 支付管理显示"张三"而非用户 ID
- [ ] 退款管理显示"李四"而非用户 ID
- [ ] 钱包、积分、优惠券管理都显示用户名
- [ ] 用户不存在时显示"未知用户"

### 问题 4: 优惠券功能

- [ ] 点击"编辑"能弹窗并修改优惠券信息
- [ ] 点击"发放"能弹窗选择用户并发放
- [ ] 点击"禁用"能正确禁用优惠券
- [ ] 所有操作都有成功/失败提示

---

## 📊 工作量评估

| 问题               | 难度        | 预计时间     | 优先级 |
| ------------------ | ----------- | ------------ | ------ |
| 问题 1-2: 模糊检索 | ⭐ 简单     | 20 分钟      | 🔴 高  |
| 问题 3: 用户名显示 | ⭐⭐ 中等   | 90 分钟      | 🟡 中  |
| 问题 4: 优惠券功能 | ⭐⭐⭐ 复杂 | 120 分钟     | 🟢 低  |
| **总计**           | -           | **230 分钟** | -      |

---

## 🚀 开始执行

准备开始修复,预计完成时间: **17:20 + 230 分钟 = 21:10**

**现在开始**: 17:20  
**第 1 步完成**: 17:40（问题 1-2）  
**第 2 步完成**: 19:10（问题 3）  
**第 3 步完成**: 21:10（问题 4）
