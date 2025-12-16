<template>
  <div class="wallet-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户钱包管理</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="钱包状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="冻结" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="钱包ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="balance" label="可用余额" width="120">
          <template #default="scope">
            <span class="amount">¥{{ scope.row.balance }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="frozenBalance" label="冻结金额" width="120">
          <template #default="scope">
            <span class="amount">¥{{ scope.row.frozenBalance }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalDeposit" label="累计充值" width="120">
          <template #default="scope">
            <span class="amount">¥{{ scope.row.totalDeposit }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalConsumption" label="累计消费" width="120">
          <template #default="scope">
            <span class="amount">¥{{ scope.row.totalConsumption }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '冻结' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleViewTransactions(scope.row)">
              查看流水
            </el-button>
            <el-button 
              :type="scope.row.status === 1 ? 'danger' : 'success'" 
              size="small"
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '冻结' : '解冻' }}
            </el-button>
          </template>
        </el-table-column>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const searchForm = ref({
  userId: '',
  phone: '',
  status: null
})

const tableData = ref([])

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const handleSearch = () => {
  // TODO: 调用钱包列表API
  console.log('搜索条件:', searchForm.value)
  // 模拟数据
  tableData.value = []
  pagination.value.total = 0
}

const handleReset = () => {
  searchForm.value = {
    userId: '',
    phone: '',
    status: null
  }
  handleSearch()
}

const handleViewTransactions = (row) => {
  router.push({
    path: '/wallet/transactions',
    query: { userId: row.userId }
  })
}

const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '冻结' : '解冻'
  try {
    await ElMessageBox.confirm(
      `确定要${action}该钱包吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // TODO: 调用冻结/解冻API
    ElMessage.success(`${action}成功`)
    handleSearch()
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.wallet-list {
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
</style>
