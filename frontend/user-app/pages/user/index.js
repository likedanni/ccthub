// pages/profile/profile.js
Page({
  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {
      name: "",
      avatar: "",
      points: 0,
      balance: 0,
    },
    originalName: "",
    memberLabel: "会员",
    growthProgress: 75,
    unreadTicketCount: 1,
    unreadProductCount: 2,
    isRefreshing: false,
    growthTip: "还需 1000 成长值升级",
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.fetchUserProfile();
    this.loadOrderStats();
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    this.fetchUserProfile();
  },

  /**
   * 拉取用户信息
   */
  fetchUserProfile() {
    const app = getApp();
    const base = app?.globalData?.API_BASE || "http://localhost:8080";
    const token = wx.getStorageSync("token");
    const userId = wx.getStorageSync("userId");

    if (!token || !userId) {
      wx.showModal({
        title: "请先登录",
        content: "需要登录后查看个人信息",
        showCancel: false,
        confirmText: "去登录",
        success: () => {
          wx.reLaunch({ url: "/pages/login/login" });
        },
      });
      return;
    }

    wx.request({
      url: `${base}/api/users/${userId}/profile`,
      method: "GET",
      header: {
        Authorization: `Bearer ${token}`,
      },
      success: (res) => {
        console.log("用户资料响应:", res.data);
        if (res.statusCode === 200 && res.data && res.data.data) {
          const profile = res.data.data;
          const growthValue = Number(profile.growthValue || 0);
          const remaining = Math.max(0, 1000 - growthValue);
          const progress = Math.min(
            100,
            Math.max(0, (growthValue / 1000) * 100)
          );
          const balance = this.formatMoney(
            profile.walletBalance ?? profile.balance ?? 0
          );
          const points = profile.availablePoints ?? profile.totalPoints ?? 0;
          const name =
            profile.nickname || profile.realName || profile.phone || "用户";
          const memberLabel = this.mapMemberLevel(profile.memberLevel);

          this.setData({
            userInfo: {
              name,
              avatar: profile.avatarUrl || "/images/icons/default-avatar.png",
              points,
              balance,
            },
            originalName: name,
            memberLabel,
            growthProgress: progress,
            growthTip:
              remaining === 0 ? "成长值已满级" : `还需 ${remaining} 成长值升级`,
          });
        } else {
          wx.showToast({
            title: res.data?.message || "获取用户信息失败",
            icon: "none",
          });
        }
      },
      fail: () => {
        wx.showToast({
          title: "网络异常，请稍后重试",
          icon: "none",
        });
      },
    });
  },

  /**
   * 会员等级映射
   */
  mapMemberLevel(level) {
    if (level === null || level === undefined) return "会员";
    const map = {
      1: "普通会员",
      2: "白银会员",
      3: "黄金会员",
      4: "钻石会员",
    };
    const key = Number.isFinite(Number(level)) ? Number(level) : level;
    return map[key] || "会员";
  },

  /**
   * 加载订单统计
   */
  loadOrderStats() {
    // 实际项目中从API获取
    // 这里使用模拟数据
    this.setData({
      unreadTicketCount: 1,
      unreadProductCount: 2,
    });
  },

  /**
   * 编辑头像
   */
  editAvatar() {
    const token = wx.getStorageSync("token");
    const userId = wx.getStorageSync("userId");
    const app = getApp();
    const base = app?.globalData?.API_BASE || "http://localhost:8080";
    if (!token || !userId) {
      wx.showToast({ title: "请先登录", icon: "none" });
      return;
    }

    wx.chooseImage({
      count: 1,
      sizeType: ["compressed"],
      sourceType: ["album", "camera"],
      success: (res) => {
        const filePath = res.tempFilePaths[0];
        wx.showLoading({ title: "上传中..." });

        wx.uploadFile({
          url: `${base}/api/files/upload`,
          filePath,
          name: "file",
          formData: { category: "avatars" },
          header: {
            Authorization: `Bearer ${token}`,
          },
          success: (uploadRes) => {
            let avatarUrl = "";
            let message = "上传失败";
            try {
              const data = JSON.parse(uploadRes.data);
              console.log("头像上传响应:", data);
              // 兼容多种返回：
              // 1) { code:200, data:{url: '...'} }
              // 2) { code:200, data: 'http://...' }
              // 3) { url: '...' }
              if (data) {
                if (
                  data.data &&
                  typeof data.data === "object" &&
                  data.data.url
                ) {
                  avatarUrl = data.data.url;
                } else if (data.data && typeof data.data === "string") {
                  avatarUrl = data.data;
                } else if (data.url) {
                  avatarUrl = data.url;
                }
                message = data?.message || data?.msg || message;
              }
            } catch (e) {
              console.error("解析上传响应失败", e);
            }

            if (uploadRes.statusCode === 200 && avatarUrl) {
              // 更新用户资料中的头像
              wx.request({
                url: `${base}/api/users/${userId}/profile`,
                method: "PUT",
                header: {
                  "Content-Type": "application/json",
                  Authorization: `Bearer ${token}`,
                },
                data: { avatarUrl },
                success: () => {
                  this.setData({ "userInfo.avatar": avatarUrl });
                  wx.showToast({ title: "头像已更新", icon: "success" });
                },
                fail: () => {
                  wx.showToast({ title: "头像更新失败", icon: "none" });
                },
                complete: () => wx.hideLoading(),
              });
            } else {
              wx.hideLoading();
              wx.showToast({ title: message || "上传失败", icon: "none" });
            }
          },
          fail: () => {
            wx.hideLoading();
            wx.showToast({ title: "上传失败", icon: "none" });
          },
        });
      },
    });
  },

  /**
   * 查看订单
   */
  viewOrders(e) {
    const type = e.currentTarget.dataset.type;
    const title = this.getOrderTypeTitle(type);

    wx.navigateTo({
      url: `/pages/orders/orders?type=${type}&title=${title}`,
    });
  },

  /**
   * 查看全部订单
   */
  viewAllOrders() {
    wx.navigateTo({
      url: "/pages/orders/orders?type=all",
    });
  },

  /**
   * 获取订单类型标题
   */
  getOrderTypeTitle(type) {
    const titles = {
      all: "全部订单",
      ticket: "票务订单",
      product: "商品订单",
      activity: "活动订单",
    };
    return titles[type] || "我的订单";
  },

  /**
   * 导航到常用游客
   */
  navigateToVisitors() {
    wx.navigateTo({
      url: "/pages/visitors/visitors",
    });
  },

  /**
   * 导航到地址管理
   */
  navigateToLocation() {
    wx.navigateTo({
      url: "/pages/address/index",
    });
  },

  /**
   * 导航到我的评价
   */
  navigateToReviews() {
    wx.navigateTo({
      url: "/pages/feedback/review",
    });
  },

  /**
   * 导航到我的反馈
   */
  navigateToFeedback() {
    wx.navigateTo({
      url: "/pages/feedback/feedback",
    });
  },

  /**
   * 联系客服
   */
  contactCustomerService() {
    wx.showModal({
      title: "联系客服",
      content: "客服电话：400-123-4567\n工作时间：9:00-18:00",
      showCancel: false,
      confirmText: "知道了",
    });
  },

  /**
   * 导航到帮助中心
   */
  navigateToHelp() {
    wx.navigateTo({
      url: "/pages/help/help",
    });
  },

  /**
   * 导航到设置
   */
  navigateToSettings() {
    wx.navigateTo({
      url: "/pages/settings/settings",
    });
  },

  /**
   * 安全退出
   */
  logout() {
    wx.showModal({
      title: "确认退出",
      content: "确定要退出登录吗？",
      success: (res) => {
        if (res.confirm) {
          this.performLogout();
        }
      },
    });
  },

  /**
   * 执行退出操作
   */
  performLogout() {
    wx.showLoading({
      title: "退出中...",
    });

    // 模拟退出过程
    setTimeout(() => {
      // 清除用户数据
      wx.removeStorageSync("userInfo");
      wx.removeStorageSync("token");
      wx.removeStorageSync("userPoints");
      wx.removeStorageSync("userBalance");

      wx.hideLoading();

      // 跳转到登录页
      wx.reLaunch({
        url: "/pages/login/login",
      });
    }, 1000);
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    this.setData({ isRefreshing: true });

    // 刷新数据
    this.fetchUserProfile();
    this.loadOrderStats();

    setTimeout(() => {
      wx.stopPullDownRefresh();
      this.setData({ isRefreshing: false });
      wx.showToast({
        title: "刷新成功",
        icon: "success",
        duration: 1500,
      });
    }, 1500);
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: "我的个人中心 - 文旅平台",
      path: "/pages/profile/profile",
    };
  },

  /**
   * 金额格式化
   */
  formatMoney(value) {
    const num = Number(value || 0);
    if (Number.isNaN(num)) return "0.00";
    const cents = Math.round(num * 100);
    return (cents / 100).toFixed(2);
  },

  /**
   * 姓名输入时同步本地
   */
  onNameInput(e) {
    console.log("输入昵称:", e.detail.value);
    this.setData({
      "userInfo.name": e.detail.value,
    });
  },

  /**
   * 失焦时更新昵称
   */
  onNameBlur(e) {
    console.log("昵称失焦:", e.detail.value);
    const nickname = (e.detail.value || "").trim();
    const current = (this.data.originalName || "").trim();
    if (!nickname) {
      wx.showToast({ title: "昵称不能为空", icon: "none" });
      return;
    }
    if (nickname === current) return;

    const token = wx.getStorageSync("token");
    const userId = wx.getStorageSync("userId");
    if (!token || !userId) {
      wx.showToast({ title: "请先登录", icon: "none" });
      return;
    }

    const app = getApp();
    const base = app?.globalData?.API_BASE || "http://localhost:8080";

    wx.request({
      url: `${base}/api/users/${userId}/profile`,
      method: "PUT",
      header: {
        "Content-Type": "application/json;charset=utf-8",
        Accept: "application/json",
        Authorization: `Bearer ${token}`,
      },
      data: { nickname },
      success: (res) => {
        console.log("更新昵称响应 success:", res);
        const okCode =
          res?.data?.code === 200 ||
          res?.data?.status === 200 ||
          res?.data?.success === true;
        if (
          res.statusCode === 200 &&
          (okCode || res?.data?.code === undefined)
        ) {
          this.setData({ "userInfo.name": nickname, originalName: nickname });
          wx.showToast({ title: "昵称已更新", icon: "success" });
        } else {
          const msg =
            res?.data?.message ||
            res?.data?.error ||
            `更新失败(${res.statusCode})`;
          console.warn("更新昵称非200返回:", res.data);
          wx.showToast({ title: msg, icon: "none" });
        }
      },
      fail: (err) => {
        console.error("更新昵称失败:", err);
        wx.showToast({ title: "网络异常，稍后再试", icon: "none" });
      },
      complete: (res) => {
        console.log("更新昵称 complete:", res);
      },
    });
  },

  /**
   * 键盘确认时也提交
   */
  onNameConfirm(e) {
    this.onNameBlur(e);
  },
});
