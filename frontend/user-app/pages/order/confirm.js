// index.js - 完整版
Page({
  data: {
    // 日期数据
    dates: [
      { day: '今天 10.24', price: '¥418', selected: true },
      { day: '明天 10.25', price: '¥418', selected: false },
      { day: '周六 10.26', price: '¥528', selected: false },
      { day: '周日 10.27', price: '¥528', selected: false }
    ],
    selectedDateIndex: 0,
    
    // 游客数据
    visitors: [
      { 
        name: '李小花', 
        id: '110101********1234', 
        verified: true, 
        selected: true 
      },
      { 
        name: '张大伟', 
        id: '110101********5678', 
        verified: false, 
        selected: false 
      }
    ],
    visitorTabs: ['张大伟', '李小花', '王建国'],
    selectedTabIndex: 1,
    selectedVisitorIndex: 0,
    
    // 协议状态
    agreementChecked: false,
    
    // 价格数据
    totalPrice: 398,
    discount: 20,
    originalPrice: 418,
    
    // 优惠券数据
    couponCount: 1,
    couponAmount: 20
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('页面加载，options:', options);
    // 这里可以添加页面初始化逻辑
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
    console.log('页面渲染完成');
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    console.log('页面显示');
  },

  /**
   * 选择日期
   */
  selectDate(e) {
    const index = e.currentTarget.dataset.index;
    console.log('选择日期索引:', index);
    
    // 更新日期选择状态
    const dates = this.data.dates.map((date, i) => ({
      ...date,
      selected: i === index
    }));
    
    // 如果是周末，价格不同
    let price = 418;
    if (index >= 2) { // 周六和周日
      price = 528;
    }
    
    // 重新计算总价
    const newTotalPrice = price - this.data.discount;
    
    this.setData({
      dates,
      selectedDateIndex: index,
      originalPrice: price,
      totalPrice: newTotalPrice
    });
  },

  /**
   * 选择游客标签
   */
  selectTab(e) {
    const index = e.currentTarget.dataset.index;
    console.log('选择标签索引:', index);
    
    // 更新标签选择状态
    this.setData({
      selectedTabIndex: index
    });
    
    // 如果是已有的游客，同时选中对应的游客
    if (index < this.data.visitors.length) {
      const visitors = this.data.visitors.map((visitor, i) => ({
        ...visitor,
        selected: i === index
      }));
      
      this.setData({
        visitors,
        selectedVisitorIndex: index
      });
    }
  },

  /**
   * 选择游客
   */
  selectVisitor(e) {
    const index = e.currentTarget.dataset.index;
    console.log('选择游客索引:', index);
    
    // 更新游客选择状态
    const visitors = this.data.visitors.map((visitor, i) => ({
      ...visitor,
      selected: i === index
    }));
    
    // 同时更新标签选中状态
    this.setData({
      visitors,
      selectedVisitorIndex: index,
      selectedTabIndex: index
    });
  },

  /**
   * 新增游客
   */
  addVisitor() {
    console.log('点击新增游客');
    wx.showModal({
      title: '新增游客',
      content: '跳转到新增游客页面',
      showCancel: true,
      cancelText: '取消',
      confirmText: '确定',
      success: (res) => {
        if (res.confirm) {
          // 在实际应用中，这里应该跳转到新增游客页面
          // wx.navigateTo({ url: '/pages/add-visitor/add-visitor' });
          
          // 这里模拟新增一个游客
          const newVisitor = {
            name: '新游客',
            id: '110101********0000',
            verified: false,
            selected: false
          };
          
          const visitors = [...this.data.visitors, newVisitor];
          const visitorTabs = [...this.data.visitorTabs, '新游客'];
          
          this.setData({
            visitors,
            visitorTabs
          });
          
          wx.showToast({
            title: '新增游客成功',
            icon: 'success',
            duration: 2000
          });
        }
      }
    });
  },

  /**
   * 编辑游客
   */
  editVisitor(e) {
    const index = e.currentTarget.dataset.index;
    const visitorName = this.data.visitors[index].name;
    console.log('编辑游客:', visitorName);
    
    wx.showModal({
      title: '编辑游客',
      content: `编辑 ${visitorName} 的信息`,
      showCancel: true,
      cancelText: '取消',
      confirmText: '编辑',
      success: (res) => {
        if (res.confirm) {
          // 在实际应用中，这里应该跳转到编辑页面
          // wx.navigateTo({ url: `/pages/edit-visitor/edit-visitor?id=${index}` });
          
          wx.showToast({
            title: '编辑功能开发中',
            icon: 'none',
            duration: 2000
          });
        }
      }
    });
  },

  /**
   * 查看价格日历
   */
  viewPriceCalendar() {
    console.log('查看价格日历');
    wx.showModal({
      title: '价格日历',
      content: '显示各日期价格对比',
      showCancel: false,
      confirmText: '知道了'
    });
  },

  /**
   * 使用优惠券
   */
  useCoupon() {
    console.log('使用优惠券');
    wx.showModal({
      title: '选择优惠券',
      content: '您有1张¥20优惠券可用',
      showCancel: true,
      cancelText: '不使用',
      confirmText: '使用',
      success: (res) => {
        if (res.confirm) {
          wx.showToast({
            title: '已使用¥20优惠券',
            icon: 'success',
            duration: 2000
          });
        }
      }
    });
  },

  /**
   * 切换协议勾选状态
   */
  toggleAgreement(e) {
    const checked = e.detail.value.length > 0;
    console.log('协议勾选状态:', checked);
    
    this.setData({
      agreementChecked: checked
    });
  },

  /**
   * 返回上一页
   */
  goBack() {
    console.log('返回上一页');
    wx.navigateBack({
      delta: 1,
      fail: (err) => {
        console.log('返回失败:', err);
        // 如果无法返回，则跳转到首页
        wx.switchTab({
          url: '/pages/index/index'
        });
      }
    });
  },

  /**
   * 提交订单
   */
  submitOrder() {
    console.log('提交订单');
    
    // 检查是否勾选协议
    if (!this.data.agreementChecked) {
      wx.showToast({
        title: '请先阅读并同意协议',
        icon: 'none',
        duration: 2000
      });
      return;
    }
    
    // 检查是否选择了游客
    const selectedVisitor = this.data.visitors.find(v => v.selected);
    if (!selectedVisitor) {
      wx.showToast({
        title: '请选择游客',
        icon: 'none',
        duration: 2000
      });
      return;
    }
    
    // 显示加载中
    wx.showLoading({
      title: '提交中...',
      mask: true
    });
    
    // 模拟API请求
    setTimeout(() => {
      wx.hideLoading();
      
      // 模拟订单数据
      const orderData = {
        orderId: 'ORD' + Date.now(),
        amount: this.data.totalPrice,
        date: this.data.dates[this.data.selectedDateIndex].day,
        visitor: selectedVisitor.name,
        timestamp: new Date().toISOString()
      };
      
      // 保存订单数据到本地
      try {
        const orders = wx.getStorageSync('orders') || [];
        orders.unshift(orderData);
        wx.setStorageSync('orders', orders);
        
        console.log('订单保存成功:', orderData);
      } catch (e) {
        console.error('保存订单失败:', e);
      }
      
      // 显示成功提示
      wx.showToast({
        title: '订单提交成功',
        icon: 'success',
        duration: 2000,
        success: () => {
          // 延迟跳转到订单详情页
          setTimeout(() => {
            wx.redirectTo({
              url: `/pages/order-detail/order-detail?orderId=${orderData.orderId}&amount=${orderData.amount}`
            });
          }, 2000);
        }
      });
    }, 1500);
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    console.log('页面隐藏');
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    console.log('页面卸载');
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    console.log('下拉刷新');
    // 停止下拉刷新
    wx.stopPullDownRefresh();
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
    console.log('上拉触底');
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: '北京环球影城购票',
      path: '/pages/index/index'
    };
  },

  /**
   * 页面滚动触发事件
   */
  onPageScroll(e) {
    // 如果需要根据滚动做某些操作，可以在这里处理
    // console.log('页面滚动:', e.scrollTop);
  },

  /**
   * 错误处理函数
   */
  onError(msg) {
    console.error('页面错误:', msg);
  }
});