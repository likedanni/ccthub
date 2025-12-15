Page({
  data: {
    // 表单数据
    title: '',
    titleLength: 0,
    categoryIndex: -1,
    selectedCategoryValue: '',
    content: '',
    images: [],
    phone: '',
    
    // 分类选项
    categoryOptions: [
      { label: '景区招聘', value: 'scenic' },
      { label: '商户合作', value: 'merchant' },
      { label: '个人求职', value: 'job' },
      { label: '店铺转让', value: 'transfer' },
      { label: '其他需求', value: 'other' }
    ],
    
    // 前三个分类（用于标签显示）
    firstThreeCategories: [],
    
    // 表单验证状态
    canSubmit: false
  },

  onLoad: function() {
    // 页面加载时尝试获取用户信息
    this.checkLoginStatus();
    
    // 设置前三个分类
    this.setData({
      firstThreeCategories: this.data.categoryOptions.slice(0, 3)
    });
  },

  // 检查登录状态
  checkLoginStatus: function() {
    try {
      var userInfo = wx.getStorageSync('userInfo');
      if (userInfo && userInfo.phone) {
        this.setData({
          phone: userInfo.phone
        });
        this.validateForm();
      }
    } catch (e) {
      console.error('读取用户信息失败', e);
    }
  },

  // 标题输入
  onTitleInput: function(e) {
    var value = e.detail.value;
    this.setData({
      title: value,
      titleLength: value.length
    });
    this.validateForm();
  },

  // 分类选择
  onCategoryChange: function(e) {
    var index = e.detail.value;
    var category = this.data.categoryOptions[index];
    if (category) {
      this.setData({
        categoryIndex: index,
        selectedCategoryValue: category.value
      });
      this.validateForm();
    }
  },

  // 标签点击
  onTagTap: function(e) {
    var value = e.currentTarget.dataset.value;
    var categoryOptions = this.data.categoryOptions;
    var index = -1;
    
    // 查找对应分类的索引
    for (var i = 0; i < categoryOptions.length; i++) {
      if (categoryOptions[i].value === value) {
        index = i;
        break;
      }
    }
    
    if (index !== -1) {
      this.setData({
        categoryIndex: index,
        selectedCategoryValue: value
      });
      this.validateForm();
    }
  },

  // 内容输入
  onContentInput: function(e) {
    this.setData({
      content: e.detail.value
    });
    this.validateForm();
  },

  // 添加图片
  onAddImage: function() {
    var images = this.data.images;
    if (images.length >= 9) {
      wx.showToast({
        title: '最多只能上传9张图片',
        icon: 'none'
      });
      return;
    }

    var that = this;
    wx.chooseMedia({
      count: 9 - images.length,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: function(res) {
        var newImages = res.tempFiles.map(function(file) {
          return file.tempFilePath;
        });
        that.setData({
          images: images.concat(newImages)
        });
      }
    });
  },

  // 移除图片
  onRemoveImage: function(e) {
    var index = e.currentTarget.dataset.index;
    var images = this.data.images.slice(); // 创建副本
    images.splice(index, 1);
    this.setData({ images: images });
  },

  // 手机号输入
  onPhoneInput: function(e) {
    var value = e.detail.value.replace(/\D/g, '');
    this.setData({
      phone: value
    });
    this.validateForm();
  },

  // 自动填充
  onAutoFill: function() {
    var that = this;
    wx.getUserProfile({
      desc: '用于自动填充联系方式',
      success: function(res) {
        var userInfo = res.userInfo;
        wx.showModal({
          title: '使用手机号',
          content: '是否使用 ' + userInfo.nickName + ' 关联的手机号？',
          success: function(modalRes) {
            if (modalRes.confirm) {
              // 这里应该调用获取手机号的API
              // 模拟获取手机号
              var mockPhone = '13800000000';
              that.setData({
                phone: mockPhone
              });
              that.validateForm();
              
              // 保存到本地
              wx.setStorageSync('userInfo', {
                nickName: userInfo.nickName,
                avatarUrl: userInfo.avatarUrl,
                phone: mockPhone
              });
            }
          }
        });
      },
      fail: function() {
        wx.showToast({
          title: '获取用户信息失败',
          icon: 'none'
        });
      }
    });
  },

  // 表单验证
  validateForm: function() {
    var title = this.data.title;
    var categoryIndex = this.data.categoryIndex;
    var content = this.data.content;
    var phone = this.data.phone;
    
    var isValid = title && title.trim().length > 0 &&
                   categoryIndex >= 0 &&
                   content && content.trim().length > 0 &&
                   /^1[3-9]\d{9}$/.test(phone);
    
    this.setData({ canSubmit: isValid });
    return isValid;
  },

  // 提交表单
  onSubmit: function() {
    if (!this.validateForm()) {
      wx.showToast({
        title: '请填写完整信息',
        icon: 'none'
      });
      return;
    }

    var formData = {
      title: this.data.title,
      category: this.data.categoryOptions[this.data.categoryIndex],
      content: this.data.content,
      images: this.data.images,
      phone: this.data.phone,
      createTime: new Date().toISOString()
    };

    console.log('提交的数据:', formData);

    // 模拟提交
    wx.showLoading({
      title: '提交中...',
      mask: true
    });

    var that = this;
    setTimeout(function() {
      wx.hideLoading();
      wx.showToast({
        title: '提交成功，等待审核',
        icon: 'success',
        duration: 2000
      });

      // 清空表单
      that.setData({
        title: '',
        titleLength: 0,
        categoryIndex: -1,
        selectedCategoryValue: '',
        content: '',
        images: [],
        phone: '',
        canSubmit: false
      });

      // 返回上一页
      setTimeout(function() {
        wx.navigateBack();
      }, 1500);
    }, 1500);
  }
});