// pages/qrcode/qrcode.js
Page({
  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {
      name: '',
      id: '',
      avatar: ''
    },
    qrcodeUrl: '',
    expiryTime: '10:00',
    showRefreshTip: true,
    refreshTimer: null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('二维码页面加载', options);
    this.loadUserInfo();
    this.generateQrcode();
    this.startRefreshTimer();
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    // 页面显示时恢复刷新计时器
    if (!this.data.refreshTimer) {
      this.startRefreshTimer();
    }
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    // 页面隐藏时停止计时器
    this.stopRefreshTimer();
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    // 页面卸载时清理计时器
    this.stopRefreshTimer();
  },

  /**
   * 加载用户信息
   */
  loadUserInfo() {
    // 实际项目中从缓存或API获取用户信息
    const userInfo = wx.getStorageSync('userInfo') || {};
    this.setData({
      userInfo: {
        name: userInfo.nickName || '文旅用户',
        id: userInfo.userId || 'WLY' + Date.now().toString().slice(-6),
        avatar: userInfo.avatarUrl || '/images/icons/default-avatar.png'
      }
    });
  },

  /**
   * 生成二维码
   */
  generateQrcode() {
    // 实际项目中从服务器获取二维码
    const userId = this.data.userInfo.id;
    const timestamp = Date.now();
    
    // 模拟生成二维码URL（实际项目中应该是后端API返回）
    const qrcodeUrl = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=wltrip://user/login?userId=${userId}&t=${timestamp}`;
    
    this.setData({
      qrcodeUrl: qrcodeUrl,
      expiryTime: this.calculateExpiryTime()
    });
  },

  /**
   * 计算有效期时间
   */
  calculateExpiryTime() {
    const now = new Date();
    const expiry = new Date(now.getTime() + 10 * 60 * 1000); // 10分钟后过期
    return expiry.getHours().toString().padStart(2, '0') + ':' + 
           expiry.getMinutes().toString().padStart(2, '0');
  },

  /**
   * 开始刷新计时器
   */
  startRefreshTimer() {
    // 每分钟刷新一次二维码
    const timer = setInterval(() => {
      this.refreshQrcode();
    }, 60 * 1000);
    
    this.setData({
      refreshTimer: timer
    });
  },

  /**
   * 停止刷新计时器
   */
  stopRefreshTimer() {
    if (this.data.refreshTimer) {
      clearInterval(this.data.refreshTimer);
      this.setData({
        refreshTimer: null
      });
    }
  },

  /**
   * 刷新二维码
   */
  refreshQrcode() {
    wx.showLoading({
      title: '刷新中...',
    });
    
    // 模拟网络请求
    setTimeout(() => {
      this.generateQrcode();
      wx.hideLoading();
      
      wx.showToast({
        title: '二维码已刷新',
        icon: 'success',
        duration: 1500
      });
    }, 500);
  },

  /**
   * 预览二维码大图
   */
  previewQrcode() {
    if (!this.data.qrcodeUrl) return;
    
    wx.previewImage({
      urls: [this.data.qrcodeUrl],
      current: this.data.qrcodeUrl
    });
  },

  /**
   * 保存二维码到相册
   */
  saveQrcode() {
    wx.showLoading({
      title: '保存中...',
    });
    
    // 实际项目中需要下载图片再保存
    wx.downloadFile({
      url: this.data.qrcodeUrl,
      success: (res) => {
        wx.saveImageToPhotosAlbum({
          filePath: res.tempFilePath,
          success: () => {
            wx.hideLoading();
            wx.showToast({
              title: '保存成功',
              icon: 'success',
              duration: 1500
            });
          },
          fail: (err) => {
            wx.hideLoading();
            console.error('保存失败:', err);
            wx.showToast({
              title: '保存失败',
              icon: 'error',
              duration: 2000
            });
          }
        });
      },
      fail: (err) => {
        wx.hideLoading();
        console.error('下载失败:', err);
        wx.showToast({
          title: '下载失败',
          icon: 'error',
          duration: 2000
        });
      }
    });
  },

  /**
   * 分享二维码
   */
  shareQrcode() {
    wx.showActionSheet({
      itemList: ['分享给好友', '分享到朋友圈', '生成分享图'],
      success: (res) => {
        const index = res.tapIndex;
        switch(index) {
          case 0:
            this.shareToFriend();
            break;
          case 1:
            this.shareToTimeline();
            break;
          case 2:
            this.generateShareImage();
            break;
        }
      }
    });
  },

  /**
   * 分享给好友
   */
  shareToFriend() {
    wx.showToast({
      title: '请在聊天中选择发送',
      icon: 'none',
      duration: 2000
    });
  },

  /**
   * 分享到朋友圈
   */
  shareToTimeline() {
    wx.showToast({
      title: '朋友圈分享功能',
      icon: 'none',
      duration: 1500
    });
  },

  /**
   * 生成分享图片
   */
  generateShareImage() {
    wx.showToast({
      title: '生成分享图片',
      icon: 'none',
      duration: 1500
    });
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: '我的文旅平台二维码',
      path: '/pages/qrcode/qrcode',
      imageUrl: this.data.qrcodeUrl
    };
  }
});