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
            <el-option label="订单支付" value="order_pay" />
            <el-option label="每日签到" value="daily_checkin" />
            <el-option label="邀请好友" value="invite" />
            <el-option label="积分兑换" value="exchange" />
            <el-option label="后台调整" value="admin_adjust" />
          </el-select>
        </el-form-item>
        <el-form-item label="变动类型">
          <el-select v-model="searchForm.changeType" placeholder="请选择类型" clearable>
            <el-option label="增加" :value="1" />
            <el-option label="减少" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="有效" :value="1" />
            <el-option label="无效" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="流水ID" width="80" />
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
        <el-table-column prop="points" label="变动积分" width="100">
          <template #default="scope">
            <span :class="scope.row.points >= 0 ? 'points-positive' : 'points-negative'">
              {{ scope.row.points >= 0 ? '+' : '' }}{{ scope.row.points }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="currentBalance" label="变动后余额" width="120">
          <template #default="scope">
            <span class="points-balance">{{ scope.row.currentBalance }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="订单号" width="180" show-overflow-tooltip />
        <el-table-column prop="expiresAt" label="过期时间" width="160" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '有效' : '无效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
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
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const searchForm = ref({
  userId: route.query.userId || '',
  source: '',
  changeType: null,
  status: null
})

const tableData = ref([])

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const sourceMap = {
  'order_pay': '订单支付',
  'daily_checkin': '每日签到',
  'invite': '邀请好友',
  'exchange': '积分兑换',
  'admin_adjust': '后台调整'
}

const getSourceName = (source) => sourceMap[source] || source

const handleSearch = () => {
  // TODO: 调用积分流水API
  console.log('搜索条件:', searchForm.value)
  tableData.value = []
  pagination.value.total = 0
}

const handleReset = () => {
  searchForm.value = {
    userId: route.query.userId || '',
    source: '',
    changeType: null,
    status: null
  }
  handleSearch()
}

onMounted(() => {
  handleSearch()
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

.points-balance {
  color: #E6A23C;
  font-weight: bold;
}

.points-positive {
  color: #67C23A;
  font-weight: bold;
}

.points-negative {
  color: #F56C6C;
  font-weight: bold;
}
</style>
