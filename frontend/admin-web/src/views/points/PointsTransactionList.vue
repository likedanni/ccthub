<template>
  <div class="points-transaction-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>积分流水</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>
        <el-form-item label="积分来源">
          <el-select v-model="searchForm.source" placeholder="请选择来源" clearable>
            <el-option label="注册赠送" value="register" />
            <el-option label="签到" value="sign_in" />
            <el-option label="订单消费" value="order" />
            <el-option label="分享" value="share" />
            <el-option label="活动奖励" value="activity" />
            <el-option label="积分兑换" value="exchange" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="searchForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="searchForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="changeType" label="变动类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.changeType === 1 ? 'success' : 'warning'">
              {{ scope.row.changeType === 1 ? '增加' : '减少' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="120">
          <template #default="scope">
            {{ getSourceName(scope.row.source) }}
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="100">
          <template #default="scope">
            <span :class="scope.row.changeType === 1 ? 'points-income' : 'points-expense'">
              {{ formatPoints(scope.row.points, scope.row.changeType) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="currentBalance" label="当前余额" width="120" />
        <el-table-column prop="orderNo" label="关联订单" width="180" />
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column prop="expiresAt" label="过期时间" width="160" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '有效' : '已过期' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
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
import { getPointsHistory } from '@/api/points'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const searchForm = ref({
  userId: route.query.userId || '',
  source: '',
  startTime: '',
  endTime: ''
})

const tableData = ref([])
const loading = ref(false)

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const sourceMap = {
  register: '注册赠送',
  sign_in: '签到',
  order: '订单消费',
  share: '分享',
  activity: '活动奖励',
  exchange: '积分兑换'
}

const getSourceName = (source) => sourceMap[source] || source || '未知'

const formatPoints = (points, changeType) => {
  const prefix = changeType === 1 ? '+' : '-'
  return `${prefix}${points}`
}

const handleSearch = async () => {
  if (!searchForm.value.userId) {
    ElMessage.warning('请输入用户ID')
    return
  }

  loading.value = true
  try {
    const params = {
      source: searchForm.value.source,
      startTime: searchForm.value.startTime,
      endTime: searchForm.value.endTime,
      page: pagination.value.page - 1,
      size: pagination.value.size
    }

    const res = await getPointsHistory(searchForm.value.userId, params)
    
    if (res.success && res.data) {
      tableData.value = res.data.records || []
      pagination.value.total = res.data.total || 0
    } else {
      tableData.value = []
      pagination.value.total = 0
      ElMessage.error(res.message || '获取数据失败')
    }
  } catch (error) {
    console.error('查询积分流水失败:', error)
    ElMessage.error('查询失败：' + (error.message || '未知错误'))
    tableData.value = []
    pagination.value.total = 0
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.value = {
    userId: route.query.userId || '',
    source: '',
    startTime: '',
    endTime: ''
  }
  pagination.value.page = 1
  handleSearch()
}

onMounted(() => {
  if (searchForm.value.userId) {
    handleSearch()
  }
})
</script>

<style scoped>
.points-transaction-list {
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

.points-income {
  color: #67C23A;
  font-weight: bold;
}

.points-expense {
  color: #F56C6C;
  font-weight: bold;
}
</style>
