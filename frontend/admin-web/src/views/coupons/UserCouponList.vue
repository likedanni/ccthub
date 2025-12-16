<template>
  <div class="user-coupon-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户优惠券</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>
        <el-form-item label="优惠券名称">
          <el-input v-model="searchForm.couponName" placeholder="请输入优惠券名称" clearable />
        </el-form-item>
        <el-form-item label="使用状态">
          <el-select v-model="searchForm.useStatus" placeholder="请选择状态" clearable>
            <el-option label="未使用" :value="0" />
            <el-option label="已使用" :value="1" />
            <el-option label="已过期" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 统计卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-label">未使用</div>
              <div class="stat-value text-primary">{{ stats.unused }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-label">已使用</div>
              <div class="stat-value text-success">{{ stats.used }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-label">已过期</div>
              <div class="stat-value text-danger">{{ stats.expired }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-label">总计</div>
              <div class="stat-value">{{ stats.total }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="couponName" label="优惠券名称" width="180" />
        <el-table-column prop="couponType" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.couponType)">
              {{ getTypeName(scope.row.couponType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="discount" label="优惠" width="120">
          <template #default="scope">
            {{ formatDiscount(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column prop="receiveTime" label="领取时间" width="160" />
        <el-table-column prop="validTo" label="有效期至" width="160" />
        <el-table-column prop="useStatus" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.useStatus)">
              {{ getStatusName(scope.row.useStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="useTime" label="使用时间" width="160" />
        <el-table-column prop="orderNo" label="使用订单" width="180" />
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const searchForm = ref({
  userId: '',
  couponName: '',
  useStatus: null
})

const stats = ref({
  unused: 0,
  used: 0,
  expired: 0,
  total: 0
})

const tableData = ref([])

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const typeMap = {
  1: '满减券',
  2: '折扣券',
  3: '代金券'
}

const statusMap = {
  0: '未使用',
  1: '已使用',
  2: '已过期'
}

const getTypeName = (type) => typeMap[type] || '未知'

const getTypeTagType = (type) => {
  const map = {
    1: 'success',
    2: 'warning',
    3: 'primary'
  }
  return map[type] || ''
}

const getStatusName = (status) => statusMap[status] || '未知'

const getStatusTagType = (status) => {
  const map = {
    0: 'primary',
    1: 'success',
    2: 'info'
  }
  return map[status] || ''
}

const formatDiscount = (row) => {
  if (row.couponType === 1) {
    return `减${row.discountAmount}元`
  } else if (row.couponType === 2) {
    return `${row.discountRate}折`
  } else if (row.couponType === 3) {
    return `${row.discountAmount}元`
  }
  return '-'
}

const handleSearch = () => {
  // TODO: 调用用户优惠券列表API
  console.log('搜索条件:', searchForm.value)
  tableData.value = []
  pagination.value.total = 0
  stats.value = {
    unused: 0,
    used: 0,
    expired: 0,
    total: 0
  }
}

const handleReset = () => {
  searchForm.value = {
    userId: '',
    couponName: '',
    useStatus: null
  }
  handleSearch()
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.user-coupon-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.text-primary {
  color: #409EFF !important;
}

.text-success {
  color: #67C23A !important;
}

.text-danger {
  color: #F56C6C !important;
}
</style>
