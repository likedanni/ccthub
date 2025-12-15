<template>
  <div class="ticket-price-calendar">
    <el-page-header @back="handleBack" :icon="ArrowLeft">
      <template #content>
        <span class="page-title">票价日历 - {{ ticketName }}</span>
      </template>
    </el-page-header>

    <el-card class="calendar-card">
      <template #header>
        <div class="card-header">
          <span>价格与库存设置</span>
          <el-button type="primary" @click="showBatchDialog = true" :icon="Plus">
            批量设置
          </el-button>
        </div>
      </template>

      <el-table :data="priceList" v-loading="loading" border>
        <el-table-column prop="priceDate" label="日期" width="120" />
        <el-table-column prop="priceTypeText" label="价格类型" width="120" />
        <el-table-column label="价格" width="200">
          <template #default="{ row }">
            <span>原价：¥{{ row.originalPrice }}</span>
            <el-divider direction="vertical" />
            <span class="sell-price">售价：¥{{ row.sellPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="300">
          <template #default="{ row }">
            <el-tag type="success">总计：{{ row.inventoryTotal }}</el-tag>
            <el-tag type="primary">可用：{{ row.inventoryAvailable }}</el-tag>
            <el-tag type="warning">锁定：{{ row.inventoryLocked }}</el-tag>
            <el-tag type="danger">已售：{{ row.inventorySold }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'info'">
              {{ row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="库存占用率" width="150">
          <template #default="{ row }">
            <el-progress
              :percentage="Math.round(row.occupancyRate)"
              :color="getOccupancyColor(row.occupancyRate)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
              :icon="Edit"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
              :icon="Delete"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 批量设置对话框 -->
    <el-dialog
      v-model="showBatchDialog"
      title="批量设置票价"
      width="600px"
    >
      <el-form
        ref="batchFormRef"
        :model="batchForm"
        :rules="batchRules"
        label-width="120px"
      >
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="batchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="价格类型" prop="priceType">
          <el-select v-model="batchForm.priceType" placeholder="请选择价格类型">
            <el-option label="成人票" :value="1" />
            <el-option label="学生票" :value="2" />
            <el-option label="儿童票" :value="3" />
            <el-option label="老年票" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="原价" prop="originalPrice">
          <el-input-number
            v-model="batchForm.originalPrice"
            :min="0.01"
            :precision="2"
            placeholder="请输入原价"
          />
        </el-form-item>

        <el-form-item label="售价" prop="sellPrice">
          <el-input-number
            v-model="batchForm.sellPrice"
            :min="0.01"
            :precision="2"
            placeholder="请输入售价"
          />
        </el-form-item>

        <el-form-item label="总库存" prop="inventoryTotal">
          <el-input-number
            v-model="batchForm.inventoryTotal"
            :min="0"
            placeholder="请输入总库存"
          />
        </el-form-item>

        <el-form-item label="是否启用" prop="isActive">
          <el-switch v-model="batchForm.isActive" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showBatchDialog = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {
    batchSaveTicketPrices,
    deleteTicketPrice,
    getTicket,
    getTicketPricesByTicket
} from '@/api/ticket'
import { ArrowLeft, Delete, Edit, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()
const ticketId = route.params.ticketId

const ticketName = ref('')
const priceList = ref([])
const loading = ref(false)
const submitting = ref(false)
const showBatchDialog = ref(false)
const batchFormRef = ref(null)

const batchForm = reactive({
  dateRange: [],
  priceType: 1,
  originalPrice: 0,
  sellPrice: 0,
  inventoryTotal: 0,
  isActive: true
})

const batchRules = {
  dateRange: [
    { required: true, message: '请选择日期范围', trigger: 'change' }
  ],
  priceType: [
    { required: true, message: '请选择价格类型', trigger: 'change' }
  ],
  originalPrice: [
    { required: true, message: '请输入原价', trigger: 'blur' }
  ],
  sellPrice: [
    { required: true, message: '请输入售价', trigger: 'blur' }
  ],
  inventoryTotal: [
    { required: true, message: '请输入总库存', trigger: 'blur' }
  ]
}

// 加载票种信息
const loadTicket = async () => {
  try {
    const res = await getTicket(ticketId)
    ticketName.value = res.name  // 后端直接返回TicketResponse对象
  } catch (error) {
    ElMessage.error('加载票种信息失败')
  }
}

// 加载票价列表
const loadPrices = async () => {
  loading.value = true
  try {
    const res = await getTicketPricesByTicket(ticketId)
    // 后端直接返回数组
    priceList.value = (res || []).map(item => ({
      ...item,
      occupancyRate: (item.inventorySold + item.inventoryLocked) / item.inventoryTotal * 100
    }))
  } catch (error) {
    ElMessage.error('加载票价列表失败')
  } finally {
    loading.value = false
  }
}

// 批量提交
const handleBatchSubmit = async () => {
  await batchFormRef.value.validate()

  submitting.value = true
  try {
    const [startDate, endDate] = batchForm.dateRange
    const start = new Date(startDate)
    const end = new Date(endDate)
    const requests = []

    // 生成每天的票价数据
    for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
      requests.push({
        ticketId: Number(ticketId),
        priceDate: d.toISOString().split('T')[0],
        priceType: batchForm.priceType,
        originalPrice: batchForm.originalPrice,
        sellPrice: batchForm.sellPrice,
        inventoryTotal: batchForm.inventoryTotal,
        isActive: batchForm.isActive
      })
    }

    await batchSaveTicketPrices(requests)
    ElMessage.success('批量设置成功')
    showBatchDialog.value = false
    loadPrices()
  } catch (error) {
    ElMessage.error('批量设置失败')
  } finally {
    submitting.value = false
  }
}

// 编辑（暂不实现，后续完善）
const handleEdit = (row) => {
  ElMessage.info('编辑功能开发中')
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除该票价吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteTicketPrice(row.id)
        ElMessage.success('删除成功')
        loadPrices()
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 返回
const handleBack = () => {
  router.push('/tickets/list')
}

// 获取库存占用率颜色
const getOccupancyColor = (rate) => {
  if (rate < 50) return '#67c23a'
  if (rate < 80) return '#e6a23c'
  return '#f56c6c'
}

onMounted(() => {
  loadTicket()
  loadPrices()
})
</script>

<style scoped>
.ticket-price-calendar {
  padding: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
}

.calendar-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sell-price {
  color: #f56c6c;
  font-weight: bold;
}
</style>
