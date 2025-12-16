<template>
  <div class="transaction-list">
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
        <el-form-item label="流水号">
          <el-input v-model="searchForm.transactionNo" placeholder="请输入流水号" clearable />
        </el-form-item>
        <el-form-item label="交易类型">
          <el-select v-model="searchForm.transactionType" placeholder="请选择类型" clearable>
            <el-option label="充值" :value="1" />
            <el-option label="消费" :value="2" />
            <el-option label="退款" :value="3" />
            <el-option label="提现" :value="4" />
            <el-option label="冻结" :value="5" />
            <el-option label="解冻" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
            <el-option label="处理中" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="transactionNo" label="流水号" width="180" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="transactionType" label="交易类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.transactionType)">
              {{ getTypeName(scope.row.transactionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="变动金额" width="120">
          <template #default="scope">
            <span :class="scope.row.amount >= 0 ? 'amount-positive' : 'amount-negative'">
              {{ scope.row.amount >= 0 ? '+' : '' }}¥{{ scope.row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="balanceAfter" label="变动后余额" width="120">
          <template #default="scope">
            <span class="amount">¥{{ scope.row.balanceAfter }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusName(scope.row.status) }}
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
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const searchForm = ref({
  userId: route.query.userId || '',
  transactionNo: '',
  transactionType: null,
  status: null
})

const tableData = ref([])

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const typeMap = {
  1: '充值',
  2: '消费',
  3: '退款',
  4: '提现',
  5: '冻结',
  6: '解冻'
}

const statusMap = {
  0: '失败',
  1: '成功',
  2: '处理中'
}

const getTypeName = (type) => typeMap[type] || '未知'

const getTypeTagType = (type) => {
  const map = {
    1: 'success',
    2: 'danger',
    3: 'warning',
    4: 'info',
    5: 'danger',
    6: 'success'
  }
  return map[type] || ''
}

const getStatusName = (status) => statusMap[status] || '未知'

const getStatusTagType = (status) => {
  const map = {
    0: 'danger',
    1: 'success',
    2: 'warning'
  }
  return map[status] || ''
}

const handleSearch = () => {
  // TODO: 调用钱包流水API
  console.log('搜索条件:', searchForm.value)
  tableData.value = []
  pagination.value.total = 0
}

const handleReset = () => {
  searchForm.value = {
    userId: route.query.userId || '',
    transactionNo: '',
    transactionType: null,
    status: null
  }
  handleSearch()
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.transaction-list {
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

.amount-positive {
  color: #67C23A;
  font-weight: bold;
}

.amount-negative {
  color: #F56C6C;
  font-weight: bold;
}
</style>
