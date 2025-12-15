// pages/index/index.js
Page({
  /**
   * 页面的初始数据
   */
  data: {
    // 这里可以添加页面数据
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('首页加载', options);
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    // 页面显示时的逻辑
  },

  /**
   * 导航到景区页面
   */
  navigateToScenic() {
    wx.navigateTo({
      url: '/pages/scenic/list'
    });
  },

  /**
   * 导航到餐饮页面
   */
  navigateToCatering() {
    wx.navigateTo({
      url: '/pages/catering/catering'
    });
  },

  /**
   * 导航到生鲜页面
   */
  navigateToFreshFood() {
    wx.navigateTo({
      url: '/pages/fresh-food/fresh-food'
    });
  },

  /**
   * 导航到文创页面
   */
  navigateToCulture() {
    wx.navigateTo({
      url: '/pages/culture/culture'
    });
  },

  /**
   * 导航到所有活动页面
   */
  navigateToAllActivities() {
    wx.navigateTo({
      url: '/pages/activities/activities'
    });
  },

  /**
   * 导航到活动详情
   */
  navigateToActivityDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/activity-detail/activity-detail?id=${id}`
    });
  },

  /**
   * 导航到所有票务页面
   */
  navigateToAllTickets() {
    wx.navigateTo({
      url: '/pages/tickets/tickets'
    });
  },

  /**
   * 导航到票务详情
   */
  navigateToTicketDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/ticket-detail/ticket-detail?id=${id}`
    });
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    // 下拉刷新逻辑
    setTimeout(() => {
      wx.stopPullDownRefresh();
    }, 1000);
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: '文旅平台 - 功夫熊猫带您游山西',
      path: '/pages/index/index'
    };
  }
});
