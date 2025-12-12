<template>
  <view class="container">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-header">
        <image :src="userInfo.avatarUrl || defaultAvatar" class="avatar" mode="aspectFill" />
        <view class="user-info">
          <text class="nickname">{{ userInfo.nickname || '未登录' }}</text>
          <view class="member-level">
            <text class="level-text">{{ memberLevelText }}</text>
            <text class="growth-value">成长值: {{ userInfo.growthValue || 0 }}</text>
          </view>
        </view>
      </view>
      
      <view class="user-stats">
        <view class="stat-item" @click="navigateTo('/pages/wallet/index')">
          <text class="stat-value">¥{{ userInfo.walletBalance || '0.00' }}</text>
          <text class="stat-label">钱包余额</text>
        </view>
        <view class="stat-item" @click="navigateTo('/pages/points/index')">
          <text class="stat-value">{{ userInfo.availablePoints || 0 }}</text>
          <text class="stat-label">可用积分</text>
        </view>
        <view class="stat-item" @click="navigateTo('/pages/coupon/index')">
          <text class="stat-value">{{ coupons || 0 }}</text>
          <text class="stat-label">优惠券</text>
        </view>
      </view>
    </view>

    <!-- 订单 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">我的订单</text>
        <text class="section-more" @click="navigateTo('/pages/order/list')">全部订单 ></text>
      </view>
      <view class="order-types">
        <view class="order-type" @click="navigateTo('/pages/order/list?status=pending')">
          <view class="type-icon">⏰</view>
          <text class="type-text">待付款</text>
        </view>
        <view class="order-type" @click="navigateTo('/pages/order/list?status=paid')">
          <view class="type-icon">🎫</view>
          <text class="type-text">待使用</text>
        </view>
        <view class="order-type" @click="navigateTo('/pages/order/list?status=completed')">
          <view class="type-icon">✅</view>
          <text class="type-text">已完成</text>
        </view>
        <view class="order-type" @click="navigateTo('/pages/order/list?status=refund')">
          <view class="type-icon">↩️</view>
          <text class="type-text">退款</text>
        </view>
      </view>
    </view>

    <!-- 功能列表 -->
    <view class="section">
      <view class="menu-list">
        <view class="menu-item" @click="navigateTo('/pages/address/list')">
          <view class="menu-left">
            <text class="menu-icon">📍</text>
            <text class="menu-text">收货地址</text>
          </view>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/profile/index')">
          <view class="menu-left">
            <text class="menu-icon">👤</text>
            <text class="menu-text">个人信息</text>
          </view>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/settings/index')">
          <view class="menu-left">
            <text class="menu-icon">⚙️</text>
            <text class="menu-text">设置</text>
          </view>
          <text class="menu-arrow">></text>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-btn" @click="handleLogout" v-if="isLogin">
      <button class="btn">退出登录</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getUserProfile } from '@/api/user'

const defaultAvatar = 'https://via.placeholder.com/150?text=Avatar'

const userInfo = ref({})
const coupons = ref(0)
const isLogin = ref(false)

const memberLevelText = computed(() => {
  const level = userInfo.value.memberLevel || 1
  const levels = ['', '普通会员', '白银会员', '黄金会员', '钻石会员']
  return levels[level] || '普通会员'
})

const loadUserInfo = async () => {
  try {
    const userId = uni.getStorageSync('userId')
    if (!userId) {
      isLogin.value = false
      return
    }
    
    const data = await getUserProfile(userId)
    userInfo.value = data
    isLogin.value = true
  } catch (error) {
    console.error('获取用户信息失败:', error)
    isLogin.value = false
  }
}

const navigateTo = (url) => {
  if (!isLogin.value) {
    uni.navigateTo({
      url: '/pages/login/index'
    })
    return
  }
  
  uni.navigateTo({ url })
}

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗?',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('token')
        uni.removeStorageSync('userId')
        isLogin.value = false
        userInfo.value = {}
        
        uni.showToast({
          title: '已退出登录',
          icon: 'success'
        })
      }
    }
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20rpx;
}

.user-card {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.user-header {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.user-info {
  flex: 1;
}

.nickname {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.member-level {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.level-text {
  font-size: 24rpx;
  color: #ff6b6b;
  background-color: #fff5f5;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.growth-value {
  font-size: 24rpx;
  color: #999;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  padding-top: 30rpx;
  border-top: 1rpx solid #f0f0f0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
}

.stat-value {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 24rpx;
  color: #999;
}

.section {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 20rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.section-more {
  font-size: 26rpx;
  color: #999;
}

.order-types {
  display: flex;
  justify-content: space-around;
}

.order-type {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
}

.type-icon {
  font-size: 48rpx;
}

.type-text {
  font-size: 24rpx;
  color: #666;
}

.menu-list {
  display: flex;
  flex-direction: column;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.menu-icon {
  font-size: 40rpx;
}

.menu-text {
  font-size: 30rpx;
  color: #333;
}

.menu-arrow {
  font-size: 30rpx;
  color: #ccc;
}

.logout-btn {
  margin: 40rpx;
}

.btn {
  width: 100%;
  background-color: #ff4d4f;
  color: #fff;
  border-radius: 50rpx;
  font-size: 32rpx;
  padding: 24rpx 0;
}
</style>
