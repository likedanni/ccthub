# User Service API 文档

> 本文档供 AI 开发工具(如 Codex)使用,包含所有接口的请求参数和响应格式

**Base URL:** `http://localhost:8080`

---

## 1️⃣ 用户认证

### 用户注册

```http
POST /api/users/register
Content-Type: application/json
```

**请求体:**

```json
{
  "phone": "13800138000",
  "password": "password123",
  "verificationCode": "123456"
}
```

**响应:**

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "phone": "13800138000"
  }
}
```

### 用户登录

```http
POST /api/users/login
Content-Type: application/json
```

**请求体:**

```json
{
  "phone": "13800138000",
  "password": "password123"
}
```

**响应:**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "nickname": "用户138****8000",
    "avatarUrl": "http://localhost:8080/api/files/avatars/xxx.jpg",
    "memberLevel": "BRONZE",
    "points": 0,
    "balance": 0.0
  }
}
```

---

## 2️⃣ 用户信息

### 获取基本信息

```http
GET /api/users/{id}
```

**响应:**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "phone": "138****8000",
    "nickname": "张三",
    "avatarUrl": "http://localhost:8080/api/files/avatars/xxx.jpg",
    "memberLevel": "BRONZE"
  }
}
```

### 获取详细资料

```http
GET /api/users/{id}/profile
```

**响应:**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "phone": "138****8000",
    "nickname": "张三",
    "avatarUrl": "http://localhost:8080/api/files/avatars/xxx.jpg",
    "realName": "张三",
    "idCard": "110101199001011234",
    "memberLevel": "BRONZE",
    "growthValue": 0,
    "totalPoints": 0,
    "availablePoints": 0,
    "walletBalance": 0.0,
    "registerTime": "2024-01-01T10:00:00",
    "lastLoginTime": "2024-01-15T14:30:00"
  }
}
```

### 更新用户资料 ⭐

```http
PUT /api/users/{id}/profile
Content-Type: application/json
```

**请求体 (支持部分更新,传哪个更新哪个):**

```json
{
  "nickname": "新昵称", // 可选,单独更新昵称
  "avatarUrl": "https://...", // 可选,单独更新头像URL
  "realName": "张三", // 可选,单独更新真实姓名
  "idCard": "110101199001011234" // 可选,单独更新身份证(会加密存储)
}
```

**示例 - 只更新昵称:**

```json
{
  "nickname": "新昵称"
}
```

**响应:**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "nickname": "新昵称",
    "avatarUrl": "http://localhost:8080/api/files/avatars/xxx.jpg",
    "realName": "张三",
    "idCard": "110101********1234",
    "memberLevel": "BRONZE",
    "growthValue": 0,
    "totalPoints": 0,
    "availablePoints": 0,
    "walletBalance": 0.0
  }
}
```

---

## 3️⃣ 文件上传 🆕

### 上传头像

```http
POST /api/files/upload/avatar
Content-Type: multipart/form-data
```

**请求参数:**

- `file`: 图片文件 (JPG/JPEG/PNG, 最大 2MB)

**响应:**

```json
{
  "code": 200,
  "message": "上传成功",
  "data": "http://localhost:8080/api/files/avatars/550e8400-e29b-41d4-a716-446655440000.jpg"
}
```

**完整流程 - 上传并更新头像:**

```javascript
// 1. 先上传图片
const uploadResponse = await fetch("/api/files/upload/avatar", {
  method: "POST",
  body: formData, // FormData with file
});
const { data: avatarUrl } = await uploadResponse.json();

// 2. 更新用户资料中的头像URL
await fetch(`/api/users/${userId}/profile`, {
  method: "PUT",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ avatarUrl }),
});
```

### 获取头像图片

```http
GET /api/files/avatars/{filename}
```

---

## 4️⃣ 密码管理

### 修改登录密码

```http
POST /api/users/{id}/change-password
Content-Type: application/json
```

**请求体:**

```json
{
  "oldPassword": "oldpass123",
  "newPassword": "newpass456"
}
```

**响应:**

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

### 设置/修改支付密码

```http
POST /api/users/{id}/payment-password
Content-Type: application/json
```

**请求体:**

```json
{
  "paymentPassword": "123456" // 必须是6位数字
}
```

**响应:**

```json
{
  "code": 200,
  "message": "支付密码设置成功",
  "data": null
}
```

### 验证支付密码

```http
POST /api/users/{id}/verify-payment-password
Content-Type: application/json
```

**请求体:**

```json
{
  "paymentPassword": "123456"
}
```

**响应:**

```json
{
  "code": 200,
  "message": "验证成功",
  "data": true
}
```

---

## 📋 通用响应格式

所有接口都使用统一的响应格式:

```json
{
  "code": 200, // HTTP状态码
  "message": "成功", // 提示信息
  "data": {} // 具体数据(根据接口不同而不同)
}
```

**常见错误码:**

- `400` - 请求参数错误
- `401` - 未授权(需要登录)
- `404` - 资源不存在
- `500` - 服务器内部错误

---

## 🔐 认证说明

需要认证的接口需要在请求头中携带 JWT Token:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Accept: application/json
```

**获取 Token 的方式:**

1. 用户注册 (`/api/users/register`) 后返回 token
2. 用户登录 (`/api/users/login`) 后返回 token

---

## 💡 Codex 使用建议

### 在微信小程序中调用示例:

```javascript
// 1. 用户登录
wx.request({
  url: "http://localhost:8080/api/users/login",
  method: "POST",
  data: {
    phone: "13800138000",
    password: "password123",
  },
  success(res) {
    // 保存token
    wx.setStorageSync("token", res.data.data.token);
    wx.setStorageSync("userId", res.data.data.userId);
  },
});

// 2. 获取用户资料(需要token)
wx.request({
  url: `http://localhost:8080/api/users/${userId}/profile`,
  method: "GET",
  header: {
    Authorization: "Bearer " + wx.getStorageSync("token"),
  },
  success(res) {
    console.log("用户资料:", res.data.data);
  },
});

// 3. 上传头像
wx.chooseImage({
  count: 1,
  success(res) {
    wx.uploadFile({
      url: "http://localhost:8080/api/files/upload/avatar",
      filePath: res.tempFilePaths[0],
      name: "file",
      header: {
        Authorization: "Bearer " + wx.getStorageSync("token"),
      },
      success(uploadRes) {
        const avatarUrl = JSON.parse(uploadRes.data).data;

        // 4. 更新用户资料中的头像URL
        wx.request({
          url: `http://localhost:8080/api/users/${userId}/profile`,
          method: "PUT",
          header: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + wx.getStorageSync("token"),
          },
          data: {
            avatarUrl: avatarUrl,
          },
          success() {
            wx.showToast({ title: "头像更新成功" });
          },
        });
      },
    });
  },
});

// 5. 单独更新昵称
wx.request({
  url: `http://localhost:8080/api/users/${userId}/profile`,
  method: "PUT",
  header: {
    "Content-Type": "application/json",
    Authorization: "Bearer " + wx.getStorageSync("token"),
  },
  data: {
    nickname: "新昵称", // 只传需要更新的字段
  },
  success() {
    wx.showToast({ title: "昵称更新成功" });
  },
});
```

---

## 5️⃣ 用户地址管理

### 创建地址

```http
POST /api/addresses
Content-Type: application/json
```

**请求体:**

```json
{
  "userId": 1,
  "recipientName": "张三",
  "recipientPhone": "13800138000",
  "province": "山西省",
  "city": "长治市",
  "district": "潞州区",
  "detailAddress": "某某街道某某小区1号楼101室",
  "isDefault": true
}
```

**响应:**

```json
{
  "success": true,
  "message": "地址创建成功",
  "data": {
    "id": 1,
    "userId": 1,
    "recipientName": "张三",
    "recipientPhone": "13800138000",
    "province": "山西省",
    "city": "长治市",
    "district": "潞州区",
    "detailAddress": "某某街道某某小区1号楼101室",
    "isDefault": true,
    "createTime": "2025-12-16T10:30:00",
    "updateTime": "2025-12-16T10:30:00"
  }
}
```

### 更新地址

```http
PUT /api/addresses/{id}
Content-Type: application/json
```

**请求体:**

```json
{
  "userId": 1,
  "recipientName": "李四",
  "recipientPhone": "13900139000",
  "province": "山西省",
  "city": "长治市",
  "district": "潞州区",
  "detailAddress": "新地址",
  "isDefault": false
}
```

**响应:**

```json
{
  "success": true,
  "message": "地址更新成功",
  "data": {
    "id": 1,
    "userId": 1,
    "recipientName": "李四",
    "recipientPhone": "13900139000",
    "province": "山西省",
    "city": "长治市",
    "district": "潞州区",
    "detailAddress": "新地址",
    "isDefault": false,
    "updateTime": "2025-12-16T10:35:00"
  }
}
```

### 删除地址

```http
DELETE /api/addresses/{id}?userId={userId}
```

**响应:**

```json
{
  "success": true,
  "message": "地址删除成功",
  "data": null
}
```

**说明:** 删除默认地址时会自动将最早创建的地址设为新的默认地址。

### 设置默认地址

```http
PUT /api/addresses/{id}/default?userId={userId}
```

**响应:**

```json
{
  "success": true,
  "message": "默认地址设置成功",
  "data": {
    "id": 2,
    "userId": 1,
    "isDefault": true,
    "updateTime": "2025-12-16T10:40:00"
  }
}
```

### 查询用户地址列表

```http
GET /api/addresses/user/{userId}
```

**响应:**

```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {
      "id": 2,
      "userId": 1,
      "recipientName": "王五",
      "recipientPhone": "13700137000",
      "province": "山西省",
      "city": "长治市",
      "district": "潞州区",
      "detailAddress": "默认地址",
      "isDefault": true,
      "createTime": "2025-12-16T09:00:00"
    },
    {
      "id": 1,
      "userId": 1,
      "recipientName": "李四",
      "recipientPhone": "13900139000",
      "province": "山西省",
      "city": "长治市",
      "district": "潞州区",
      "detailAddress": "新地址",
      "isDefault": false,
      "createTime": "2025-12-15T10:00:00"
    }
  ]
}
```

**说明:** 返回按默认地址优先、创建时间倒序排列的地址列表。

### 查询默认地址

```http
GET /api/addresses/user/{userId}/default
```

**响应:**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "id": 2,
    "userId": 1,
    "recipientName": "王五",
    "recipientPhone": "13700137000",
    "province": "山西省",
    "city": "长治市",
    "district": "潞州区",
    "detailAddress": "默认地址",
    "isDefault": true
  }
}
```

### 查询地址详情

```http
GET /api/addresses/{id}
```

**响应:**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "id": 1,
    "userId": 1,
    "recipientName": "李四",
    "recipientPhone": "13900139000",
    "province": "山西省",
    "city": "长治市",
    "district": "潞州区",
    "detailAddress": "新地址",
    "isDefault": false,
    "createTime": "2025-12-15T10:00:00",
    "updateTime": "2025-12-16T10:35:00"
  }
}
```

---

## 6️⃣ 门票订单管理（通用订单系统）

### 创建门票订单

```http
POST /api/ticket-orders
Content-Type: application/json
```

**请求体:**

```json
{
  "userId": 1,
  "scenicSpotId": 1,
  "merchantId": 1,
  "visitDate": "2025-12-25",
  "contactName": "张三",
  "contactPhone": "13800138000",
  "tickets": [
    {
      "ticketPriceId": 1,
      "visitorName": "张三",
      "visitorIdCard": "140000199001011234",
      "visitorPhone": "13800138000",
      "price": 80.00,
      "productName": "长治太行山大峡谷成人票"
    },
    {
      "ticketPriceId": 2,
      "visitorName": "李四",
      "price": 40.00,
      "productName": "长治太行山大峡谷儿童票"
    }
  ],
  "remark": "请提前准备好身份证"
}
```

**响应:**

```json
{
  "success": true,
  "message": "订单创建成功",
  "data": {
    "orderNo": "ORDER1734323456001234",
    "userId": 1,
    "scenicSpotId": 1,
    "merchantId": 1,
    "visitDate": "2025-12-25",
    "contactName": "张三",
    "contactPhone": "13800138000",
    "totalAmount": 120.00,
    "discountAmount": 0.00,
    "payAmount": 120.00,
    "pointAmount": 0.00,
    "pointEarned": 0,
    "paymentMethod": null,
    "paymentStatus": 0,
    "paymentStatusText": "待支付",
    "orderStatus": 0,
    "orderStatusText": "待付款",
    "remark": "请提前准备好身份证",
    "createTime": "2025-12-16T10:45:00",
    "updateTime": "2025-12-16T10:45:00",
    "tickets": [
      {
        "id": 1,
        "productName": "长治太行山大峡谷成人票",
        "unitPrice": 80.00,
        "visitorName": "张三",
        "visitorIdCard": null,
        "visitorPhone": null,
        "verificationCode": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
        "verificationStatus": 0,
        "verificationStatusText": "未核销",
        "ticketDate": "2025-12-25"
      },
      {
        "id": 2,
        "productName": "长治太行山大峡谷儿童票",
        "unitPrice": 40.00,
        "visitorName": "李四",
        "verificationCode": "p6o5n4m3l2k1j0i9h8g7f6e5d4c3b2a1",
        "verificationStatus": 0,
        "verificationStatusText": "未核销",
        "ticketDate": "2025-12-25"
      }
    ]
  }
}
```

### 查询门票订单详情

```http
GET /api/ticket-orders/{orderNo}
```

**响应:**

```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "orderNo": "ORDER1734323456001234",
    "userId": 1,
    "merchantId": 1,
    "visitDate": "2025-12-25",
    "totalAmount": 120.00,
    "payAmount": 120.00,
    "paymentStatus": 0,
    "paymentStatusText": "待支付",
    "orderStatus": 0,
    "orderStatusText": "待付款",
    "tickets": [...]
  }
}
```

### 查询用户门票订单列表

```http
GET /api/ticket-orders/user/{userId}
```

**响应:**

```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {
      "orderNo": "ORDER1734323456001234",
      "visitDate": "2025-12-25",
      "totalAmount": 120.00,
      "orderStatus": 0,
      "orderStatusText": "待付款",
      "tickets": [...]
    },
    {
      "orderNo": "ORDER1734323456001235",
      "visitDate": "2025-12-20",
      "totalAmount": 80.00,
      "orderStatus": 1,
      "orderStatusText": "待使用",
      "tickets": [...]
    }
  ]
}
```

### 支付门票订单

```http
POST /api/ticket-orders/{orderNo}/pay?paymentMethod=wechat
```

**响应:**

```json
{
  "success": true,
  "message": "支付成功",
  "data": {
    "orderNo": "ORDER1734323456001234",
    "paymentMethod": "wechat",
    "paymentStatus": 1,
    "paymentStatusText": "支付成功",
    "orderStatus": 1,
    "orderStatusText": "待使用"
  }
}
```

### 取消门票订单

```http
POST /api/ticket-orders/{orderNo}/cancel
```

**响应:**

```json
{
  "success": true,
  "message": "订单已取消",
  "data": null
}
```

**说明:** 

- 订单状态(orderStatus): 0-待付款, 1-待使用, 2-已完成, 3-已取消, 4-退款中
- 支付状态(paymentStatus): 0-待支付, 1-支付成功, 2-支付失败, 3-已退款, 4-处理中
- 核销状态(verificationStatus): 0-未核销, 1-已核销, 2-已过期
- 订单号(orderNo): 自动生成，格式为 ORDER+时间戳+随机数

---

## 📝 数据库字段说明

**users 表字段:**

- `id` - 用户 ID
- `phone_encrypted` - 加密的手机号
- `password` - 加密的登录密码
- `nickname` - 昵称
- `avatar_url` - 头像 URL
- `real_name` - 真实姓名
- `id_card_encrypted` - 加密的身份证号
- `member_level` - 会员等级 (BRONZE/SILVER/GOLD/PLATINUM/DIAMOND)
- `growth_value` - 成长值
- `total_points` - 累计积分
- `available_points` - 可用积分
- `wallet_balance` - 钱包余额
- `payment_password` - 支付密码
- `register_time` - 注册时间
- `last_login_time` - 最后登录时间
