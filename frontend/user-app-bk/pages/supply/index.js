// supply-demand.js
Page({
  data: {
    supplies: [
      {
        id: 1,
        title: "大理古城民宿招义工，包吃住，氛围超好",
        time: "刚刚发布",
        author: "归去来兮民宿",
        description: "寻找有趣的灵魂！主要负责前台接待和简单的咖啡制作，工作轻松，老板nice，定期组织聚餐出游...",
        tag: "recruitment",
        tagText: "#招聘"
      },
      {
        id: 2,
        title: "川西小环线自驾游，缺2人，AA制",
        time: "1小时前",
        author: "爱旅行的小王",
        description: "计划下周五成都出发，已有车和老司机。希望你性格开朗，不矫情，喜欢拍照就更好了！行程自由...",
        tag: "travel",
        tagText: "#找搭子"
      },
      {
        id: 3,
        title: "独立摄影师接单，擅长古风/日系写真",
        time: "3小时前",
        author: "咔嚓Studio",
        description: "在这个美好的季节，想为你记录下最美的瞬间。价格美丽，出片快，提供服装和简单妆造...",
        tag: "service",
        tagText: "#服务"
      },
      {
        id: 4,
        title: "景区文创雪糕寻求源头工厂合作",
        time: "5小时前",
        author: "云顶风景区运营部",
        description: "我们需要寻找一家有经验的文创雪糕生产商，要求能定制模具，保证食品安全，支持小批量打样...",
        tag: "cooperation",
        tagText: "#合作"
      }
    ],
    activeFilter: '全部'
  },

  onLoad() {
    console.log('供需广场页面加载');
  },

  // 切换筛选
  switchFilter(e) {
    const filter = e.currentTarget.dataset.filter;
    this.setData({ activeFilter: filter });
    console.log('切换筛选:', filter);
  },

  // 联系TA
  onContact(e) {
    const id = e.currentTarget.dataset.id;
    const supply = this.data.supplies.find(item => item.id === id);
    
    wx.showModal({
      title: '联系发布者',
      content: `确定要联系"${supply.title}"的发布者吗？`,
      success(res) {
        if (res.confirm) {
          wx.showToast({
            title: '联系成功',
            icon: 'success'
          });
        }
      }
    });
  },

  // 搜索
  onSearch(e) {
    const keyword = e.detail.value;
    console.log('搜索关键词:', keyword);
    if (keyword) {
      wx.showToast({
        title: `搜索: ${keyword}`,
        icon: 'none'
      });
    }
  },

  // 发布
  onPublish() {
    wx.navigateTo({
      url: '/pages/supply/publish/index'
    });
  },

  // 点击卡片
  onCardTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/supplyDetail/supplyDetail?id=${id}`
    });
  }
});
