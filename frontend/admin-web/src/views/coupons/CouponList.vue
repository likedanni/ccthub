<template>
  <div class="coupon-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>优惠券管理</span>
          <el-button type="primary" @click="handleCreate">创建优惠券</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="优惠券名称">
          <el-input v-model="searchForm.name" placeholder="请输入优惠券名称" clearable />
        </el-form-item>
        <el-form-item label="优惠券类型">
          <el-select v-model="searchForm.couponType" placeholder="请选择类型" clearable>
            <el-option label="满减券" :value="1" />
            <el-option label="折扣券" :value="2" />
            <el-option label="代金券" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="生效中" :value="1" />
            <el-option label="已失效" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="优惠券名称" width="180" />
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
        <el-table-column prop="minAmount" label="最低消费" width="100">
          <template #default="scope">
            {{ scope.row.minAmount ? `¥${scope.row.minAmount}` : '无门槛' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="发放总量" width="100" />
        <el-table-column prop="usedCount" label="已使用" width="100" />
        <el-table-column prop="validFrom" label="有效期开始" width="160" />
        <el-table-column prop="validTo" label="有效期结束" width="160" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '生效中' : '已失效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleGrant(scope.row)">发放</el-button>
            <el-button 
              :type="scope.row.status === 1 ? 'danger' : 'success'" 
              size="small"
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = ref({
  name: '',
  couponType: null,
  status: null
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

const getTypeName = (type) => typeMap[type] || '未知'

const getTypeTagType = (type) => {
  const map = {
    1: 'success',
    2: 'warning',
    3: 'primary'
  }
  return map[type] || ''
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
  // TODO: 调用优惠券列表API
  console.log('搜索条件:', searchForm.value)
  tableData.value = []
  pagination.value.total = 0
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    couponType: null,
    status: null
  }
  handleSearch()
}

const handleCreate = () => {
  ElMessage.info('创建优惠券功能待开发')
  // TODO: 跳转到创建页面或打开对话框
}

const handleEdit = (row) => {
  ElMessage.info('编辑优惠券功能待开发')
  // TODO: 跳转到编辑页面或打开对话框
}

const handleGrant = (row) => {
  ElMessage.info('发放优惠券功能待开发')
  // TODO: 打开发放对话框
}

const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(
      `确定要${action}该优惠券吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // TODO: 调用禁用/启用API
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
.coupon-list {
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
