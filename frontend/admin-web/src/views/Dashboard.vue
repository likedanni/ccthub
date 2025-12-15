<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="注册用户数" :value="stats.totalUsers">
            <template #prefix>
              <el-icon style="color: #409EFF">
                <User />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日活跃" :value="stats.activeToday">
            <template #prefix>
              <el-icon style="color: #67C23A">
                <TrendCharts />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本月订单" :value="stats.monthlyOrders">
            <template #prefix>
              <el-icon style="color: #E6A23C">
                <ShoppingCart />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="本月收入(元)" :value="stats.monthlyRevenue" :precision="2">
            <template #prefix>
              <el-icon style="color: #F56C6C">
                <Money />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>系统概览</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="运行状态">
              <el-tag type="success">正常</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Boot 3.1.4</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { getDashboardStats } from '@/api/user'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'

const stats = ref({
  totalUsers: 0,
  activeToday: 0,
  monthlyOrders: 0,
  monthlyRevenue: 0
})

const loadStats = async () => {
  try {
    const data = await getDashboardStats()
    stats.value = data
  } catch (error) {
    ElMessage.error('获取统计数据失败')
    console.error(error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}
</style>
