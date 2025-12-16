<template>
  <div class="points-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户积分管理</span>
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
              <div class="stat-label">总用户数</div>
              <div class="stat-value">{{ stats.totalUsers }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-label">总积分</div>
              <div class="stat-value text-success">{{ stats.totalPoints }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-label">今日发放</div>
              <div class="stat-value text-primary">{{ stats.todayEarned }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-label">今日消耗</div>
              <div class="stat-value text-danger">{{ stats.todaySpent }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="currentBalance" label="当前积分" width="120">
          <template #default="scope">
            <span class="points-balance">{{ scope.row.currentBalance }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalEarned" label="累计获得" width="120" />
        <el-table-column prop="totalSpent" label="累计消耗" width="120" />
        <el-table-column prop="lastEarnTime" label="最近获得时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleViewHistory(scope.row)">
              查看流水
            </el-button>
            <el-button type="success" size="small" @click="handleAdjust(scope.row)">
              调整积分
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

    <!-- 调整积分对话框 -->
    <el-dialog v-model="adjustDialog.visible" title="调整积分" width="500px">
      <el-form :model="adjustDialog.form" label-width="100px">
        <el-form-item label="用户ID">
          <el-input v-model="adjustDialog.form.userId" disabled />
        </el-form-item>
        <el-form-item label="当前积分">
          <el-input v-model="adjustDialog.form.currentBalance" disabled />
        </el-form-item>
        <el-form-item label="调整类型">
          <el-radio-group v-model="adjustDialog.form.type">
            <el-radio :label="1">增加</el-radio>
            <el-radio :label="2">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整积分">
          <el-input-number v-model="adjustDialog.form.points" :min="1" :max="100000" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="adjustDialog.form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAdjust">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const searchForm = ref({
  userId: '',
  phone: ''
})

const stats = ref({
  totalUsers: 0,
  totalPoints: 0,
  todayEarned: 0,
  todaySpent: 0
})

const tableData = ref([])

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const adjustDialog = ref({
  visible: false,
  form: {
    userId: '',
    currentBalance: 0,
    type: 1,
    points: 0,
    remark: ''
  }
})

const handleSearch = () => {
  // TODO: 调用积分列表API
  console.log('搜索条件:', searchForm.value)
  tableData.value = []
  pagination.value.total = 0
}

const handleReset = () => {
  searchForm.value = {
    userId: '',
    phone: ''
  }
  handleSearch()
}

const handleViewHistory = (row) => {
  router.push({
    path: '/points/transactions',
    query: { userId: row.userId }
  })
}

const handleAdjust = (row) => {
  adjustDialog.value.form = {
    userId: row.userId,
    currentBalance: row.currentBalance,
    type: 1,
    points: 0,
    remark: ''
  }
  adjustDialog.value.visible = true
}

const handleConfirmAdjust = () => {
  // TODO: 调用调整积分API
  ElMessage.success('调整成功')
  adjustDialog.value.visible = false
  handleSearch()
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.points-list {
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

.text-success {
  color: #67C23A !important;
}

.text-primary {
  color: #409EFF !important;
}

.text-danger {
  color: #F56C6C !important;
}

.points-balance {
  color: #E6A23C;
  font-weight: bold;
  font-size: 16px;
}
</style>
