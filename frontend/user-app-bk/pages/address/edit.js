Page({
  data: {
    // 地址数据
    name: '李晓明',
    phone: '18612349999',
    region: '北京市 朝阳区 三里屯街道',
    detail: '太古里北区 N8-201',
    tags: ['家', '公司', '学校'],
    selectedTag: '家',
    isDefault: true,
    showDeleteModal: false,
    customTagInput: ''
  },

  onLoad(options) {
    // 可以从上一页传递地址ID
    const addressId = options.id
    if (addressId) {
      // 根据ID加载地址数据
      this.loadAddressData(addressId)
    }
  },

  // 加载地址数据
  loadAddressData(id) {
    // 这里应该调用API获取地址详情
    console.log('加载地址数据:', id)
  },

  // 输入框输入事件
  onInput(e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    
    this.setData({
      [field]: value
    })
  },

  // 选择地区
  chooseRegion() {
    console.log('选择地区')
    // 这里可以调用微信的地区选择器
    // wx.chooseLocation() 或自定义地区选择器
  },

  // 选择标签
  selectTag(e) {
    const tag = e.currentTarget.dataset.tag
    this.setData({
      selectedTag: tag
    })
  },

  // 切换默认地址
  toggleDefault(e) {
    const value = e.detail.value
    this.setData({
      isDefault: value
    })
  },

  // 添加自定义标签
  addCustomTag() {
    wx.showModal({
      title: '自定义标签',
      editable: true,
      placeholderText: '请输入标签名称',
      success: (res) => {
        if (res.confirm && res.content) {
          const newTag = res.content
          const tags = [...this.data.tags, newTag]
          this.setData({
            tags,
            selectedTag: newTag
          })
        }
      }
    })
  },

  // 保存地址
  saveAddress() {
    const { name, phone, region, detail, selectedTag, isDefault } = this.data
    
    // 表单验证
    if (!name.trim()) {
      wx.showToast({ title: '请输入收货人姓名', icon: 'none' })
      return
    }
    
    if (!phone.trim() || !/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '请输入正确的手机号码', icon: 'none' })
      return
    }
    
    if (!region.trim()) {
      wx.showToast({ title: '请选择所在地区', icon: 'none' })
      return
    }
    
    if (!detail.trim()) {
      wx.showToast({ title: '请输入详细地址', icon: 'none' })
      return
    }
    
    // 保存地址逻辑
    const addressData = {
      name,
      phone,
      region,
      detail,
      tag: selectedTag,
      isDefault
    }
    
    console.log('保存地址:', addressData)
    
    // 显示成功提示
    wx.showToast({
      title: '保存成功',
      icon: 'success',
      duration: 1500,
      success: () => {
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      }
    })
  },

  // 删除地址
  deleteAddress() {
    this.setData({
      showDeleteModal: true
    })
  },

  // 确认删除
  confirmDelete() {
    // 删除地址逻辑
    console.log('删除地址')
    
    wx.showToast({
      title: '删除成功',
      icon: 'success',
      duration: 1500,
      success: () => {
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      }
    })
  },

  // 取消删除
  cancelDelete() {
    this.setData({
      showDeleteModal: false
    })
  },

  // 返回上一页
  goBack() {
    wx.navigateBack()
  }
})