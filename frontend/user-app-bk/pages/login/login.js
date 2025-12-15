// pages/login/login.js
const app = getApp();

Page({
  /**
   * 页面的初始数据
   */
  data: {
    isAgreed: false,
    isLoading: false,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log("登录页面加载", options);
  },

  /**
   * 处理微信登录
   */
  handleWechatLogin() {
    if (!this.data.isAgreed) {
      app.showToast("请先阅读并同意协议");
      return;
    }

    if (this.data.isLoading) return;

    this.setData({ isLoading: true });
    app.showLoading("登录中...");

    // 模拟微信登录逻辑
    setTimeout(() => {
      // 实际项目中这里调用微信登录API
      // wx.login({
      //   success: (res) => {
      //     // 获取code后发送到后端
      //     this.loginWithCode(res.code);
      //   },
      //   fail: (err) => {
      //     this.handleLoginError(err);
      //   }
      // });

      // 模拟登录成功
      this.handleLoginSuccess();
    }, 1000);
  },

  /**
   * 处理登录成功
   */
  handleLoginSuccess() {
    this.setData({ isLoading: false });
    app.hideLoading();
    app.showToast("登录成功", "success");

    // 跳转到首页
    setTimeout(() => {
      wx.switchTab({
        url: "/pages/home/index",
      });
    }, 1500);
  },

  /**
   * 处理登录错误
   */
  handleLoginError(error) {
    this.setData({ isLoading: false });
    app.hideLoading();
    console.error("登录失败:", error);
    app.showToast("登录失败，请重试", "error");
  },

  /**
   * 处理其他登录方式
   */
  handleOtherLogin() {
    wx.showToast({
      title: "其他登录方式开发中",
      icon: "none",
      duration: 1500,
    });
  },

  /**
   * 处理协议勾选变化
   */
  handleAgreementChange(e) {
    const isChecked = Array.isArray(e.detail.value)
      ? e.detail.value.length > 0
      : !!e.detail.value;
    this.setData({
      isAgreed: isChecked,
    });
  },

  /**
   * 点击协议文字区域切换勾选
   */
  toggleAgreement() {
    this.setData({
      isAgreed: !this.data.isAgreed,
    });
  },

  /**
   * 跳转到服务协议
   */
  navigateToServiceAgreement() {
    wx.navigateTo({
      url: "/pages/webview/webview?url=https://your-domain.com/service-agreement",
    });
  },

  /**
   * 跳转到隐私政策
   */
  navigateToPrivacyPolicy() {
    wx.navigateTo({
      url: "/pages/webview/webview?url=https://your-domain.com/privacy-policy",
    });
  },

  /**
   * 协议区域点击跳转首页
   */
  goHome() {
    wx.switchTab({
      url: "/pages/home/index",
    });
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    // 页面显示时的逻辑
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    // 页面隐藏时的逻辑
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    // 页面卸载时的逻辑
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    // 下拉刷新逻辑
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
    // 上拉加载更多逻辑
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: "文旅App - 探索世界，发现美好",
      path: "/pages/login/login",
    };
  },
});
