<template>
  <div class="order-list">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待支付" value="PENDING_PAYMENT" />
            <el-option label="待使用" value="PENDING_USE" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="已退款" value="REFUNDED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 订单列表 -->
      <el-table :data="orderList" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="contactName" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="visitDate" label="游玩日期" width="120" />
        <el-table-column prop="visitorCount" label="人数" width="80" />
        <el-table-column prop="actualAmount" label="实付金额" width="100">
          <template #default="{ row }">
            ¥{{ row.actualAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button 
              v-if="row.status === 'PENDING_USE'" 
              link 
              type="success" 
              @click="handleBatchVerify(row)">
              批量核销
            </el-button>
            <el-button 
              v-if="row.status === 'PENDING_PAYMENT'" 
              link 
              type="danger" 
              @click="handleCancel(row)">
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="70%">
      <div v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentOrder.contactName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="游玩日期">{{ currentOrder.visitDate }}</el-descriptions-item>
          <el-descriptions-item label="游客人数">{{ currentOrder.visitorCount }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">¥{{ currentOrder.actualAmount }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <h3 style="margin-top: 20px;">票券信息</h3>
        <el-table :data="currentOrder.items" stripe style="width: 100%">
          <el-table-column prop="visitorName" label="游客姓名" />
          <el-table-column prop="visitorIdCard" label="身份证号" />
          <el-table-column prop="price" label="票价">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="verificationCode" label="核销码" width="280" />
          <el-table-column prop="isVerified" label="核销状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.isVerified ? 'success' : 'warning'">
                {{ row.isVerified ? '已核销' : '未核销' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="verifyTime" label="核销时间" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button 
                v-if="!row.isVerified" 
                link 
                type="success" 
                @click="handleVerify(row)">
                核销
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 核销对话框 -->
    <el-dialog v-model="verifyVisible" title="核销票券" width="400px">
      <el-form :model="verifyForm" label-width="100px">
        <el-form-item label="核销码">
          <el-input v-model="verifyForm.verificationCode" disabled />
        </el-form-item>
        <el-form-item label="游客姓名">
          <el-input v-model="verifyForm.visitorName" disabled />
        </el-form-item>
        <el-form-item label="核销员ID">
          <el-input-number v-model="verifyForm.staffId" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="verifyVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmVerify">确认核销</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { batchVerify, cancelOrder, getAllOrders, getOrderDetail, verifyTicket } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'

// 搜索表单
const searchForm = ref({
  orderNo: '',
  status: ''
})

// 订单列表
const orderList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 订单详情
const detailVisible = ref(false)
const currentOrder = ref(null)

// 核销表单
const verifyVisible = ref(false)
const verifyForm = ref({
  verificationCode: '',
  visitorName: '',
  staffId: 1
})

// 加载订单列表
const loadOrders = async () => {
  try {
    const response = await getAllOrders()
    if (response.data.success) {
      let list = response.data.data
      
      // 前端过滤
      if (searchForm.value.orderNo) {
        list = list.filter(item => item.orderNo.includes(searchForm.value.orderNo))
      }
      if (searchForm.value.status) {
        list = list.filter(item => item.status === searchForm.value.status)
      }
      
      total.value = list.length
      
      // 前端分页
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      orderList.value = list.slice(start, end)
    }
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  }
}

// 查看订单详情
const handleView = async (row) => {
  try {
    const response = await getOrderDetail(row.id)
    if (response.data.success) {
      currentOrder.value = response.data.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载订单详情失败')
  }
}

// 取消订单
const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await cancelOrder(row.id)
      if (response.data.success) {
        ElMessage.success('订单已取消')
        loadOrders()
      }
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '取消订单失败')
    }
  })
}

// 批量核销
const handleBatchVerify = (row) => {
  ElMessageBox.prompt('请输入核销员ID', '批量核销', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^\d+$/,
    inputErrorMessage: '请输入有效的核销员ID'
  }).then(async ({ value }) => {
    try {
      const response = await batchVerify(row.id, parseInt(value))
      if (response.data.success) {
        ElMessage.success(response.data.message)
        loadOrders()
      }
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '批量核销失败')
    }
  })
}

// 单个核销
const handleVerify = (row) => {
  verifyForm.value = {
    verificationCode: row.verificationCode,
    visitorName: row.visitorName,
    staffId: 1
  }
  verifyVisible.value = true
}

// 确认核销
const confirmVerify = async () => {
  try {
    const response = await verifyTicket(verifyForm.value.verificationCode, verifyForm.value.staffId)
    if (response.data.success) {
      ElMessage.success('核销成功')
      verifyVisible.value = false
      handleView(currentOrder.value) // 刷新订单详情
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '核销失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadOrders()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    orderNo: '',
    status: ''
  }
  currentPage.value = 1
  loadOrders()
}

// 分页
const handleSizeChange = () => {
  loadOrders()
}

const handleCurrentChange = () => {
  loadOrders()
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PENDING_PAYMENT': 'warning',
    'PENDING_USE': 'info',
    'COMPLETED': 'success',
    'CANCELLED': 'danger',
    'REFUNDED': 'info'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'PENDING_PAYMENT': '待支付',
    'PENDING_USE': '待使用',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'REFUNDED': '已退款'
  }
  return textMap[status] || status
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-list {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
