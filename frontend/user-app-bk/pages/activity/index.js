const app = getApp();

Page({
  data: {
    activities: [
      {
        id: 1,
        title: "城市涂鸦艺术展打卡",
        tag: "打卡",
        date: "10.01 - 10.15",
        location: "798 艺术区",
        participants: "2.3k",
        status: "hot",
        statusText: "火热进行",
        liked: false,
        ended: false,
      },
      {
        id: 2,
        title: "海滨度假村探店",
        tag: "打卡",
        date: "长期有效",
        location: "三亚",
        participants: "892",
        status: "ongoing",
        statusText: "进行中",
        liked: false,
        ended: false,
      },
      {
        id: 3,
        title: "周末博物馆奇妙夜",
        tag: "积分",
        date: "10.12 18:00",
        location: "国家博物馆",
        participants: "500",
        status: "upcoming",
        statusText: "即将开始",
        liked: false,
        ended: false,
      },
      {
        id: 4,
        title: "社区环保知识讲座",
        tag: "积分",
        date: "10.25 14:00",
        location: "社区中心",
        participants: "120",
        status: "signing",
        statusText: "报名中",
        liked: false,
        ended: false,
      },
      {
        id: 5,
        title: "草莓音乐节限定活动",
        tag: "主题",
        date: "10.20 - 10.22",
        location: "世园公园",
        participants: "10k+",
        status: "hot",
        statusText: "火热报名",
        liked: true,
        ended: false,
      },
      {
        id: 6,
        title: "夜游锦江摄影赛",
        tag: "打卡",
        date: "09.10 - 09.30",
        location: "锦江",
        participants: "5k+",
        status: "ended",
        statusText: "回顾",
        liked: false,
        ended: true,
      },
    ],
    activeFilter: "全部",
  },

  onLoad() {
    console.log("页面加载完成");
  },

  switchFilter(e) {
    const filter = e.currentTarget.dataset.filter;
    this.setData({ activeFilter: filter });
  },

  toggleLike(e) {
    const id = e.currentTarget.dataset.id;
    const activities = this.data.activities.map((item) => {
      if (item.id === id && !item.ended) {
        return { ...item, liked: !item.liked };
      }
      return item;
    });
    this.setData({ activities });

    const activity = activities.find((item) => item.id === id);
    if (activity) {
      app.showToast(activity.liked ? "已收藏" : "已取消收藏");
    }
  },

  onCardTap(e) {
    const id = Number(e.currentTarget.dataset.id);
    const activity = this.data.activities.find(
      (item) => Number(item.id) === id
    );

    if (!activity) {
      app.showToast("活动信息不存在");
      return;
    }

    if (activity.ended) {
      app.showToast("该活动已结束");
    } else {
      app.navigateTo("/pages/activity/detail", { id });
    }
  },
});
