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
            <el-option label="发放中" :value="1" />
            <el-option label="停用" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
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

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialog.visible" title="编辑优惠券" width="600px">
      <el-form :model="editDialog.form" label-width="120px">
        <el-form-item label="优惠券名称">
          <el-input v-model="editDialog.form.name" />
        </el-form-item>
        <el-form-item label="优惠券类型">
          <el-select v-model="editDialog.form.couponType" disabled>
            <el-option label="满减券" :value="1" />
            <el-option label="折扣券" :value="2" />
            <el-option label="代金券" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="发放总量">
          <el-input-number v-model="editDialog.form.totalCount" :min="0" />
        </el-form-item>
        <el-form-item label="最低消费金额">
          <el-input-number v-model="editDialog.form.minAmount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="有效期开始">
          <el-date-picker
            v-model="editDialog.form.validFrom"
            type="datetime"
            placeholder="选择日期时间"
          />
        </el-form-item>
        <el-form-item label="有效期结束">
          <el-date-picker
            v-model="editDialog.form.validTo"
            type="datetime"
            placeholder="选择日期时间"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 发放对话框 -->
    <el-dialog v-model="grantDialog.visible" title="发放优惠券" width="500px">
      <el-alert
        :title="`将发放优惠券: ${grantDialog.couponName}`"
        type="info"
        :closable="false"
        style="margin-bottom: 20px"
      />
      <el-form :model="grantDialog.form" label-width="100px">
        <el-form-item label="用户ID列表">
          <el-input
            v-model="grantDialog.userIdsInput"
            type="textarea"
            :rows="5"
            placeholder="请输入用户ID，多个用户用逗号或换行分隔，例如：1,2,3 或每行一个ID"
          />
        </el-form-item>
        <el-form-item>
          <el-text type="info" size="small">
            支持批量发放，将自动过滤无效的ID
          </el-text>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="grantDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveGrant">确定发放</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { getCoupons, updateCouponStatus } from '@/api/coupon'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'

const loading = ref(false)

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

// 编辑对话框
const editDialog = ref({
  visible: false,
  form: {}
})

// 发放对话框
const grantDialog = ref({
  visible: false,
  couponId: null,
  couponName: '',
  form: {
    userIds: []
  },
  userIdsInput: '' // 用于输入用户ID列表
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

const handleSearch = async () => {
  loading.value = true
  try {
    const params = {
      name: searchForm.value.name,
      type: searchForm.value.couponType,
      status: searchForm.value.status,
      page: pagination.value.page - 1,
      size: pagination.value.size
    }
    
    const res = await getCoupons(params)
    
    if (res.success && res.data) {
      tableData.value = res.data.records || []
      pagination.value.total = res.data.total || 0
    } else {
      tableData.value = []
      pagination.value.total = 0
      ElMessage.error(res.message || '获取数据失败')
    }
  } catch (error) {
    console.error('查询优惠券列表失败:', error)
    ElMessage.error('查询失败：' + (error.message || '未知错误'))
    tableData.value = []
    pagination.value.total = 0
  } finally {
    loading.value = false
  }
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
  editDialog.value.visible = true
  editDialog.value.form = { ...row }
}

const handleGrant = (row) => {
  grantDialog.value.visible = true
  grantDialog.value.couponId = row.id
  grantDialog.value.couponName = row.name
  grantDialog.value.form = {
    userIds: []
  }
}

const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  const newStatus = row.status === 1 ? 3 : 1
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
    await updateCouponStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    handleSearch()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败：` + (error.message || '未知错误'))
    }
  }
}

// 保存编辑
const handleSaveEdit = async () => {
  try {
    const data = {
      name: editDialog.value.form.name,
      totalCount: editDialog.value.form.totalCount,
      minAmount: editDialog.value.form.minAmount,
      validFrom: editDialog.value.form.validFrom,
      validTo: editDialog.value.form.validTo
    }
    await updateCoupon(editDialog.value.form.id, data)
    ElMessage.success('编辑成功')
    editDialog.value.visible = false
    handleSearch()
  } catch (error) {
    ElMessage.error('编辑失败：' + (error.message || '未知错误'))
  }
}

// 保存发放
const handleSaveGrant = async () => {
  try {
    // 解析用户ID列表
    const input = grantDialog.value.userIdsInput.trim()
    if (!input) {
      ElMessage.warning('请输入用户ID')
      return
    }
    
    // 支持逗号和换行分隔
    const userIds = input
      .split(/[,\n\s]+/)
      .map(id => id.trim())
      .filter(id => id && /^\d+$/.test(id))
      .map(id => parseInt(id))
    
    if (userIds.length === 0) {
      ElMessage.warning('没有有效的用户ID')
      return
    }
    
    // 批量发放
    let successCount = 0
    let failCount = 0
    
    for (const userId of userIds) {
      try {
        await grantCoupon(grantDialog.value.couponId, { userId })
        successCount++
      } catch (error) {
        failCount++
        console.error(`发放给用户${userId}失败:`, error)
      }
    }
    
    if (successCount > 0) {
      ElMessage.success(`成功发放${successCount}个用户${failCount > 0 ? `，失败${failCount}个` : ''}`)
      grantDialog.value.visible = false
      grantDialog.value.userIdsInput = ''
    } else {
      ElMessage.error('发放失败，请检查用户ID是否有效')
    }
  } catch (error) {
    ElMessage.error('发放失败：' + (error.message || '未知错误'))
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
