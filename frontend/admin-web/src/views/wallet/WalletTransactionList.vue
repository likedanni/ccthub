<template>
  <div class="wallet-transaction-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>钱包流水</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>
        <el-form-item label="交易类型">
          <el-select v-model="searchForm.transactionType" placeholder="请选择类型" clearable>
            <el-option label="充值" :value="1" />
            <el-option label="消费" :value="2" />
            <el-option label="退款" :value="3" />
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
        <el-table-column prop="transactionNo" label="流水号" width="180" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="transactionType" label="交易类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.transactionType)">
              {{ getTypeName(scope.row.transactionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="交易金额" width="120">
          <template #default="scope">
            <span :class="getAmountClass(scope.row.transactionType)">
              {{ formatAmount(scope.row.amount, scope.row.transactionType) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="balanceAfter" label="交易后余额" width="120">
          <template #default="scope">
            <span class="amount">¥{{ scope.row.balanceAfter }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="关联订单" width="180" />
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '成功' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="交易时间" width="160" />
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
import { getWalletTransactions } from '@/api/wallet'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const searchForm = ref({
  userId: route.query.userId || '',
  transactionType: null,
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

const typeMap = {
  1: '充值',
  2: '消费',
  3: '退款'
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

const getAmountClass = (type) => {
  return type === 1 || type === 3 ? 'amount-income' : 'amount-expense'
}

const formatAmount = (amount, type) => {
  const prefix = type === 1 || type === 3 ? '+' : '-'
  return `${prefix}¥${amount}`
}

const handleSearch = async () => {
  if (!searchForm.value.userId) {
    ElMessage.warning('请输入用户ID')
    return
  }

  loading.value = true
  try {
    const params = {
      transactionType: searchForm.value.transactionType,
      startTime: searchForm.value.startTime,
      endTime: searchForm.value.endTime,
      page: pagination.value.page - 1,
      size: pagination.value.size
    }

    const res = await getWalletTransactions(searchForm.value.userId, params)
    
    if (res.success && res.data) {
      tableData.value = res.data.content || []
      pagination.value.total = res.data.totalElements || 0
    } else {
      tableData.value = []
      pagination.value.total = 0
      ElMessage.error(res.message || '获取数据失败')
    }
  } catch (error) {
    console.error('查询钱包流水失败:', error)
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
    transactionType: null,
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
.wallet-transaction-list {
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

.amount {
  color: #67C23A;
  font-weight: bold;
}

.amount-income {
  color: #67C23A;
  font-weight: bold;
}

.amount-expense {
  color: #F56C6C;
  font-weight: bold;
}
</style>
