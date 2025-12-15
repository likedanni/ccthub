<template>
  <div class="refund-management">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="审核拒绝" :value="2" />
            <el-option label="退款中" :value="3" />
            <el-option label="退款成功" :value="4" />
            <el-option label="退款失败" :value="5" />
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
      <el-col :span="4">
        <el-card class="stat-card warning">
          <div class="stat-content">
            <div class="stat-label">待审核</div>
            <div class="stat-value">{{ statistics.pendingAudit || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">审核通过</div>
            <div class="stat-value">{{ statistics.approved || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card danger">
          <div class="stat-content">
            <div class="stat-label">审核拒绝</div>
            <div class="stat-value">{{ statistics.rejected || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card info">
          <div class="stat-content">
            <div class="stat-label">退款中</div>
            <div class="stat-value">{{ statistics.refunding || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card success">
          <div class="stat-content">
            <div class="stat-label">退款成功</div>
            <div class="stat-value">{{ statistics.success || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card danger">
          <div class="stat-content">
            <div class="stat-label">退款失败</div>
            <div class="stat-value">{{ statistics.failed || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 列表区域 -->
    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="refundNo" label="退款单号" width="200" />
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="refundTypeText" label="退款类型" width="100" />
        <el-table-column prop="refundAmount" label="退款金额" width="120">
          <template #default="{ row }">
            ¥{{ row.refundAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="refundReason" label="退款原因" width="150" show-overflow-tooltip />
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditNote" label="审核备注" width="150" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="申请时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-button 
              link 
              type="success" 
              size="small" 
              @click="auditRefund(row, 1)"
              v-if="row.status === 0"
            >
              通过
            </el-button>
            <el-button 
              link 
              type="danger" 
              size="small" 
              @click="auditRefund(row, 2)"
              v-if="row.status === 0"
            >
              拒绝
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
    <el-dialog v-model="detailVisible" title="退款详情" width="800px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="退款单号">{{ currentRow.refundNo }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ currentRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentRow.userId }}</el-descriptions-item>
        <el-descriptions-item label="退款类型">{{ currentRow.refundTypeText }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">¥{{ currentRow.refundAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow.status)">{{ currentRow.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="退款原因" :span="2">{{ currentRow.refundReason }}</el-descriptions-item>
        <el-descriptions-item label="退款凭证" :span="2">
          <div v-if="currentRow.refundEvidence" class="evidence-images">
            <el-image 
              v-for="(img, idx) in currentRow.refundEvidence.split(',')" 
              :key="idx"
              :src="img"
              :preview-src-list="currentRow.refundEvidence.split(',')"
              :initial-index="idx"
              style="width: 100px; height: 100px; margin-right: 10px;"
              fit="cover"
            />
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核人ID">{{ currentRow.auditorId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ currentRow.auditedAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ currentRow.auditNote || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付渠道退款单号">{{ currentRow.paymentRefundNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款完成时间">{{ currentRow.paymentRefundAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentRow.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentRow.updatedAt }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="auditVisible" :title="auditForm.auditStatus === 1 ? '审核通过' : '审核拒绝'" width="500px">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核备注">
          <el-input 
            v-model="auditForm.auditNote" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { auditRefund as auditRefundApi, getRefunds, getRefundStatistics } from '@/api/refund'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const auditVisible = ref(false)
const currentRow = ref(null)

const searchForm = reactive({
  orderNo: '',
  userId: '',
  status: null,
  timeRange: []
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const statistics = reactive({
  pendingAudit: 0,
  approved: 0,
  rejected: 0,
  refunding: 0,
  success: 0,
  failed: 0
})

const auditForm = reactive({
  refundNo: '',
  auditStatus: null,
  auditNote: '',
  auditorId: 1 // TODO: 从登录用户获取
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      orderNo: searchForm.orderNo || undefined,
      userId: searchForm.userId || undefined,
      status: searchForm.status !== null ? searchForm.status : undefined,
      startTime: searchForm.timeRange?.[0] || undefined,
      endTime: searchForm.timeRange?.[1] || undefined
    }

    const res = await getRefunds(params)
    tableData.value = res.content
    pagination.total = res.totalElements
  } catch (error) {
    ElMessage.error('加载数据失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getRefundStatistics()
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
    userId: '',
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

// 审核退款
const auditRefund = (row, auditStatus) => {
  currentRow.value = row
  auditForm.refundNo = row.refundNo
  auditForm.auditStatus = auditStatus
  auditForm.auditNote = ''
  auditVisible.value = true
}

// 提交审核
const submitAudit = async () => {
  try {
    await auditRefundApi(auditForm)
    ElMessage.success('审核成功')
    auditVisible.value = false
    loadData()
    loadStatistics()
  } catch (error) {
    ElMessage.error('审核失败：' + error.message)
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    0: 'warning',
    1: '',
    2: 'danger',
    3: 'info',
    4: 'success',
    5: 'danger'
  }
  return types[status] || ''
}

onMounted(() => {
  loadData()
  loadStatistics()
})
</script>

<style scoped lang="scss">
.refund-management {
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

    &.warning {
      border-left: 4px solid #e6a23c;
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

.evidence-images {
  display: flex;
  flex-wrap: wrap;
}
</style>
