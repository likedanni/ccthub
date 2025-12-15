// pages/scenic/detail/index.js

Page({
  data: {
    // 滚动区域高度
    scrollHeight: 0,
    
    // 当前图片索引
    currentImageIndex: 0,
    currentImage: '',
    
    // 是否收藏
    isFavorite: false,
    
    // 票型数据
    activeTicketTab: 'single',
    
    // 景区数据
    scenicData: {
      id: 1,
      name: '蓝月谷',
      level: '5A级景区',
      category: '自然公园',
      mustVisit: '必打卡',
      rating: 4.9,
      reviewCount: '2400+',
      description: '"玉龙雪山脚下的璀璨明珠，湖水湛蓝，倒映雪山，是周末亲近自然、放松身心的绝佳去处。"',
      openHours: '08:00 - 18:00',
      address: '玉龙雪山景区内',
      distance: '距您 2.3km',
      phone: '0888-8888',
      highlight: '蓝月谷位于玉龙雪山脚下，其前身为早前人们所熟知的"白水河"。在晴天时，水的颜色是蓝色的，而且山谷呈月牙形，远看就像一轮蓝色的月亮镶嵌在玉龙雪山脚下。',
      bestSeason: '春秋两季',
      suggestedTime: '2-3 小时',
      mapImage: '/images/temple-banner.png'
    },
    
    // 图片列表
    imageList: [
      'https://lh3.googleusercontent.com/aida-public/AB6AXuCCN3jXXdzruLfVFVCp4KnnFTaqEU2_Lp66Xn660DIwRwaDkfPyzrLdg8192ruQR61L3OuulkwBnQTjYifvguDV-fGilw4ZvTYcTRSLVEpmPWKAZuyYm_BDH_iVOmTU1iYyFPXmrL1qa-KHNu3AWrp83-iL1yHwdvFfHSMpfVvU2SUpk8TWZzU_-QHo3zDl_GYh9VU7TmowA9nE75eFDyrDXXF3j-juqW0bxlH7IGRknmaHugnUJgGumjXtX42C8Dt9-CRVx578whIO',
      'https://example.com/image2.jpg',
      'https://example.com/image3.jpg',
      'https://example.com/image4.jpg',
      'https://example.com/image5.jpg',
      'https://example.com/image6.jpg',
      'https://example.com/image7.jpg',
      'https://example.com/image8.jpg'
    ],
    
    // 票型数据
    tickets: {
      single: [
        {
          id: 1,
          name: '成人大门票',
          tags: ['随买随用', '极速出票'],
          currentPrice: 120,
          originalPrice: 180,
          available: true
        },
        {
          id: 2,
          name: '学生/老人优惠票',
          tags: ['需身份证'],
          currentPrice: 60,
          originalPrice: 90,
          available: true
        }
      ],
      combo: [
        {
          id: 3,
          name: '蓝月谷+玉龙雪山联票',
          tags: ['组合优惠', '一日通'],
          currentPrice: 220,
          originalPrice: 300,
          available: true
        }
      ],
      package: [
        {
          id: 4,
          name: '家庭亲子套票',
          tags: ['2大1小', '超值套餐'],
          currentPrice: 280,
          originalPrice: 380,
          available: true
        }
      ]
    },
    
    // 当前显示的票型
    currentTickets: [],
    
    // 最低价格
    lowestPrice: 120,
    
    // 示例评价
    sampleReview: {
      name: '林小鹿',
      avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAk4xdKIk1AkbmFZG6aOr4iTgbDvViFDuToDNlexOzc7Vo1FR94N3QT0U8W9YK-iONDiSuk-YQZeYxNFcH4EwG24289NrH7JY7SJ6Kfp1QSXrgKF_rX3PBWi4-6v99P-YV3xfyujo1VBVtB2K4f9GyTZ1lUSwCPAOQA0BxTIj9yToJe9B1XkKPbxu3kB_njVeqkeB1KDgPfdKUOfAmEYHQeF_szVWs8oz1fx026cne1snMPQcx17FqGl3voRC5agq4qWYf5iBIAhg9i',
      rating: 5,
      time: '2天前',
      content: '景色真的太美了！水是蒂芙尼蓝，拍照非常出片。建议早上早点去，人少光线好，可以慢慢逛。',
      images: [
        'https://lh3.googleusercontent.com/aida-public/AB6AXuBCO5ot3gthel9iYb_UpvCtJmrbgD_UutpEYhkDk6psuMfa7gFTc2n9Vpb66Ppy5GjeoD1tP-iwmgi9oNhE3qpf9DoDYb87kpwBoxD02lpAhGzBvEJQrizVoTzIK2TKfUhVVUut0lGkVYSFfPbnNGllG1NunVa9M-JvlO43KEEptjV5GD0eVRUii3rJ5pTn2RZ0qY96VX4yp7gz_zyvq7jZeID1M9kLXEB1VpEfoh47k8ipsrqpQG0iWGgL0e9PsigFdRymWCA59l9m',
        'https://lh3.googleusercontent.com/aida-public/AB6AXuCmzIp1AAjI9IQhbof3dR9RFjT4J_eBtlFeRoBK9_X6z_wycmlxPCBPg1v2B-l7Oy9KA9WvIhLyDq_1SdHkkIbsOcY6IKpuiU0tnBoRcldyblX2GyXSk10JNrVBc-hnHdW1KmUrDssL0TYRYUBszRq0LL7E_Ks7k94Eisp6O8u7eF2ATbEw5UZE54Prk7d1d7lT1NrUH7iDyhhS_ESxFs4Z8UdTjDXvCV-RbwPy1W_GTVhnwXmRvrWMNWWb75gBQxnlcMO5CQSxZ0kc'
      ]
    }
  },

  onLoad: function(options) {
    console.log('页面加载，参数:', options);
    
    // 从参数获取景区ID
    if (options && options.id) {
      this.setData({ scenicId: options.id });
      this.loadScenicData(options.id);
    }
    
    // 计算滚动区域高度
    this.calculateScrollHeight();
    
    // 设置当前图片
    this.setData({
      currentImage: this.data.imageList[0],
      currentTickets: this.data.tickets.single
    });
    
    // 检查收藏状态
    this.checkFavoriteStatus();
  },

  onReady: function() {
    console.log('页面渲染完成');
  },

  onShow: function() {
    console.log('页面显示');
  },

  onUnload: function() {
    console.log('页面卸载');
  },

  // 计算滚动区域高度
  calculateScrollHeight: function() {
    const that = this;
    wx.getSystemInfo({
      success: function(res) {
        const windowHeight = res.windowHeight;
        const windowWidth = res.windowWidth;
        
        // 创建选择器查询底部栏高度
        const query = wx.createSelectorQuery();
        query.select('.bottom-bar').boundingClientRect();
        query.exec(function(rects) {
          let bottomBarHeight = 200; // 默认高度
          if (rects && rects[0]) {
            bottomBarHeight = rects[0].height;
          }
          
          const scrollHeight = windowHeight - bottomBarHeight;
          console.log('计算滚动高度:', scrollHeight);
          
          that.setData({
            scrollHeight: scrollHeight
          });
        });
      },
      fail: function(err) {
        console.error('获取系统信息失败:', err);
        // 设置默认高度
        that.setData({ scrollHeight: 1000 });
      }
    });
  },

  // 加载景区数据
  loadScenicData: function(scenicId) {
    console.log('加载景区数据:', scenicId);
    // 这里可以是网络请求
    // wx.request({
    //   url: 'https://api.example.com/scenic/' + scenicId,
    //   success: (res) => {
    //     if (res.data.success) {
    //       this.setData({ scenicData: res.data.data });
    //     }
    //   }
    // });
  },

  // 检查收藏状态
  checkFavoriteStatus: function() {
    try {
      const favoriteList = wx.getStorageSync('favoriteScenics') || [];
      const scenicId = this.data.scenicId || 1;
      const isFavorite = favoriteList.includes(scenicId);
      this.setData({ isFavorite: isFavorite });
    } catch (e) {
      console.error('检查收藏状态失败:', e);
    }
  },

  // 返回上一页
  goBack: function() {
    wx.navigateBack();
  },

  // 切换收藏状态
  toggleFavorite: function() {
    const isFavorite = !this.data.isFavorite;
    this.setData({ isFavorite: isFavorite });
    
    // 更新本地存储
    try {
      let favoriteList = wx.getStorageSync('favoriteScenics') || [];
      const scenicId = this.data.scenicId || 1;
      
      if (isFavorite) {
        if (!favoriteList.includes(scenicId)) {
          favoriteList.push(scenicId);
        }
      } else {
        const index = favoriteList.indexOf(scenicId);
        if (index > -1) {
          favoriteList.splice(index, 1);
        }
      }
      
      wx.setStorageSync('favoriteScenics', favoriteList);
      
      wx.showToast({
        title: isFavorite ? '已收藏' : '已取消收藏',
        icon: 'success',
        duration: 1500
      });
    } catch (e) {
      console.error('更新收藏状态失败:', e);
    }
  },

  // 分享
  shareScenic: function() {
    wx.showShareMenu({
      withShareTicket: true
    });
  },

  // 打开位置导航
  openLocation: function() {
    const location = {
      latitude: 26.852396,
      longitude: 100.238130
    };
    
    wx.openLocation({
      latitude: location.latitude,
      longitude: location.longitude,
      scale: 18,
      name: this.data.scenicData.name,
      address: this.data.scenicData.address
    });
  },

  // 显示购票须知
  showTicketNotice: function() {
    wx.showModal({
      title: '购票须知',
      content: '1. 门票当天有效\n2. 请携带有效证件\n3. 特殊票种需现场验证\n4. 退改规则详见订单详情',
      showCancel: false,
      confirmText: '我知道了',
      confirmColor: '#FBC02D'
    });
  },

  // 切换票型选项卡
  switchTicketTab: function(e) {
    const tab = e.currentTarget.dataset.tab;
    console.log('切换到票型:', tab);
    
    this.setData({
      activeTicketTab: tab,
      currentTickets: this.data.tickets[tab] || []
    });
  },

  // 预订票型
  bookTicket: function(e) {
    const ticketId = e.currentTarget.dataset.id;
    console.log('预订票型:', ticketId);
    
    const scenicId = this.data.scenicId || 1;
    wx.navigateTo({
      url: `/pages/order/confirm?scenicId=${scenicId}&ticketId=${ticketId}`
    });
  },

  // 前往评价页面
  goToReviews: function() {
    const scenicId = this.data.scenicId || 1;
    wx.navigateTo({
      url: `/pages/scenic/reviews/index?id=${scenicId}`
    });
  },

  // 查看完整地图
  viewFullMap: function() {
    const scenicId = this.data.scenicId || 1;
    wx.navigateTo({
      url: `/pages/map/index?scenicId=${scenicId}`
    });
  },

  // 查看全部评价
  viewAllReviews: function() {
    this.goToReviews();
  },

  // 联系客服
  contactCustomerService: function() {
    wx.makePhoneCall({
      phoneNumber: this.data.scenicData.phone
    });
  },

  // 快速预订
  quickBook: function() {
    // 找到第一个可用的票型
    const availableTickets = this.data.currentTickets.filter(ticket => ticket.available);
    if (availableTickets.length > 0) {
      this.bookTicket({ currentTarget: { dataset: { id: availableTickets[0].id } } });
    } else {
      wx.showToast({
        title: '暂无可用票型',
        icon: 'none',
        duration: 2000
      });
    }
  },

  // 页面分享
  onShareAppMessage: function() {
    return {
      title: `${this.data.scenicData.name} | 景区推荐`,
      path: `/pages/scenic/detail/index?id=${this.data.scenicId || 1}`,
      imageUrl: this.data.currentImage
    };
  }
});
