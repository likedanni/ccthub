// pages/login/login.js
Page({
  /**
   * 页面的初始数据
   */
  data: {
    isAgreed: false,
    isLoading: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('登录页面加载', options);
    // 可以在这里初始化一些数据
  },

  /**
   * 处理微信登录
   */
  handleWechatLogin() {
    if (!this.data.isAgreed) {
      wx.showToast({
        title: '请先阅读并同意协议',
        icon: 'none',
        duration: 2000
      });
      return;
    }

    if (this.data.isLoading) return;

    this.setData({ isLoading: true });

    // 显示加载状态
    wx.showLoading({
      title: '登录中...',
      mask: true
    });

    // 固定账号密码登录
    this.loginWithAccount();
  },

  /**
   * 使用固定账号密码调用登录接口
   */
  loginWithAccount() {
    const app = getApp();
    const base = app?.globalData?.API_BASE || 'http://localhost:8080';
    const url = `${base}/api/users/login`;
    const payload = {
      phone: '13900139000',
      password: '123456'
    };

    wx.request({
      url,
      method: 'POST',
      data: payload,
      header: {
        'Content-Type': 'application/json'
      },
      success: (res) => {
        const code = res?.data?.code ?? res.statusCode;
        const token = res?.data?.data?.token || res?.data?.token || res?.data?.access_token;
        const userId = res?.data?.data?.userId || res?.data?.userId || res?.data?.id;
        const message = res?.data?.message || res?.data?.msg || '登录失败，请重试';

        console.log('登录响应:', res);

        if (res.statusCode === 200 && token) {
          wx.setStorageSync('token', token);
          if (userId) {
            wx.setStorageSync('userId', userId);
          }
          this.handleLoginSuccess();
        } else {
          this.handleLoginError(message || `登录失败，code: ${code}`);
        }
      },
      fail: (err) => {
        this.handleLoginError(err.errMsg || '网络异常，请稍后重试');
      },
      complete: () => {
        wx.hideLoading();
        this.setData({ isLoading: false });
      }
    });
  },

  /**
   * 处理登录成功
   */
  handleLoginSuccess() {
    this.setData({ isLoading: false });
    wx.hideLoading();

    // 显示成功提示
    wx.showToast({
      title: '登录成功',
      icon: 'success',
      duration: 1500
    });

    // 跳转到首页
    setTimeout(() => {
      wx.switchTab({
        url: '/pages/home/index'
      });
    }, 1500);
  },

  /**
   * 处理登录错误
   */
  handleLoginError(error) {
    this.setData({ isLoading: false });
    wx.hideLoading();

    console.error('登录失败:', error);
    wx.showToast({
      title: typeof error === 'string' ? error : '登录失败，请重试',
      icon: 'error',
      duration: 2000
    });
  },

  /**
   * 处理其他登录方式
   */
  handleOtherLogin() {
    wx.showToast({
      title: '其他登录方式开发中',
      icon: 'none',
      duration: 1500
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
      isAgreed: isChecked
    });
  },

  /**
   * 点击协议文字区域切换勾选
   */
  toggleAgreement() {
    this.setData({
      isAgreed: !this.data.isAgreed
    });
  },

  /**
   * 跳转到服务协议
   */
  navigateToServiceAgreement() {
    wx.navigateTo({
      url: '/pages/webview/webview?url=https://your-domain.com/service-agreement'
    });
  },

  /**
   * 跳转到隐私政策
   */
  navigateToPrivacyPolicy() {
    wx.navigateTo({
      url: '/pages/webview/webview?url=https://your-domain.com/privacy-policy'
    });
  },

  /**
   * 协议区域点击跳转首页
   */
  goHome() {
    wx.switchTab({
      url: '/pages/home/index'
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
      title: '文旅App - 探索世界，发现美好',
      path: '/pages/login/login'
    };
  }
});
