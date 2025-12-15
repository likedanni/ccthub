Page({
  data: {
    // Tab数据
    currentTab: 0,
    tabs: ['待评价', '意见反馈'],
    
    // 待评价数据 - 每条数据都有自己的状态
    toRateOrders: [
      {
        id: 1,
        title: '上海迪士尼乐园一日票',
        date: '2023-10-25 出行 · 已完成',
        type: '电子票',
        image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBCCmDJl40iC_btf88Bd8Y59-1oyQ_jkk6xX507jYidtmwcIdP00OzYy72ZgLDuaQHvp6rDvsL1hLR_QW4qqgxSHC-bB5iPF-wwDJ4OHHsqwQoB7ykWN-d5saApmLESg2DScMUoDcA6waxDB7uR6Pbpo7Ah0rdhy9V-kQ3tXFUgou5nIBQh2J1tl4FSdZnElNDOfnWcE3S4qykMZbkPq9Rxsy5YLrU26uVug8DHd1wpghTK6PwXttNQmxtZ044QfN5OLuAAuzhpWZzj',
        showRate: false,
        stars: 0,
        starText: '请选择评分',
        comment: '',
        selectedImages: []
      },
      {
        id: 2,
        title: '外滩全景观光巴士',
        date: '2023-10-20 出行 · 已完成',
        type: '电子票',
        image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCBReeQMd_lHHcJRXLPSgxXn-c-QC7fkIAhi0Zzl_PMz4UO49zfqE8IRjUYmnTBlMUjB2e6-AUrjV5xFf1UVoyzREy0gGv8sERq0sIRHZey7pCl_N4cbRVLNPfYPGibhz74X5NgpB0h_0umSkTZrjDvszwc6dRC0epHOnjGd2cvRaL-TTmKIJWyP3J_Dq2J6zdNVaWUhtN69z9vy12sdAiIEyRA0xVQRwNlpuYzN5ng2Z3nW4jLc8KevwgwV6zt-ddjOiMUQ2UdNZNf',
        showRate: false,
        stars: 0,
        starText: '请选择评分',
        comment: '',
        selectedImages: []
      }
    ],
    
    // 星星数组
    starArray: [1, 2, 3, 4, 5],
    maxImages: 9,
    
    // 意见反馈数据
    feedbackType: '功能故障',
    feedbackTypes: ['功能故障', '产品建议', '咨询问题'],
    feedbackContent: '',
    selectedOrder: null,
    feedbackImages: [],
    contactPhone: '138****8888',
    
    // 反馈历史数据
    feedbackHistory: [
      {
        id: 1,
        type: '功能故障',
        date: '2023-10-12',
        content: '支付页面有时候会卡顿无法...',
        status: '处理中',
        statusColor: 'red'
      },
      {
        id: 2,
        type: '咨询问题',
        date: '2023-09-08',
        content: '关于退改签的规则说明...',
        status: '已完成',
        statusColor: 'gray'
      }
    ],
    showHistory: false
  },

  onLoad: function() {
    // 页面加载
  },

  // 切换Tab
  switchTab: function(e) {
    var index = e.currentTarget.dataset.index
    this.setData({
      currentTab: index
    })
  },

  // 开始评价 - 点击"去评价"
  startRate: function(e) {
    var orderId = e.currentTarget.dataset.id
    var orders = this.data.toRateOrders
    
    // 先关闭所有其他订单的评价框
    for (var i = 0; i < orders.length; i++) {
      if (orders[i].id == orderId) {
        // 切换当前订单的显示状态
        orders[i].showRate = !orders[i].showRate
      } else {
        // 关闭其他订单的评价框
        orders[i].showRate = false
      }
    }
    
    this.setData({
      toRateOrders: orders
    })
  },

  // 关闭评价卡片
  closeRateCard: function(e) {
    var orderId = e.currentTarget.dataset.id
    var orders = this.data.toRateOrders
    
    for (var i = 0; i < orders.length; i++) {
      if (orders[i].id == orderId) {
        orders[i].showRate = false
        break
      }
    }
    
    this.setData({
      toRateOrders: orders
    })
  },

  // 选择星星评分 - 修正
 // 选择星星评分 - 修正
selectStar: function(e) {
 
  var orderId = e.currentTarget.dataset.orderid
  var stars = e.currentTarget.dataset.stars
  var orders = this.data.toRateOrders
  var text = ''
 
  // 根据星星数设置文字
  if (stars == 1) {
    text = '不太满意'
  } else if (stars == 2) {
    text = '一般般'
  } else if (stars == 3) {
    text = '还不错'
  } else if (stars == 4) {
    text = '比较满意，仍有提升空间'
  } else if (stars == 5) {
    text = '非常满意'
  }
  
  // 更新对应的订单数据
  for (var i = 0; i < orders.length; i++) {
    if (orders[i].id == orderId) {
      orders[i].stars = stars
      orders[i].starText = text
      break
    }
  }

  this.setData({
    toRateOrders: orders
  })
},

  // 输入评价内容
  onCommentInput: function(e) {
    var orderId = e.currentTarget.dataset.orderid
    var value = e.detail.value
    var orders = this.data.toRateOrders
    
    for (var i = 0; i < orders.length; i++) {
      if (orders[i].id == orderId) {
        orders[i].comment = value
        break
      }
    }
    
    this.setData({
      toRateOrders: orders
    })
  },

  // 删除图片
  deleteImage: function(e) {
    var orderId = e.currentTarget.dataset.orderid
    var imageId = e.currentTarget.dataset.imageid
    var orders = this.data.toRateOrders
    
    for (var i = 0; i < orders.length; i++) {
      if (orders[i].id == orderId) {
        var images = orders[i].selectedImages
        var newImages = []
        
        for (var j = 0; j < images.length; j++) {
          if (images[j].id != imageId) {
            newImages.push(images[j])
          }
        }
        
        orders[i].selectedImages = newImages
        break
      }
    }
    
    this.setData({
      toRateOrders: orders
    })
  },

  // 上传图片
  uploadImage: function(e) {
    var that = this
    var orderId = e.currentTarget.dataset.orderid
    var orders = this.data.toRateOrders
    var orderIndex = -1
    
    // 找到对应的订单
    for (var i = 0; i < orders.length; i++) {
      if (orders[i].id == orderId) {
        orderIndex = i
        break
      }
    }
    
    if (orderIndex === -1) return
    
    var remaining = this.data.maxImages - orders[orderIndex].selectedImages.length
    
    wx.chooseImage({
      count: remaining,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: function(res) {
        var tempFilePaths = res.tempFilePaths
        var newImages = []
        
        for (var i = 0; i < tempFilePaths.length; i++) {
          newImages.push({
            id: new Date().getTime() + i,
            url: tempFilePaths[i]
          })
        }
        
        orders[orderIndex].selectedImages = orders[orderIndex].selectedImages.concat(newImages)
        if (orders[orderIndex].selectedImages.length > that.data.maxImages) {
          orders[orderIndex].selectedImages = orders[orderIndex].selectedImages.slice(0, that.data.maxImages)
        }
        
        that.setData({
          toRateOrders: orders
        })
      }
    })
  },

  // 提交评价
  submitRate: function(e) {
    var orderId = e.currentTarget.dataset.orderid
    var orders = this.data.toRateOrders
    var orderIndex = -1
    
    // 找到对应的订单
    for (var i = 0; i < orders.length; i++) {
      if (orders[i].id == orderId) {
        orderIndex = i
        break
      }
    }
    
    if (orderIndex === -1) return
    
    var order = orders[orderIndex]
    
    if (!order.comment.trim()) {
      wx.showToast({
        title: '请填写评价内容',
        icon: 'none'
      })
      return
    }
    
    if (order.comment.length < 15) {
      wx.showToast({
        title: '评价内容至少15字',
        icon: 'none'
      })
      return
    }
    
    if (order.stars === 0) {
      wx.showToast({
        title: '请选择评分',
        icon: 'none'
      })
      return
    }
    
    console.log('提交评价:', { 
      orderId: orderId,
      stars: order.stars, 
      comment: order.comment, 
      selectedImages: order.selectedImages 
    })
    
    wx.showToast({
      title: '评价提交成功',
      icon: 'success',
      success: function() {
        // 提交成功后关闭评价框并重置数据
        setTimeout(function() {
          orders[orderIndex].showRate = false
          orders[orderIndex].stars = 0
          orders[orderIndex].starText = '请选择评分'
          orders[orderIndex].comment = ''
          orders[orderIndex].selectedImages = []
          
          that.setData({
            toRateOrders: orders
          })
        }, 1500)
      }.bind(that)
    })
  },

  // 选择反馈类型
  selectFeedbackType: function(e) {
    var type = e.currentTarget.dataset.type
    this.setData({
      feedbackType: type
    })
  },

  // 输入反馈内容
  onFeedbackInput: function(e) {
    this.setData({
      feedbackContent: e.detail.value
    })
  },

  // 上传反馈图片
  uploadFeedbackImage: function() {
    var that = this
    var remaining = 9 - this.data.feedbackImages.length
    
    wx.chooseImage({
      count: remaining,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: function(res) {
        var tempFilePaths = res.tempFilePaths
        var newImages = []
        
        for (var i = 0; i < tempFilePaths.length; i++) {
          newImages.push({
            id: new Date().getTime() + i,
            url: tempFilePaths[i]
          })
        }
        
        var allImages = that.data.feedbackImages.concat(newImages)
        if (allImages.length > 9) {
          allImages = allImages.slice(0, 9)
        }
        
        that.setData({
          feedbackImages: allImages
        })
      }
    })
  },

  // 输入联系方式
  onContactInput: function(e) {
    this.setData({
      contactPhone: e.detail.value
    })
  },

  // 提交反馈
  submitFeedback: function() {
    var feedbackType = this.data.feedbackType
    var feedbackContent = this.data.feedbackContent
    
    if (!feedbackContent.trim()) {
      wx.showToast({
        title: '请填写反馈内容',
        icon: 'none'
      })
      return
    }
    
    console.log('提交反馈:', { 
      feedbackType: feedbackType, 
      feedbackContent: feedbackContent, 
      contactPhone: this.data.contactPhone 
    })
    
    wx.showToast({
      title: '反馈提交成功',
      icon: 'success',
      success: function() {
        setTimeout(function() {
          this.setData({
            feedbackType: '功能故障',
            feedbackContent: '',
            feedbackImages: []
          })
        }.bind(this), 1500)
      }.bind(this)
    })
  },

  // 切换历史显示
  toggleHistory: function() {
    this.setData({
      showHistory: !this.data.showHistory
    })
  }
})