// index.js
Page({
  data: {
    // 活动数据
    activity: {
      title: '2024夏日City Walk：探索艺术街区',
      organizer: {
        name: 'CityVibe 官方',
        verified: true
      },
      date: '2024年8月24日',
      time: '周六 14:00 - 18:00',
      location: '中央艺术公园',
      locationDetail: '北门入口，距地铁站500米',
      participants: {
        current: 203,
        total: 300,
        more: 199
      }
    },
    
    // 折叠面板状态
    rulesOpen: true,
    rewardsOpen: false,
    faqOpen: false
  },

  onLoad(options) {
    console.log('活动详情页面加载');
    if (options.id) {
      this.loadActivityData(options.id);
    }
  },

  onReady() {
    console.log('页面渲染完成');
  },

  onShow() {
    console.log('页面显示');
  },

  loadActivityData(id) {
    setTimeout(() => {
      console.log('加载活动数据:', id);
    }, 300);
  },

  goBack() {
    console.log('返回上一页');
    wx.navigateBack({
      delta: 1,
      fail: () => {
        wx.switchTab({
          url: '/pages/index/index'
        });
      }
    });
  },

  shareActivity() {
    console.log('分享活动');
    wx.showShareMenu({
      withShareTicket: true,
      menus: ['shareAppMessage', 'shareTimeline']
    });
  },

  toggleRules() {
    this.setData({
      rulesOpen: !this.data.rulesOpen
    });
  },

  toggleRewards() {
    this.setData({
      rewardsOpen: !this.data.rewardsOpen
    });
  },

  toggleFaq() {
    this.setData({
      faqOpen: !this.data.faqOpen
    });
  },

  contactService() {
    console.log('咨询客服');
    wx.showModal({
      title: '联系客服',
      content: '拨打电话：400-123-4567',
      showCancel: true,
      cancelText: '取消',
      confirmText: '拨打',
      success: (res) => {
        if (res.confirm) {
          wx.makePhoneCall({
            phoneNumber: '4001234567'
          });
        }
      }
    });
  },

  joinActivity() {
    wx.navigateTo({
      url: '/pages/punchIn/index'
    });
  },

  viewAllParticipants() {
    console.log('查看全部参与者');
    wx.navigateTo({
      url: '/pages/participants/participants?activityId=123'
    });
  },

  viewLocation() {
    console.log('查看位置详情');
    const location = {
      latitude: 39.9042,
      longitude: 116.4074,
      name: '中央艺术公园',
      address: '北京市中央艺术公园北门'
    };
    
    wx.openLocation({
      ...location,
      scale: 15
    });
  },

  onShareAppMessage() {
    return {
      title: this.data.activity.title,
      path: `/pages/activity-detail/activity-detail?id=123`,
      imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBe-hkI1m40gXvEyL5oelWHoo2ZBjTJf3VEwoAK-38ME8Gjq9IYXXyaNdz97_Ety_HqqYnv0Y0ybJu-dngPx0faBdUiZq1zfUI7dtSnSywwQ1Kr09maLWddVVarRRLCw71QH_rpnFYg_mfq_qVqI5qzkn1VXSMfk8EEyq8NX6stHwCuDHeiF07vfomjHCUasS_MqhHKYHLOmrmBBQkwNGNgTY09oPztb5emMjXyAoiIvUnLoZ7smw_RCO9jBXGzCz_Tu3jyjmNSkerC'
    };
  },

  onShareTimeline() {
    return {
      title: this.data.activity.title,
      query: 'id=123'
    };
  }
});
