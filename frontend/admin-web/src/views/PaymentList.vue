<template>
  <div class="payment-management">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="支付类型">
          <el-select v-model="searchForm.paymentType" placeholder="请选择支付类型" clearable style="width: 150px">
            <el-option label="微信支付" value="wechat" />
            <el-option label="支付宝" value="alipay" />
            <el-option label="余额支付" value="balance" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="待支付" :value="0" />
            <el-option label="支付成功" :value="1" />
            <el-option label="支付失败" :value="2" />
            <el-option label="已关闭" :value="3" />
            <el-option label="处理中" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">检索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">待支付</div>
            <div class="stat-value">{{ statistics.pending || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card success">
          <div class="stat-content">
            <div class="stat-label">支付成功</div>
            <div class="stat-value">{{ statistics.success || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card danger">
          <div class="stat-content">
            <div class="stat-label">支付失败</div>
            <div class="stat-value">{{ statistics.failed || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card info">
          <div class="stat-content">
            <div class="stat-label">处理中</div>
            <div class="stat-value">{{ statistics.processing || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 列表区域 -->
    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="paymentNo" label="支付单号" width="200" />
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="paymentType" label="支付类型" width="120">
          <template #default="{ row }">
            {{ getPaymentTypeText(row.paymentType) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentChannelText" label="支付渠道" width="100" />
        <el-table-column prop="paymentAmount" label="支付金额" width="120">
          <template #default="{ row }">
            ¥{{ row.paymentAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payerId" label="支付方标识" width="150" show-overflow-tooltip />
        <el-table-column prop="thirdPartyNo" label="第三方流水号" width="200" show-overflow-tooltip />
        <el-table-column prop="paymentTime" label="支付时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-button 
              link 
              type="danger" 
              size="small" 
              @click="closePayment(row)"
              v-if="row.status === 0"
            >
              关闭
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
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="支付详情" width="800px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="支付单号">{{ currentRow.paymentNo }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ currentRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="支付类型">{{ getPaymentTypeText(currentRow.paymentType) }}</el-descriptions-item>
        <el-descriptions-item label="支付渠道">{{ getPaymentChannelText(currentRow.paymentType) }}</el-descriptions-item>
        <el-descriptions-item label="支付金额">¥{{ currentRow.paymentAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow.status)">{{ currentRow.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="支付方标识">{{ currentRow.payerId }}</el-descriptions-item>
        <el-descriptions-item label="第三方流水号">{{ currentRow.thirdPartyNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentRow.paymentTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="回调时间">{{ currentRow.callbackTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentRow.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { closePayment as closePaymentApi, getPayments, getPaymentStatistics } from '@/api/payment'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const currentRow = ref(null)

const searchForm = reactive({
  orderNo: '',
  paymentType: '',
  status: null,
  timeRange: []
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const statistics = reactive({
  pending: 0,
  success: 0,
  failed: 0,
  processing: 0
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      orderNo: searchForm.orderNo || undefined,
      paymentType: searchForm.paymentType || undefined,
      status: searchForm.status !== null ? searchForm.status : undefined,
      startTime: searchForm.timeRange?.[0] || undefined,
      endTime: searchForm.timeRange?.[1] || undefined
    }

    const res = await getPayments(params)
    if (res.success && res.data) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getPaymentStatistics()
    Object.assign(statistics, res)
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 检索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const resetSearch = () => {
  Object.assign(searchForm, {
    orderNo: '',
    paymentType: '',
    status: null,
    timeRange: []
  })
  handleSearch()
}

// 查看详情
const viewDetail = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

// 关闭支付
const closePayment = async (row) => {
  try {
    await ElMessageBox.confirm('确认关闭此支付订单？', '提示', {
      type: 'warning'
    })

    await closePaymentApi(row.paymentNo)
    ElMessage.success('关闭成功')
    loadData()
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('关闭失败：' + error.message)
    }
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'danger',
    3: 'info',
    4: 'primary'
  }
  return types[status] || ''
}

// 获取支付类型文本
const getPaymentTypeText = (type) => {
  const types = {
    'WECHAT': '微信支付',
    'ALIPAY': '支付宝',
    'POINTS': '积分支付',
    'MIXED': '混合支付',
    'wechat': '微信支付',
    'alipay': '支付宝',
    'points': '积分支付',
    'mixed': '混合支付'
  }
  return types[type] || type
}

// 获取支付渠道文本
const getPaymentChannelText = (type) => {
  const channels = {
    'WECHAT': '微信',
    'ALIPAY': '支付宝',
    'POINTS': '系统',
    'MIXED': '多渠道',
    'wechat': '微信',
    'alipay': '支付宝',
    'points': '系统',
    'mixed': '多渠道'
  }
  return channels[type] || type
}

onMounted(() => {
  loadData()
  loadStatistics()
})
</script>

<style scoped lang="scss">
.payment-management {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;

  .stat-card {
    &.success {
      border-left: 4px solid #67c23a;
    }

    &.danger {
      border-left: 4px solid #f56c6c;
    }

    &.info {
      border-left: 4px solid #909399;
    }

    .stat-content {
      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }

      .stat-value {
        font-size: 28px;
        font-weight: bold;
        color: #303133;
      }
    }
  }
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
