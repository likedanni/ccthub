// pages/index/index.js
const app = getApp();

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
    console.log("首页加载", options);
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
    app.navigateTo("/pages/scenic/list");
  },

  /**
   * 导航到餐饮页面
   */
  navigateToCatering() {
    app.navigateTo("/pages/catering/catering");
  },

  /**
   * 导航到生鲜页面
   */
  navigateToFreshFood() {
    app.navigateTo("/pages/fresh-food/fresh-food");
  },

  /**
   * 导航到文创页面
   */
  navigateToCulture() {
    app.navigateTo("/pages/culture/culture");
  },

  /**
   * 导航到所有活动页面
   */
  navigateToAllActivities() {
    app.navigateTo("/pages/activities/activities");
  },

  /**
   * 导航到活动详情
   */
  navigateToActivityDetail(e) {
    const id = e.currentTarget.dataset.id;
    app.navigateTo("/pages/activity-detail/activity-detail", { id });
  },

  /**
   * 导航到所有票务页面
   */
  navigateToAllTickets() {
    app.navigateTo("/pages/tickets/tickets");
  },

  /**
   * 导航到票务详情
   */
  navigateToTicketDetail(e) {
    const id = e.currentTarget.dataset.id;
    app.navigateTo("/pages/ticket-detail/ticket-detail", { id });
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
      title: "文旅平台 - 功夫熊猫带您游山西",
      path: "/pages/index/index",
    };
  },
});
