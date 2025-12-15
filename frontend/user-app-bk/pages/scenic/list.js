// pages/scenic/list/index.js
// 获取应用实例
const app = getApp()

Page({
  data: {
    // 当前筛选类型
    activeFilter: 'all',
    
    // 景区列表数据
    scenicList: [
      {
        id: 1,
        name: '玉龙雪山',
        level: '5A景区',
        levelClass: 'level-5a',
        description: '纳西人心中的神山，冰川公园，云海奇观...',
        image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCBVQ8zVY5zpDoY1_izP18nk0MkNBSfGlOyd8orpHBx92FaMgmZ6lJlKnca1_TG7NIyK_om2DRY-B_nUELYFbCC7rxl3OFoaFCx5NvkqkHpMo1pb2cpw8l-rU-fC_LifMPQ66cT_vLd2yHkRBc6LK4CQCMu4TLiZKOmKvB9JGqxK6XeRP2Pp64mTbCWZNi4om-NpcHCxzW2ntI2yzUroN_RDu0y-IOebUauUk297BZtwOeeBUnVfAAZ1hsLusg4pASVXsSF5x0qup4_',
        imageCount: 12,
        isHot: false,
        tags: ['自然风光', '登山'],
        rating: 4.9,
        reviewCount: '2.1k',
        price: 180
      },
      {
        id: 2,
        name: '蓝月谷',
        level: '4A景区',
        levelClass: 'level-4a',
        description: '仿佛一块碧玉，静静流淌在山谷之中...',
        image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBqucFDUJkGCrGDVrytg9cPlnX7pec9IgM4SdyCo38ImgIafdVgmXvUQa2s8V_R9gZ9gSxI8keG97wNMldxsIZTXcabO-9ubMOI3LpsoInaFVliIfKelEPzqvte4hoNRXFpjSE-Cx0DqpqC08sED9VLlxZtZmA027uduKostTNZsupHoY-K-m64AZDWDajWpmVBqKNBLpfaXmJ93KbShypalEfOK0eac3R1mTc4zWB00RvrLyeAa3BTmlkR4UN4xmQdvoE46YPNJPme',
        imageCount: 0,
        isHot: true,
        tags: ['摄影圣地', '湖泊'],
        rating: 4.7,
        reviewCount: '',
        price: 50
      },
      {
        id: 3,
        name: '云杉坪',
        level: '4A景区',
        levelClass: 'level-4a',
        description: '隐藏在原始森林中的高山草甸，静谧幽深...',
        image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDlZkHrbrU_Fnghf9xD9O5m2gpuAcw0IRQFMXP3Bqd1qZvcbR8bZSs30D7UdC443MWjMs1gdCMMh9iaXLBHwY2EcP41VoxthKFqF31lVJJqXDeoEPHvpTZ9T5PrNi0I5uT0220ieqYeUHu9G7NluU0CH2S4zH7bjYZPz-WFbEKc-FeqbIEbudbwxa6LZBu7uc0SwqwXaWTz5Fh_A5rCJiwtOx5CuTNgwExoemhAuKyv84iv8I1USEqN4GvQikt2yXuvXOv7utvFJ8bU',
        imageCount: 0,
        isHot: false,
        tags: ['森林', '徒步'],
        rating: 4.5,
        reviewCount: '',
        price: 60
      }
    ],
    
    // 分页相关
    loading: false,
    hasMore: true,
    page: 1,
    pageSize: 10,
    
    // 滚动区域高度
    scrollHeight: 0
  },

  onLoad: function(options) {
    console.log('景区列表页加载', options);
    
    // 计算滚动区域高度
    this.calculateScrollHeight();
    
    // 监听窗口尺寸变化
    if (wx.onWindowResize) {
      wx.onWindowResize(() => {
        this.calculateScrollHeight();
      });
    }
    
    // 加载景区列表
    this.loadScenicList();
  },

  onReady: function() {
    console.log('景区列表页渲染完成');
  },

  onShow: function() {
    console.log('景区列表页显示');
  },

  onHide: function() {
    console.log('景区列表页隐藏');
  },

  onUnload: function() {
    console.log('景区列表页卸载');
  },

  // 计算滚动区域高度
  calculateScrollHeight: function() {
    var that = this;
    wx.getSystemInfo({
      success: function(res) {
        // 获取窗口高度
        var windowHeight = res.windowHeight;
        var windowWidth = res.windowWidth;
        
        console.log('窗口尺寸:', windowWidth + 'x' + windowHeight);
        
        // 创建选择器获取筛选栏高度
        var query = wx.createSelectorQuery();
        query.select('.filter-bar').boundingClientRect();
        query.exec(function(rects) {
          var filterBarHeight = 60; // 默认高度
          if (rects && rects[0]) {
            filterBarHeight = rects[0].height;
          }
          
          // 计算滚动区域高度
          var scrollHeight = windowHeight - filterBarHeight;
          console.log('筛选栏高度:', filterBarHeight, '滚动区域高度:', scrollHeight);
          
          that.setData({
            scrollHeight: scrollHeight
          });
        });
      },
      fail: function(err) {
        console.error('获取系统信息失败:', err);
        // 设置默认高度
        that.setData({
          scrollHeight: 600
        });
      }
    });
  },

  // 筛选按钮点击
  onFilterTap: function(e) {
    var type = e.currentTarget.dataset.type;
    console.log('筛选类型:', type);
    
    this.setData({
      activeFilter: type
    });
    
    // 这里可以添加筛选逻辑
    this.loadScenicList();
  },

  // 景区卡片点击
  onScenicTap: function(e) {
    var id = e.currentTarget.dataset.id;
    console.log('点击景区ID:', id);
    
    // 跳转到景区详情页
    wx.navigateTo({
      url: '/pages/scenic/detail?id=' + id
    });
  },

  // 预订按钮点击
  onBookTap: function(e) {
    var id = e.currentTarget.dataset.id;
    console.log('预订景区ID:', id);
    
    // 阻止事件冒泡
    if (e.stopPropagation) {
      e.stopPropagation();
    }
    
    // 跳转到预订页面
    wx.navigateTo({
      url: '/pages/booking/index?id=' + id
    });
  },

  // 滚动到底部加载更多
  onReachBottom: function() {
    if (this.data.loading || !this.data.hasMore) {
      return;
    }
    
    this.loadMoreData();
  },

  // 加载更多数据
  loadMoreData: function() {
    var that = this;
    this.setData({
      loading: true
    });
    
    wx.showLoading({
      title: '加载中...',
      mask: true
    });
    
    // 模拟网络请求延迟
    setTimeout(function() {
      var newData = [
        {
          id: 4,
          name: '牦牛坪',
          level: '4A景区',
          levelClass: 'level-4a',
          description: '高原草甸风光，牦牛成群...',
          image: 'https://example.com/image4.jpg',
          imageCount: 8,
          isHot: false,
          tags: ['草原', '摄影'],
          rating: 4.6,
          reviewCount: '1.5k',
          price: 70
        },
        {
          id: 5,
          name: '甘海子',
          level: '4A景区',
          levelClass: 'level-4a',
          description: '高山湖泊，倒影如画...',
          image: 'https://example.com/image5.jpg',
          imageCount: 0,
          isHot: true,
          tags: ['湖泊', '观景'],
          rating: 4.4,
          reviewCount: '800',
          price: 40
        },
        {
          id: 6,
          name: '印象丽江',
          level: '5A景区',
          levelClass: 'level-5a',
          description: '大型实景演出，感受纳西文化...',
          image: 'https://example.com/image6.jpg',
          imageCount: 15,
          isHot: true,
          tags: ['演出', '文化'],
          rating: 4.8,
          reviewCount: '3.2k',
          price: 280
        }
      ];
      
      // 检查是否还有更多数据
      var hasMore = that.data.page < 2; // 假设总共有2页数据
      
      that.setData({
        scenicList: that.data.scenicList.concat(newData),
        loading: false,
        hasMore: hasMore,
        page: that.data.page + 1
      });
      
      wx.hideLoading();
      
      if (!hasMore) {
        wx.showToast({
          title: '已加载所有数据',
          icon: 'none',
          duration: 2000
        });
      }
      
      console.log('加载更多数据完成，当前页数:', that.data.page);
    }, 1500);
  },

  // 加载景区列表
  loadScenicList: function() {
    console.log('加载景区列表数据');
    
    // 模拟网络请求
    var that = this;
    this.setData({
      loading: true
    });
    
    // 这里可以是实际的网络请求
    // wx.request({
    //   url: app.globalData.baseUrl + '/api/scenic/list',
    //   method: 'GET',
    //   data: {
    //     filter: this.data.activeFilter,
    //     page: this.data.page,
    //     pageSize: this.data.pageSize
    //   },
    //   success: function(res) {
    //     if (res.data.code === 0) {
    //       that.setData({
    //         scenicList: res.data.data.list,
    //         hasMore: res.data.data.hasMore,
    //         loading: false
    //       });
    //     }
    //   },
    //   fail: function(err) {
    //     console.error('请求失败:', err);
    //     that.setData({
    //       loading: false
    //     });
    //   }
    // });
    
    // 模拟请求完成
    setTimeout(function() {
      that.setData({
        loading: false
      });
      console.log('景区列表加载完成');
    }, 500);
  },

  // 下拉刷新
  onPullDownRefresh: function() {
    console.log('下拉刷新');
    
    // 重置数据
    this.setData({
      scenicList: [],
      page: 1,
      hasMore: true,
      loading: true
    });
    
    // 重新加载数据
    var that = this;
    setTimeout(function() {
      that.setData({
        scenicList: [
          {
            id: 1,
            name: '玉龙雪山',
            level: '5A景区',
            levelClass: 'level-5a',
            description: '纳西人心中的神山，冰川公园，云海奇观...',
            image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCBVQ8zVY5zpDoY1_izP18nk0MkNBSfGlOyd8orpHBx92FaMgmZ6lJlKnca1_TG7NIyK_om2DRY-B_nUELYFbCC7rxl3OFoaFCx5NvkqkHpMo1pb2cpw8l-rU-fC_LifMPQ66cT_vLd2yHkRBc6LK4CQCMu4TLiZKOmKvB9JGqxK6XeRP2Pp64mTbCWZNi4om-NpcHCxzW2ntI2yzUroN_RDu0y-IOebUauUk297BZtwOeeBUnVfAAZ1hsLusg4pASVXsSF5x0qup4_',
            imageCount: 12,
            isHot: false,
            tags: ['自然风光', '登山'],
            rating: 4.9,
            reviewCount: '2.1k',
            price: 180
          },
          {
            id: 2,
            name: '蓝月谷',
            level: '4A景区',
            levelClass: 'level-4a',
            description: '仿佛一块碧玉，静静流淌在山谷之中...',
            image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBqucFDUJkGCrGDVrytg9cPlnX7pec9IgM4SdyCo38ImgIafdVgmXvUQa2s8V_R9gZ9gSxI8keG97wNMldxsIZTXcabO-9ubMOI3LpsoInaFVliIfKelEPzqvte4hoNRXFpjSE-Cx0DqpqC08sED9VLlxZtZmA027uduKostTNZsupHoY-K-m64AZDWDajWpmVBqKNBLpfaXmJ93KbShypalEfOK0eac3R1mTc4zWB00RvrLyeAa3BTmlkR4UN4xmQdvoE46YPNJPme',
            imageCount: 0,
            isHot: true,
            tags: ['摄影圣地', '湖泊'],
            rating: 4.7,
            reviewCount: '',
            price: 50
          }
        ],
        loading: false
      });
      
      // 停止下拉刷新
      wx.stopPullDownRefresh();
      
      wx.showToast({
        title: '刷新成功',
        icon: 'success',
        duration: 1500
      });
    }, 1000);
  },

  // 页面分享
  onShareAppMessage: function() {
    return {
      title: '发现精彩景区，快来预订吧！',
      path: '/pages/scenic/list/index'
    };
  },

  // 分享到朋友圈
  onShareTimeline: function() {
    return {
      title: '景区推荐 | 发现美丽风景',
      query: ''
    };
  },

  // 错误处理
  onError: function(msg) {
    console.error('页面发生错误:', msg);
    wx.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    });
  },

  // 页面滚动
  onPageScroll: function(e) {
    // 可以在这里处理页面滚动事件
    // console.log('页面滚动:', e.scrollTop);
  },

  // 获取星星显示状态
  getStarStatus: function(rating) {
    var stars = [];
    var fullStars = Math.floor(rating);
    var hasHalfStar = rating % 1 >= 0.5;
    var emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
    
    // 添加满星
    for (var i = 0; i < fullStars; i++) {
      stars.push('full');
    }
    
    // 添加半星
    if (hasHalfStar) {
      stars.push('half');
    }
    
    // 添加空星
    for (var j = 0; j < emptyStars; j++) {
      stars.push('empty');
    }
    
    return stars;
  }
});
