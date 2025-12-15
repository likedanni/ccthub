<template>
  <div class="ticket-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="景区">
          <el-select
            v-model="searchForm.scenicSpotId"
            placeholder="请选择景区"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="spot in scenicSpots"
              :key="spot.id"
              :label="spot.name"
              :value="spot.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="票种名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入票种名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="已上架" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search"
            >搜索</el-button
          >
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>票种列表</span>
          <el-button type="primary" @click="handleCreate" :icon="Plus"
            >创建票种</el-button
          >
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="scenicSpotName" label="景区" width="150" />
        <el-table-column prop="name" label="票种名称" width="200" />
        <el-table-column prop="typeText" label="类型" width="100" />
        <el-table-column prop="validityTypeText" label="有效期类型" width="120" />
        <el-table-column prop="sellPrice" label="售价" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.minPrice || '未设置' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handlePrices(row)"
              :icon="Calendar"
              >票价设置</el-button
            >
            <el-button
              type="warning"
              size="small"
              @click="handleEdit(row)"
              :icon="Edit"
              >编辑</el-button
            >
            <el-button
              v-if="row.status === 1"
              type="info"
              size="small"
              @click="handleStatusChange(row, 0)"
              >下架</el-button
            >
            <el-button
              v-else
              type="success"
              size="small"
              @click="handleStatusChange(row, 1)"
              >上架</el-button
            >
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
              :icon="Delete"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { getScenicSpots } from '@/api/scenic'
import {
    deleteTicket,
    getTickets,
    updateTicketStatus
} from '@/api/ticket'
import {
    Calendar,
    Delete,
    Edit,
    Plus,
    Refresh,
    Search
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const searchForm = reactive({
  scenicSpotId: null,
  name: '',
  status: null
})

const tableData = ref([])
const scenicSpots = ref([])
const loading = ref(false)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 加载景区列表
const loadScenicSpots = async () => {
  try {
    const res = await getScenicSpots({ size: 1000 })
    // ApiResponse格式: {code, message, data: {content, total, ...}}
    scenicSpots.value = res.data.content || []
  } catch (error) {
    ElMessage.error('加载景区列表失败')
  }
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      ...searchForm
    }
    const res = await getTickets(params)
    tableData.value = res.content || []
    pagination.total = res.totalElements || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    scenicSpotId: null,
    name: '',
    status: null
  })
  handleSearch()
}

// 创建
const handleCreate = () => {
  router.push('/tickets/create')
}

// 编辑
const handleEdit = (row) => {
  router.push(`/tickets/edit/${row.id}`)
}

// 票价设置
const handlePrices = (row) => {
  router.push(`/tickets/prices/${row.id}`)
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除票种"${row.name}"吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteTicket(row.id)
        ElMessage.success('删除成功')
        loadData()
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 状态变更
const handleStatusChange = (row, status) => {
  const action = status === 1 ? '上架' : '下架'
  ElMessageBox.confirm(`确定要${action}票种"${row.name}"吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await updateTicketStatus(row.id, status)
        ElMessage.success(`${action}成功`)
        loadData()
      } catch (error) {
        ElMessage.error(`${action}失败`)
      }
    })
    .catch(() => {})
}

// 分页
const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadData()
}

onMounted(() => {
  loadScenicSpots()
  loadData()
})
</script>

<style scoped>
.ticket-list {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
