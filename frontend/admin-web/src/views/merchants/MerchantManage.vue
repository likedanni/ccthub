<template>
  <div class="merchant-manage">
    <el-card class="header-card">
      <div class="header">
        <h2>商户管理</h2>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增商户
        </el-button>
      </div>

      <!-- 筛选条件 -->
      <div class="filters">
        <el-form :inline="true" :model="filters" class="filter-form">
          <el-form-item label="商户名称">
            <el-input
              v-model="filters.name"
              placeholder="请输入商户名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="商户类型">
            <el-select
              v-model="filters.type"
              placeholder="全部类型"
              clearable
              style="width: 150px"
            >
              <el-option label="景区" :value="1" />
              <el-option label="餐饮" :value="2" />
              <el-option label="文创" :value="3" />
              <el-option label="生鲜便利" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="合作类型">
            <el-select
              v-model="filters.cooperationType"
              placeholder="全部合作类型"
              clearable
              style="width: 150px"
            >
              <el-option label="直营" :value="1" />
              <el-option label="联营" :value="2" />
              <el-option label="加盟" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select
              v-model="filters.auditStatus"
              placeholder="全部状态"
              clearable
              style="width: 150px"
            >
              <el-option label="待审核" :value="0" />
              <el-option label="已通过" :value="1" />
              <el-option label="已拒绝" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="商户状态">
            <el-select
              v-model="filters.status"
              placeholder="全部状态"
              clearable
              style="width: 120px"
            >
              <el-option label="正常" :value="1" />
              <el-option label="停用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 商户列表 -->
    <el-card class="table-card">
      <el-table :data="merchants" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商户名称" min-width="150" />
        <el-table-column label="商户类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getMerchantTypeColor(row.type)">
              {{ getMerchantTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="合作类型" width="100">
          <template #default="{ row }">
            {{ getCooperationTypeText(row.cooperationType) }}
          </template>
        </el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="row.auditStatus === 1 ? 'success' : row.auditStatus === 2 ? 'danger' : 'warning'"
            >
              {{ getAuditStatusText(row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="120">
          <template #default="{ row }">
            <el-rate v-model="row.score" disabled show-score text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">
              详情
            </el-button>
            <el-button link type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              v-if="row.auditStatus === 0"
              link
              type="success"
              @click="handleAudit(row, 1)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.auditStatus === 0"
              link
              type="danger"
              @click="handleAudit(row, 2)"
            >
              拒绝
            </el-button>
            <el-button
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadMerchants"
          @current-change="loadMerchants"
        />
      </div>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商户名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入商户名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商户类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择商户类型" style="width: 100%">
                <el-option label="景区" :value="1" />
                <el-option label="餐饮" :value="2" />
                <el-option label="文创" :value="3" />
                <el-option label="生鲜便利" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合作类型" prop="cooperationType">
              <el-select
                v-model="formData.cooperationType"
                placeholder="请选择合作类型"
                style="width: 100%"
              >
                <el-option label="直营" :value="1" />
                <el-option label="联营" :value="2" />
                <el-option label="加盟" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="营业执照号">
              <el-input
                v-model="formData.businessLicense"
                placeholder="请输入营业执照号"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input
                v-model="formData.contactPerson"
                placeholder="请输入联系人"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input
                v-model="formData.contactPhone"
                placeholder="请输入联系电话"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input v-model="formData.province" placeholder="请输入省份" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input v-model="formData.city" placeholder="请输入城市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区县">
              <el-input v-model="formData.district" placeholder="请输入区县" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="address">
          <el-input
            v-model="formData.address"
            type="textarea"
            :rows="2"
            placeholder="请输入详细地址"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经度">
              <el-input-number
                v-model="formData.longitude"
                :precision="6"
                :step="0.000001"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度">
              <el-input-number
                v-model="formData.latitude"
                :precision="6"
                :step="0.000001"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="结算费率">
          <el-input-number
            v-model="formData.settlementRate"
            :precision="4"
            :step="0.0001"
            :min="0"
            :max="1"
            style="width: 200px"
          />
          <span style="margin-left: 10px; color: #999">
            (0-1之间，例如0.05表示5%)
          </span>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="商户详情" width="800px">
      <el-descriptions :column="2" border v-if="currentMerchant">
        <el-descriptions-item label="商户ID">
          {{ currentMerchant.id }}
        </el-descriptions-item>
        <el-descriptions-item label="商户名称">
          {{ currentMerchant.name }}
        </el-descriptions-item>
        <el-descriptions-item label="商户类型">
          <el-tag :type="getMerchantTypeColor(currentMerchant.type)">
            {{ getMerchantTypeText(currentMerchant.type) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="合作类型">
          {{ getCooperationTypeText(currentMerchant.cooperationType) }}
        </el-descriptions-item>
        <el-descriptions-item label="联系人">
          {{ currentMerchant.contactPerson }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ currentMerchant.contactPhone }}
        </el-descriptions-item>
        <el-descriptions-item label="营业执照">
          {{ currentMerchant.businessLicense || '未填写' }}
        </el-descriptions-item>
        <el-descriptions-item label="商户等级">
          {{ currentMerchant.level }}
        </el-descriptions-item>
        <el-descriptions-item label="商户评分">
          <el-rate v-model="currentMerchant.score" disabled show-score text-color="#ff9900" />
        </el-descriptions-item>
        <el-descriptions-item label="结算费率">
          {{ (currentMerchant.settlementRate * 100).toFixed(2) }}%
        </el-descriptions-item>
        <el-descriptions-item label="所在地区" :span="2">
          {{ currentMerchant.province }} {{ currentMerchant.city }}
          {{ currentMerchant.district }}
        </el-descriptions-item>
        <el-descriptions-item label="详细地址" :span="2">
          {{ currentMerchant.address }}
        </el-descriptions-item>
        <el-descriptions-item label="经纬度" :span="2">
          经度: {{ currentMerchant.longitude }}, 纬度:
          {{ currentMerchant.latitude }}
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag
            :type="
              currentMerchant.auditStatus === 1
                ? 'success'
                : currentMerchant.auditStatus === 2
                ? 'danger'
                : 'warning'
            "
          >
            {{ getAuditStatusText(currentMerchant.auditStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentMerchant.status === 1 ? 'success' : 'info'">
            {{ currentMerchant.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{ currentMerchant.createTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

const API_BASE = 'http://localhost:8080/api'

// 数据
const merchants = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const submitting = ref(false)
const currentMerchant = ref(null)
const dialogTitle = ref('新增商户')
const isEdit = ref(false)
const formRef = ref(null)

// 筛选条件
const filters = reactive({
  name: '',
  type: null,
  cooperationType: null,
  auditStatus: null,
  status: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表单数据
const formData = reactive({
  name: '',
  type: null,
  cooperationType: null,
  contactPerson: '',
  contactPhone: '',
  businessLicense: '',
  province: '',
  city: '',
  district: '',
  address: '',
  longitude: null,
  latitude: null,
  settlementRate: 0.05
})

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入商户名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择商户类型', trigger: 'change' }],
  cooperationType: [
    { required: true, message: '请选择合作类型', trigger: 'change' }
  ],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 加载商户列表
const loadMerchants = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      ...filters
    }
    const response = await axios.get(`${API_BASE}/merchants`, { params })
    merchants.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    console.error('加载商户列表失败:', error)
    ElMessage.error('加载商户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadMerchants()
}

// 重置
const handleReset = () => {
  filters.name = ''
  filters.type = null
  filters.cooperationType = null
  filters.auditStatus = null
  filters.status = null
  pagination.page = 1
  loadMerchants()
}

// 新增
const handleCreate = () => {
  isEdit.value = false
  dialogTitle.value = '新增商户'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑商户'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  currentMerchant.value = row
  detailVisible.value = true
}

// 审核
const handleAudit = async (row, auditStatus) => {
  const action = auditStatus === 1 ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定要${action}该商户的入驻申请吗？`, '提示', {
      type: 'warning'
    })

    await axios.put(`${API_BASE}/merchants/${row.id}/audit`, { auditStatus })
    ElMessage.success(`审核${action}成功`)
    loadMerchants()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
      ElMessage.error('审核失败')
    }
  }
}

// 启用/停用
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '停用'

  try {
    await ElMessageBox.confirm(`确定要${action}该商户吗？`, '提示', {
      type: 'warning'
    })

    await axios.put(`${API_BASE}/merchants/${row.id}/status`, {
      status: newStatus
    })
    ElMessage.success(`${action}成功`)
    loadMerchants()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该商户吗？此操作不可恢复！', '警告', {
      type: 'error'
    })

    await axios.delete(`${API_BASE}/merchants/${row.id}`)
    ElMessage.success('删除成功')
    loadMerchants()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (isEdit.value) {
      await axios.put(`${API_BASE}/merchants/${formData.id}`, formData)
      ElMessage.success('更新成功')
    } else {
      await axios.post(`${API_BASE}/merchants`, formData)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    loadMerchants()
  } catch (error) {
    if (error.response) {
      console.error('提交失败:', error)
      ElMessage.error('提交失败: ' + (error.response.data.error || '未知错误'))
    }
  } finally {
    submitting.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.name = ''
  formData.type = null
  formData.cooperationType = null
  formData.contactPerson = ''
  formData.contactPhone = ''
  formData.businessLicense = ''
  formData.province = ''
  formData.city = ''
  formData.district = ''
  formData.address = ''
  formData.longitude = null
  formData.latitude = null
  formData.settlementRate = 0.05

  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 辅助函数
const getMerchantTypeText = (type) => {
  const map = { 1: '景区', 2: '餐饮', 3: '文创', 4: '生鲜便利' }
  return map[type] || '未知'
}

const getMerchantTypeColor = (type) => {
  const map = { 1: 'success', 2: 'warning', 3: 'primary', 4: 'info' }
  return map[type] || ''
}

const getCooperationTypeText = (type) => {
  const map = { 1: '直营', 2: '联营', 3: '加盟' }
  return map[type] || '未知'
}

const getAuditStatusText = (status) => {
  const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return map[status] || '未知'
}

// 初始化
onMounted(() => {
  loadMerchants()
})
</script>

<style scoped>
.merchant-manage {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
}

.filters {
  margin-top: 20px;
}

.filter-form {
  margin: 0;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
