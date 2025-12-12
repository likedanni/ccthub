<template>
  <view class="container">
    <!-- 轮播图 -->
    <view class="banner">
      <swiper class="swiper" indicator-dots autoplay interval="3000" duration="500">
        <swiper-item v-for="(item, index) in banners" :key="index">
          <image :src="item" class="banner-image" mode="aspectFill" />
        </swiper-item>
      </swiper>
    </view>

    <!-- 快捷入口 -->
    <view class="quick-entry">
      <view class="entry-item" v-for="(item, index) in entries" :key="index" @click="handleEntryClick(item)">
        <view class="entry-icon">{{ item.icon }}</view>
        <text class="entry-text">{{ item.name }}</text>
      </view>
    </view>

    <!-- 热门景区 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">热门景区</text>
        <text class="section-more">更多 ></text>
      </view>
      <view class="scenic-list">
        <view class="scenic-item" v-for="(item, index) in scenicSpots" :key="index" @click="handleScenicClick(item)">
          <image :src="item.image" class="scenic-image" mode="aspectFill" />
          <view class="scenic-info">
            <text class="scenic-name">{{ item.name }}</text>
            <text class="scenic-price">¥{{ item.price }}起</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 最新活动 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">最新活动</text>
        <text class="section-more">更多 ></text>
      </view>
      <view class="activity-list">
        <view class="activity-item" v-for="(item, index) in activities" :key="index" @click="handleActivityClick(item)">
          <image :src="item.image" class="activity-image" mode="aspectFill" />
          <view class="activity-info">
            <text class="activity-title">{{ item.title }}</text>
            <text class="activity-time">{{ item.time }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const banners = ref([
  'https://via.placeholder.com/750x400?text=Banner+1',
  'https://via.placeholder.com/750x400?text=Banner+2',
  'https://via.placeholder.com/750x400?text=Banner+3'
])

const entries = ref([
  { id: 1, name: '景区门票', icon: '🎫', path: '/pages/ticket/list' },
  { id: 2, name: '特色餐饮', icon: '🍜', path: '/pages/food/list' },
  { id: 3, name: '文创产品', icon: '🎨', path: '/pages/cultural/list' },
  { id: 4, name: '打卡活动', icon: '📍', path: '/pages/activity/list' }
])

const scenicSpots = ref([
  { id: 1, name: '太行山大峡谷', price: 88, image: 'https://via.placeholder.com/300x200?text=Scenic+1' },
  { id: 2, name: '八泉峡', price: 68, image: 'https://via.placeholder.com/300x200?text=Scenic+2' },
  { id: 3, name: '太行水乡', price: 58, image: 'https://via.placeholder.com/300x200?text=Scenic+3' }
])

const activities = ref([
  { id: 1, title: '春季踏青活动', time: '2025-03-01 至 2025-03-31', image: 'https://via.placeholder.com/200x200?text=Activity+1' },
  { id: 2, title: '文化节庆', time: '2025-04-01 至 2025-04-07', image: 'https://via.placeholder.com/200x200?text=Activity+2' }
])

const handleEntryClick = (item) => {
  uni.navigateTo({
    url: item.path
  })
}

const handleScenicClick = (item) => {
  uni.navigateTo({
    url: `/pages/scenic/detail?id=${item.id}`
  })
}

const handleActivityClick = (item) => {
  uni.navigateTo({
    url: `/pages/activity/detail?id=${item.id}`
  })
}
</script>

<style scoped>
.container {
  padding-bottom: 20rpx;
}

.banner {
  width: 100%;
  height: 400rpx;
}

.swiper {
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.quick-entry {
  display: flex;
  justify-content: space-around;
  padding: 40rpx 0;
  background-color: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.entry-icon {
  font-size: 60rpx;
  margin-bottom: 10rpx;
}

.entry-text {
  font-size: 26rpx;
  color: #333;
}

.section {
  margin: 20rpx;
  background-color: #fff;
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

.scenic-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.scenic-item {
  width: calc(50% - 10rpx);
}

.scenic-image {
  width: 100%;
  height: 200rpx;
  border-radius: 12rpx;
}

.scenic-info {
  margin-top: 10rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.scenic-name {
  font-size: 28rpx;
  color: #333;
}

.scenic-price {
  font-size: 28rpx;
  color: #ff6b6b;
  font-weight: bold;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.activity-item {
  display: flex;
  gap: 20rpx;
}

.activity-image {
  width: 200rpx;
  height: 150rpx;
  border-radius: 12rpx;
}

.activity-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.activity-title {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
}

.activity-time {
  font-size: 24rpx;
  color: #999;
}
</style>
