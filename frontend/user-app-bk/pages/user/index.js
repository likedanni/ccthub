// pages/profile/profile.js
const app = getApp();

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
    growthProgress: 75,
    unreadTicketCount: 1,
    unreadProductCount: 2,
    isRefreshing: false,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.loadUserInfo();
    this.loadOrderStats();
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    // 每次显示页面时刷新用户信息
    this.refreshUserInfo();
  },

  /**
   * 加载用户信息
   */
  loadUserInfo() {
    // 从缓存获取用户信息
    const userInfo = app.getStorage("userInfo") || {};
    const points = app.getStorage("userPoints") || 1240;
    const balance = app.getStorage("userBalance") || 256.0;

    this.setData({
      userInfo: {
        name: userInfo.nickName || "Alex Chen",
        avatar: userInfo.avatarUrl || "/images/icons/default-avatar.png",
        points: points,
        balance: balance,
      },
    });
  },

  /**
   * 刷新用户信息
   */
  refreshUserInfo() {
    app.showLoading();

    setTimeout(() => {
      // 实际项目中这里调用API
      const newPoints = Math.floor(Math.random() * 200) + 1200;
      const newBalance = (Math.random() * 100 + 200).toFixed(2);

      this.setData({
        "userInfo.points": newPoints.toLocaleString(),
        "userInfo.balance": newBalance,
      });

      app.hideLoading();
    }, 800);
  },

  /**
   * 加载订单统计
   */
  loadOrderStats() {
    // 实际项目中从API获取
    this.setData({
      unreadTicketCount: 1,
      unreadProductCount: 2,
    });
  },

  /**
   * 编辑头像
   */
  editAvatar() {
    wx.showActionSheet({
      itemList: ["拍照", "从相册选择", "查看大图"],
      success: (res) => {
        const index = res.tapIndex;
        switch (index) {
          case 0:
            this.takePhoto();
            break;
          case 1:
            this.chooseImage();
            break;
          case 2:
            this.previewAvatar();
            break;
        }
      },
    });
  },

  /**
   * 拍照
   */
  async takePhoto() {
    try {
      const files = await app.chooseImage({ count: 1, sourceType: ["camera"] });
      this.uploadAvatar(files[0]);
    } catch (e) {
      console.error(e);
    }
  },

  /**
   * 从相册选择
   */
  async chooseImage() {
    try {
      const files = await app.chooseImage({ count: 1, sourceType: ["album"] });
      this.uploadAvatar(files[0]);
    } catch (e) {
      console.error(e);
    }
  },

  /**
   * 预览头像
   */
  previewAvatar() {
    if (this.data.userInfo.avatar) {
      app.previewImage(this.data.userInfo.avatar);
    }
  },

  /**
   * 上传头像
   */
  uploadAvatar(filePath) {
    app.showLoading("上传中...");

    // 模拟上传
    setTimeout(() => {
      this.setData({
        "userInfo.avatar": filePath,
      });

      // 保存到缓存
      const userInfo = app.getStorage("userInfo") || {};
      userInfo.avatarUrl = filePath;
      app.setStorage("userInfo", userInfo);

      app.hideLoading();
      app.showToast("头像更新成功", "success");
    }, 1500);
  },

  /**
   * 查看订单
   */
  viewOrders(e) {
    const type = e.currentTarget.dataset.type;
    const title = this.getOrderTypeTitle(type);
    app.navigateTo("/pages/orders/orders", { type, title });
  },

  /**
   * 查看全部订单
   */
  viewAllOrders() {
    app.navigateTo("/pages/orders/orders", { type: "all" });
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
    app.navigateTo("/pages/visitors/visitors");
  },

  /**
   * 导航到地址管理
   */
  navigateToLocation() {
    app.navigateTo("/pages/address/index");
  },

  /**
   * 导航到我的评价
   */
  navigateToReviews() {
    app.navigateTo("/pages/feedback/review");
  },

  /**
   * 导航到我的反馈
   */
  navigateToFeedback() {
    app.navigateTo("/pages/feedback/feedback");
  },

  /**
   * 联系客服
   */
  async contactCustomerService() {
    await app.showModal({
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
    app.navigateTo("/pages/help/help");
  },

  /**
   * 导航到设置
   */
  navigateToSettings() {
    app.navigateTo("/pages/settings/settings");
  },

  /**
   * 安全退出
   */
  async logout() {
    const confirm = await app.showModal({
      title: "确认退出",
      content: "确定要退出登录吗？",
    });

    if (confirm) {
      this.performLogout();
    }
  },

  /**
   * 执行退出操作
   */
  performLogout() {
    app.showLoading("退出中...");

    setTimeout(() => {
      // 使用app.js中的logout方法
      app.logout();
    }, 1000);
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    this.setData({ isRefreshing: true });

    // 刷新数据
    this.refreshUserInfo();
    this.loadOrderStats();

    setTimeout(() => {
      wx.stopPullDownRefresh();
      this.setData({ isRefreshing: false });
      app.showToast("刷新成功", "success");
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
});
