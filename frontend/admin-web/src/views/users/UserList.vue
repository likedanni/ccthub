<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="handleRefresh">
            <el-icon>
              <Refresh />
            </el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="激活" value="ACTIVE" />
            <el-option label="禁用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column label="会员等级" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.memberLevel === 'BRONZE'" type="info">青铜</el-tag>
            <el-tag v-else-if="row.memberLevel === 'SILVER'">白银</el-tag>
            <el-tag v-else-if="row.memberLevel === 'GOLD'" type="warning">黄金</el-tag>
            <el-tag v-else-if="row.memberLevel === 'PLATINUM'" type="danger">铂金</el-tag>
            <el-tag v-else-if="row.memberLevel === 'DIAMOND'" type="danger">钻石</el-tag>
            <el-tag v-else type="info">{{ row.memberLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalPoints" label="积分" width="100" />
        <el-table-column label="钱包余额(元)" width="120">
          <template #default="{ row }">
            ¥{{ row.walletBalance || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'ACTIVE'" type="success">激活</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="注册时间" width="180" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button :type="row.status === 'ACTIVE' ? 'danger' : 'success'" size="small"
              @click="handleToggleStatus(row)">
              {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize"
        :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange" @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: flex-end" />
    </el-card>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="dialogVisible" title="用户详情" width="600px">
      <el-descriptions v-if="currentUser" :column="2" border>
        <el-descriptions-item label="ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentUser.realName || '未认证' }}</el-descriptions-item>
        <el-descriptions-item label="会员等级">{{ currentUser.memberLevel }}</el-descriptions-item>
        <el-descriptions-item label="成长值">{{ currentUser.growthValue }}</el-descriptions-item>
        <el-descriptions-item label="总积分">{{ currentUser.totalPoints }}</el-descriptions-item>
        <el-descriptions-item label="可用积分">{{ currentUser.availablePoints }}</el-descriptions-item>
        <el-descriptions-item label="钱包余额">¥{{ currentUser.walletBalance }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentUser.status }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{ currentUser.registerTime }}</el-descriptions-item>
        <el-descriptions-item label="最后登录" :span="2">{{ currentUser.lastLoginTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { getUserList, getUserProfile, updateUserStatus } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const dialogVisible = ref(false)
const currentUser = ref(null)

const searchForm = reactive({
  phone: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1, // 后端从0开始
      pageSize: pagination.pageSize,
      phone: searchForm.phone || undefined,
      status: searchForm.status || undefined
    }

    const data = await getUserList(params)
    tableData.value = data.content
    pagination.total = data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.phone = ''
  searchForm.status = ''
  handleSearch()
}

const handleRefresh = () => {
  loadData()
}

const handleView = async (row) => {
  try {
    const data = await getUserProfile(row.id)
    currentUser.value = data
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户详情失败')
  }
}

const handleToggleStatus = async (row) => {
  const action = row.status === 'ACTIVE' ? '禁用' : '启用'
  const newStatus = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'

  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleSizeChange = () => {
  loadData()
}

const handlePageChange = () => {
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-list {
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
</style>
