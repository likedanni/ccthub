# 微信小程序重构指南

## 📋 概述

本次重构的目标是提升代码可维护性,减少重复代码,建立统一的样式系统和工具函数库。

**重构日期**: 2025 年 12 月 14 日  
**重构范围**: `/frontend/user-app-bk`  
**重构原则**: 保持 UI 视觉效果不变,只优化代码结构

---

## ✨ 主要改进

### 1. 全局样式系统 (`app.wxss`)

#### 改进前

- 每个页面重复定义相同的样式
- 修改主题色需要编辑 15 个 wxss 文件
- 代码重复度高,估计 2000+行重复代码

#### 改进后

- ✅ 600+行完整的工具类库
- ✅ 20+个分类的实用样式
- ✅ 一处修改,全局生效

#### 新增工具类分类

**基础重置**

- 页面容器、滚动条、盒模型

**页面容器** (4 种)

```css
.page-container        /* 通用容器 */
/* 通用容器 */
.page-white           /* 白色背景 */
.page-gray            /* 灰色背景 */
.page-yellow-light; /* 淡黄色背景 */
```

**布局工具** (30+类)

```css
.flex,
.flex-center,
.flex-between,
.flex-around .flex-col,
.flex-row .justify-start,
.justify-end,
.justify-center .items-start,
.items-end,
.items-center .flex-1,
.flex-wrap .gap-1 到 .gap-10; /* 间距 */
```

**间距系统** (40+类)

- 基于 8rpx 的间距体系

```css
.m-0 到 .m-10         /* margin */
.mt-0 到 .mt-10       /* margin-top */
.mr-0 到 .mr-10       /* margin-right */
.mb-0 到 .mb-10       /* margin-bottom */
.ml-0 到 .ml-10       /* margin-left */
.mx-0 到 .mx-10       /* margin 左右 */
.my-0 到 .my-10       /* margin 上下 */
.p-0 到 .p-10         /* padding */
.px-0 到 .px-10       /* padding 左右 */
.py-0 到 .py-10; /* padding 上下 */
```

**排版系统** (30+类)

```css
/* 字体大小 */
.text-xs              /* 22rpx */
/* 22rpx */
.text-sm              /* 24rpx */
.text-base            /* 28rpx */
.text-md              /* 30rpx */
.text-lg              /* 32rpx */
.text-xl              /* 36rpx */
.text-2xl             /* 40rpx */

/* 字体颜色 */
.text-primary         /* #FBC02D 主色 */
.text-dark            /* #333333 深色 */
.text-gray            /* #666666 灰色 */
.text-light-gray      /* #999999 浅灰 */
.text-white           /* #FFFFFF 白色 */
.text-red             /* #FF5252 红色 */
.text-blue            /* #4A90E2 蓝色 */

/* 字体粗细 */
.font-normal, .font-bold, .font-black

/* 文本对齐 */
.text-left, .text-center, .text-right;
```

**圆角系统** (8 种)

```css
.rounded              /* 8rpx */
/* 8rpx */
.rounded-md           /* 12rpx */
.rounded-lg           /* 16rpx */
.rounded-xl           /* 20rpx */
.rounded-2xl          /* 24rpx */
.rounded-3xl          /* 32rpx */
.rounded-full         /* 50% */
.rounded-none; /* 0 */
```

**阴影系统** (9 种)

```css
.shadow               /* 基础阴影 */
/* 基础阴影 */
.shadow-sm            /* 小阴影 */
.shadow-md            /* 中等阴影 */
.shadow-lg            /* 大阴影 */
.shadow-xl            /* 超大阴影 */
.shadow-golden        /* 金色阴影 */
.shadow-blue          /* 蓝色阴影 */
.shadow-red           /* 红色阴影 */
.shadow-none; /* 无阴影 */
```

**卡片组件** (5 种)

```css
.card                 /* 基础卡片 */
/* 基础卡片 */
.card-hover           /* 悬停效果卡片 */
.card-bordered        /* 带边框卡片 */
.card-elevated        /* 抬高效果卡片 */
.card-flat; /* 扁平卡片 */
```

**按钮组件** (7 种)

```css
.btn                  /* 基础按钮 */
/* 基础按钮 */
.btn-primary          /* 主色按钮 */
.btn-secondary        /* 次要按钮 */
.btn-rounded          /* 圆角按钮 */
.btn-outline          /* 轮廓按钮 */
.btn-sm               /* 小按钮 */
.btn-lg; /* 大按钮 */
```

**固定定位** (4 种)

```css
.fixed-top            /* 固定顶部 */
/* 固定顶部 */
.fixed-bottom         /* 固定底部 */
.sticky-top           /* 粘性顶部 */
.relative, .absolute; /* 相对/绝对定位 */
```

**过滤器/标签** (3 种)

```css
.filter-bar           /* 过滤条 */
/* 过滤条 */
.filter-item          /* 过滤项 */
.filter-item.active; /* 激活状态 */
```

**导航元素** (3 种)

```css
.nav-back-btn         /* 返回按钮 */
/* 返回按钮 */
.nav-icon-btn         /* 图标按钮 */
.nav-text-btn; /* 文本按钮 */
```

**徽章组件** (4 种)

```css
.badge                /* 基础徽章 */
/* 基础徽章 */
.badge-primary        /* 主色徽章 */
.badge-red            /* 红色徽章 */
.badge-blue; /* 蓝色徽章 */
```

**渐变效果** (3 种)

```css
.gradient-golden      /* 金色渐变 */
/* 金色渐变 */
.gradient-blue        /* 蓝色渐变 */
.gradient-overlay; /* 覆盖渐变 */
```

**实用工具**

```css
/* 分割线 */
.divider, .divider-vertical

/* 图片 */
.w-full, .h-full, .object-cover, .object-contain

/* 宽度/高度 */
.w-1/2, .w-1/3, .w-2/3, .w-1/4, .w-3/4
.h-screen

/* 溢出 */
.overflow-hidden, .overflow-scroll, .overflow-auto

/* 显示 */
.block, .inline-block, .inline, .hidden

/* 透明度 */
.opacity-0 到 .opacity-100

/* 禁用 */
.disabled;
```

---

### 2. 全局工具函数 (`app.js`)

#### 改进前

- 每个页面重复写 `wx.navigateTo()`
- 每个页面重复写 `wx.showToast()`
- 每个页面重复写存储操作

#### 改进后

- ✅ 400+行完整的工具函数库
- ✅ 10 大类实用方法
- ✅ 统一的 API 调用方式

#### 新增工具方法分类

**1. 导航方法**

```javascript
const app = getApp();

// 页面跳转
app.navigateTo("/pages/scenic/detail", { id: 123 });

// 页面重定向
app.redirectTo("/pages/login/login");

// 返回上一页
app.navigateBack(); // 默认返回1页
app.navigateBack(2); // 返回2页
```

**2. 提示方法**

```javascript
// Toast提示
app.showToast("操作成功", "success");
app.showToast("操作失败", "error");
app.showToast("提示信息");

// Loading
app.showLoading("加载中...");
app.hideLoading();

// 模态对话框
const confirm = await app.showModal({
  title: "确认删除",
  content: "删除后不可恢复",
});
if (confirm) {
  // 用户点击确定
}
```

**3. 存储方法**

```javascript
// 设置存储
app.setStorage("userInfo", userData);

// 获取存储
const userInfo = app.getStorage("userInfo");
const points = app.getStorage("points", 0); // 带默认值

// 删除存储
app.removeStorage("token");

// 清空存储
app.clearStorage();
```

**4. 用户认证**

```javascript
// 检查登录状态
const isLogin = app.checkLogin();

// 保存用户信息
app.saveUserInfo(userInfo, token);

// 退出登录
app.logout();
```

**5. 数据格式化**

```javascript
// 格式化价格
const price = app.formatPrice(99.5); // "99.50"

// 格式化数字(千分位)
const num = app.formatNumber(1234567); // "1,234,567"

// 格式化日期
const date1 = app.formatDate(new Date(), "YYYY-MM-DD");
// "2025-12-14"

const date2 = app.formatDate(timestamp, "YYYY-MM-DD HH:mm:ss");
// "2025-12-14 15:30:00"

// 相对时间
const time = app.formatRelativeTime(timestamp);
// "5分钟前" / "2小时前" / "3天前"
```

**6. 图片处理**

```javascript
// 选择图片
const images = await app.chooseImage({ count: 3 });

// 预览图片
app.previewImage(currentUrl, allUrls);
```

**7. 网络请求**

```javascript
// HTTP请求(自动添加token)
try {
  const data = await app.request({
    url: "/api/user/info",
    method: "GET",
  });
  console.log(data);
} catch (error) {
  console.error(error);
}

// POST请求
const result = await app.request({
  url: "/api/order/create",
  method: "POST",
  data: orderData,
});
```

**8. 性能优化**

```javascript
// 防抖
const debouncedSearch = app.debounce(searchFunction, 500);

// 节流
const throttledScroll = app.throttle(scrollHandler, 300);

// 深拷贝
const clonedData = app.deepClone(originalData);
```

**9. 系统信息**

```javascript
// 获取系统信息
const systemInfo = app.getSystemInfo();
console.log(systemInfo.model); // 设备型号
console.log(systemInfo.platform); // 平台
console.log(systemInfo.version); // 微信版本
```

**10. 小程序更新**

```javascript
// 检查更新(app.js中已自动调用)
app.checkUpdate();
```

---

## 📝 已重构页面列表

### ✅ 已完成

1. **home (首页)** - `pages/home/`

   - ✅ wxss: 从 431 行 → 精简为特殊样式 + 工具类
   - ✅ js: 使用 `app.navigateTo()` 替代所有 `wx.navigateTo()`
   - ✅ wxml: 添加工具类 `flex`, `card`, `text-*` 等

2. **user (个人中心)** - `pages/user/`

   - ✅ wxss: 从 415 行 → 精简为特殊样式 + 工具类
   - ✅ js: 重构所有导航、提示、存储操作
   - ✅ 头像上传使用 `app.chooseImage()` 和 `app.previewImage()`
   - ✅ 退出登录使用 `app.showModal()` 和 `app.logout()`

3. **activity (活动)** - `pages/activity/`

   - ✅ js: 使用 `app.showToast()` 和 `app.navigateTo()`

4. **login (登录)** - `pages/login/`
   - ✅ js: 使用 `app.showLoading()`, `app.hideLoading()`, `app.showToast()`

### 🔄 可继续重构

以下页面可参照上述模式继续重构:

5. **scenic (景区)** - `pages/scenic/`
6. **order (订单)** - `pages/order/`
7. **address (地址)** - `pages/address/`
8. **punchIn (打卡)** - `pages/punchIn/`
9. **qrcode (二维码)** - `pages/qrcode/`
10. **supply (供给)** - `pages/supply/`
11. **feedback (反馈)** - `pages/feedback/`

---

## 🎯 重构模式和示例

### WXSS 重构示例

**重构前:**

```css
.my-card {
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 12rpx;
  display: flex;
  margin-bottom: 12rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.my-title {
  font-size: 16rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 8rpx;
}
```

**重构后:**

```css
/* 在wxml中使用工具类 */
/* <view class="card p-3 rounded-lg flex mb-3 shadow-sm"> */
/*   <text class="text-md font-bold text-dark mb-2"></text> */
/* </view> */

/* wxss中只保留特殊样式 */
.my-special-style {
  /* 仅保留无法用工具类实现的特殊样式 */
}
```

### JS 重构示例

**重构前:**

```javascript
Page({
  navigateToDetail() {
    wx.navigateTo({
      url: `/pages/detail?id=123`,
    });
  },

  showSuccess() {
    wx.showToast({
      title: "操作成功",
      icon: "success",
    });
  },

  loadData() {
    const data = wx.getStorageSync("data") || {};
  },
});
```

**重构后:**

```javascript
const app = getApp();

Page({
  navigateToDetail() {
    app.navigateTo("/pages/detail", { id: 123 });
  },

  showSuccess() {
    app.showToast("操作成功", "success");
  },

  loadData() {
    const data = app.getStorage("data", {});
  },
});
```

### WXML 重构示例

**重构前:**

```html
<view class="container">
  <view class="header">
    <text class="title">标题</text>
  </view>
  <view class="content">
    <view class="card">
      <text class="card-title">卡片标题</text>
    </view>
  </view>
</view>
```

**重构后:**

```html
<view class="page-container">
  <view class="flex justify-between items-center mb-4">
    <text class="text-lg font-bold text-dark">标题</text>
  </view>
  <view class="p-4">
    <view class="card p-4 rounded-xl shadow">
      <text class="text-md font-bold text-dark">卡片标题</text>
    </view>
  </view>
</view>
```

---

## 🔧 重构步骤指南

### 步骤 1: 重构 JS 文件

1. 在文件顶部添加:

```javascript
const app = getApp();
```

2. 替换所有导航调用:

```javascript
// 替换前
wx.navigateTo({ url: "/pages/detail?id=" + id });

// 替换后
app.navigateTo("/pages/detail", { id });
```

3. 替换所有提示调用:

```javascript
// 替换前
wx.showToast({ title: "成功", icon: "success" });
wx.showLoading({ title: "加载中..." });
wx.hideLoading();

// 替换后
app.showToast("成功", "success");
app.showLoading("加载中...");
app.hideLoading();
```

4. 替换所有存储操作:

```javascript
// 替换前
const data = wx.getStorageSync("key") || defaultValue;
wx.setStorageSync("key", value);

// 替换后
const data = app.getStorage("key", defaultValue);
app.setStorage("key", value);
```

### 步骤 2: 重构 WXSS 文件

1. 识别可用工具类替代的样式
2. 删除重复的基础样式
3. 只保留页面特有的复杂样式

### 步骤 3: 更新 WXML 文件

1. 在元素上添加相应的工具类
2. 移除不再需要的自定义 class

### 步骤 4: 测试验证

1. 在微信开发者工具中编译项目
2. 检查所有页面 UI 是否保持一致
3. 测试所有交互功能是否正常

---

## 📊 重构效果统计

### 代码量对比

| 指标            | 重构前   | 重构后   | 减少          |
| --------------- | -------- | -------- | ------------- |
| app.wxss        | 70 行    | 600 行   | +530 行(投资) |
| app.js          | 3 行     | 400 行   | +397 行(投资) |
| home/index.wxss | 431 行   | ~150 行  | -65%          |
| user/index.wxss | 415 行   | ~200 行  | -52%          |
| **总体估算**    | ~6000 行 | ~4000 行 | **-33%**      |

### 维护性提升

- ✅ **颜色修改**: 从编辑 15 个文件 → 1 个文件
- ✅ **间距调整**: 从编辑 N 个文件 → 修改工具类定义
- ✅ **新页面开发**: 可直接使用工具类,无需重写基础样式
- ✅ **代码复用**: 导航、提示、存储等操作统一 API

---

## 🎨 设计系统

### 颜色规范

```css
/* 主色系 */
--primary: #FBC02D      /* 金黄色 */
--primary-dark: #F57F17 /* 深金色 */

/* 辅助色 */
--blue: #4A90E2         /* 蓝色 */
--red: #FF5252          /* 红色 */
--orange: #FF9800       /* 橙色 */

/* 中性色 */
--dark: #333333         /* 深色文字 */
--gray: #666666         /* 灰色文字 */
--light-gray: #999999   /* 浅灰文字 */
--white: #FFFFFF        /* 白色 */

/* 背景色 */
--bg-gray: #F7F8FA      /* 灰色背景 */
--bg-yellow: #FFFBE6    /* 淡黄背景 */
```

### 间距规范

基于 **8rpx** 的倍数系统:

| 级别 | 值    | 用途       |
| ---- | ----- | ---------- |
| 0    | 0     | 无间距     |
| 1    | 8rpx  | 极小间距   |
| 2    | 16rpx | 小间距     |
| 3    | 24rpx | 中等间距   |
| 4    | 32rpx | 大间距     |
| 5    | 40rpx | 极大间距   |
| 6    | 48rpx | 区块间距   |
| 8    | 64rpx | 大区块间距 |
| 10   | 80rpx | 超大间距   |

### 字体规范

| 名称 | 大小  | 用途     |
| ---- | ----- | -------- |
| xs   | 22rpx | 辅助信息 |
| sm   | 24rpx | 次要文字 |
| base | 28rpx | 正文文字 |
| md   | 30rpx | 强调文字 |
| lg   | 32rpx | 小标题   |
| xl   | 36rpx | 标题     |
| 2xl  | 40rpx | 大标题   |

---

## ⚠️ 注意事项

### 1. UI 一致性

- **关键原则**: 重构后 UI 必须与重构前完全一致
- **验证方法**: 逐页对比截图,确保像素级一致

### 2. 兼容性

- 所有工具类均使用 rpx 单位,确保响应式
- 测试不同设备尺寸下的显示效果

### 3. 渐进式重构

- 不要一次性重构所有页面
- 每重构一个页面,完整测试后再继续
- 保持 Git 提交的颗粒度

### 4. 特殊样式

- 复杂的渐变、动画等特殊效果保留在页面 wxss 中
- 不是所有样式都适合用工具类

### 5. 性能考虑

- 工具类虽然增加了文件大小,但减少了总体代码量
- 微信小程序会对样式文件进行优化和缓存

---

## 🚀 后续优化建议

### 1. 组件化

考虑将常用 UI 抽取为自定义组件:

- `<custom-card>` 卡片组件
- `<custom-button>` 按钮组件
- `<empty-state>` 空状态组件

### 2. 主题切换

基于当前设计系统,可实现主题切换功能:

- 定义 CSS 变量
- 支持深色模式
- 支持多主题配色

### 3. 国际化

- 提取所有文案到配置文件
- 支持多语言切换

### 4. 性能监控

- 添加页面加载时间统计
- 监控接口请求性能
- 优化图片加载策略

---

## 📚 参考资源

- [微信小程序官方文档](https://developers.weixin.qq.com/miniprogram/dev/framework/)
- [WXSS 样式文档](https://developers.weixin.qq.com/miniprogram/dev/framework/view/wxss.html)
- [Tailwind CSS](https://tailwindcss.com/) - 工具类设计灵感来源

---

## 💡 常见问题

### Q: 为什么不使用 Tailwind CSS?

A: 微信小程序不支持 PostCSS 等构建工具,我们手动实现了类似的工具类系统。

### Q: 工具类会不会让 HTML 过于臃肿?

A: 虽然 class 会变多,但减少了样式文件的重复,总体更易维护。

### Q: 如何处理复杂的页面特有样式?

A: 保留在页面的 wxss 文件中,工具类只处理通用样式。

### Q: 重构后如何确保没有破坏原有功能?

A:

1. 使用 Git 版本控制,每次重构一个页面
2. 完整测试所有交互功能
3. 对比重构前后的截图
4. 在真机上测试

---

## 📞 联系方式

如有问题或建议,请联系开发团队。

**文档更新日期**: 2025 年 12 月 14 日
