Page({
  data: {
    addresses: [
      {
        id: 1,
        name: '李晓明',
        phone: '186****9999',
        tag: '家',
        address: '北京市朝阳区三里屯街道太古里北区 N8-201',
        isDefault: true
      },
      {
        id: 2,
        name: '王二狗',
        phone: '139****8888',
        tag: '公司',
        address: '上海市静安区南京西路1266号恒隆广场',
        isDefault: false
      },
      {
        id: 3,
        name: '刘小美',
        phone: '135****6789',
        tag: '',
        address: '广东省深圳市南山区粤海街道科技园',
        isDefault: false
      }
    ],
    showDeleteModal: false,
    selectedAddressId: null
  },

  onLoad() {
    // 页面加载时执行
  },

  // 返回上一页
  goBack() {
    wx.navigateBack()
  },

  // 编辑地址
  editAddress(e) {
    const id = e.currentTarget.dataset.id
    console.log('编辑地址:', id)
    // 这里可以跳转到编辑页面
  },

  // 删除地址
  deleteAddress(e) {
    const id = e.currentTarget.dataset.id
    this.setData({
      showDeleteModal: true,
      selectedAddressId: id
    })
  },

  // 确认删除
  confirmDelete() {
    const id = this.data.selectedAddressId
    const addresses = this.data.addresses.filter(item => item.id !== id)
    
    this.setData({
      addresses,
      showDeleteModal: false,
      selectedAddressId: null
    })
    
    wx.showToast({
      title: '删除成功',
      icon: 'success'
    })
  },

  // 取消删除
  cancelDelete() {
    this.setData({
      showDeleteModal: false,
      selectedAddressId: null
    })
  },

  // 添加新地址
  addAddress() {
    wx.navigateTo({
      url: '/pages/address/edit'
    })
  }
})
