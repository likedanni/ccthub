Page({
  data: {
    progress: 2,
    total: 5,
    tasks: [
      {
        id: 1,
        title: '打卡点 1：古城南门',
        desc: '在此拍摄一张带有城门标志建筑的照片。',
        status: '已完成',
        points: 50,
        locked: false,
        icon: 'check'
      },
      {
        id: 2,
        title: '打卡点 2：钟楼广场',
        desc: '请前往钟楼广场中央，找到"百年钟声"石碑，并拍摄一张合影上传。',
        status: '进行中',
        points: 100,
        locked: false,
        icon: 'my_location'
      },
      {
        id: 3,
        title: '打卡点 3：民俗博物馆',
        desc: '',
        status: '审核中',
        points: 50,
        locked: false,
        icon: 'hourglass_top',
        reviewImage: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAdD4Ia5o94YaALIB116ktHgCoSRS9sST79WeLwJO7C5GxxHcG6n96WsdRq9tULK7IxpbYFrMtueApU--6c0kGmDPem-ajuRbjMr6YcG_TLEVD3UIieJBqK8GqyQBPCpFL1QCP_87zPYjqb4eQZ6_q3L0CYuFgys31uB8eiDaVFujg5vAbzrLSzNA21yhJ2rcTlh7PcNWwnXzMvnZYZb6TYmGQItshQ95qpDVXeIPZ-ueCX1_TDBQv-Jl10_wly0p102saGgcy7YvuR'
      },
      {
        id: 4,
        title: '打卡点 4：护城河',
        desc: '完成上一站任务后自动解锁。',
        status: '未解锁',
        points: 100,
        locked: true,
        icon: 'lock'
      },
      {
        id: 5,
        title: '打卡点 5：文庙',
        desc: '完成上一站任务后自动解锁。',
        status: '终点站',
        points: 200,
        locked: true,
        icon: 'lock'
      }
    ],
    totalPoints: 500
  },

  onLoad() {
    // 计算进度百分比
    const progressPercent = (this.data.progress / this.data.total) * 100;
    this.setData({ progressPercent });
  },

  uploadImage() {
    wx.chooseImage({
      count: 1,
      success: (res) => {
        const tempFilePath = res.tempFilePaths[0];
        wx.showToast({
          title: '上传成功，等待审核',
          icon: 'success'
        });
        // 这里可以调用上传接口
      }
    });
  },

  openNavigation() {
    wx.showToast({
      title: '即将打开导航',
      icon: 'none'
    });
    // 这里可以调用地图导航功能
  }
});