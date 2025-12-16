<template>
  <div class="activity-manage">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="活动名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入活动名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 150px">
            <el-option label="打卡任务" :value="1" />
            <el-option label="积分奖励" :value="2" />
            <el-option label="主题促销" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.auditStatus" placeholder="全部" clearable style="width: 150px">
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="审核拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 150px">
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleCreate">创建活动</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="活动名称" width="200" />
        <el-table-column prop="type" label="活动类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 1" type="info">打卡任务</el-tag>
            <el-tag v-else-if="row.type === 2" type="success">积分奖励</el-tag>
            <el-tag v-else-if="row.type === 3" type="warning">主题促销</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startsAt" label="开始时间" width="160" />
        <el-table-column prop="endsAt" label="结束时间" width="160" />
        <el-table-column prop="auditStatus" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.auditStatus === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.auditStatus === 1" type="success">审核通过</el-tag>
            <el-tag v-else-if="row.auditStatus === 2" type="danger">审核拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="活动状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info">未开始</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">进行中</el-tag>
            <el-tag v-else-if="row.status === 2" type="warning">已结束</el-tag>
            <el-tag v-else-if="row.status === 3" type="danger">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="300">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.auditStatus === 0" link type="success" size="small" @click="handleAudit(row, 1)">通过</el-button>
            <el-button v-if="row.auditStatus === 0" link type="danger" size="small" @click="handleAudit(row, 2)">拒绝</el-button>
            <el-button v-if="row.status === 0 && row.auditStatus === 1" link type="success" size="small" @click="handleToggleStatus(row, 1)">上线</el-button>
            <el-button v-if="row.status === 1" link type="warning" size="small" @click="handleToggleStatus(row, 2)">下线</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">打卡任务</el-radio>
            <el-radio :value="2">积分奖励</el-radio>
            <el-radio :value="3">主题促销</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="封面图片" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入活动描述" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startsAt">
          <el-date-picker
            v-model="form.startsAt"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endsAt">
          <el-date-picker
            v-model="form.endsAt"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="参与人数限制" prop="participationLimit">
          <el-input-number v-model="form.participationLimit" :min="0" placeholder="0表示不限制" />
        </el-form-item>
        <el-form-item label="参与要求" prop="requirementType">
          <el-radio-group v-model="form.requirementType">
            <el-radio :value="1">无要求</el-radio>
            <el-radio :value="2">等级要求</el-radio>
            <el-radio :value="3">积分要求</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.requirementType !== 1" label="要求值" prop="requirementValue">
          <el-input-number v-model="form.requirementValue" :min="1" />
        </el-form-item>
        <el-form-item label="奖励配置" prop="rewardConfig">
          <el-input v-model="form.rewardConfig" type="textarea" :rows="3" placeholder='JSON格式，如：{"type":"points","value":100}' />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityList, createActivity, updateActivity, deleteActivity, auditActivity, toggleActivityStatus } from '@/api/activity'

const searchForm = reactive({
  keyword: '',
  type: null,
  status: null,
  auditStatus: null
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('创建活动')
const formRef = ref(null)
const form = reactive({
  id: null,
  merchantId: 1, // TODO: 从登录信息获取
  name: '',
  type: 1,
  coverImage: '',
  description: '',
  startsAt: '',
  endsAt: '',
  location: '',
  participationLimit: 0,
  requirementType: 1,
  requirementValue: 0,
  rewardConfig: '{"type":"points","value":100}'
})

const rules = {
  name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  startsAt: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endsAt: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  rewardConfig: [{ required: true, message: '请输入奖励配置', trigger: 'blur' }]
}

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  try {
    const params = {
      ...searchForm,
      page: pagination.page - 1,
      size: pagination.size
    }
    const res = await getActivityList(params)
    tableData.value = res.content
    pagination.total = res.totalElements
  } catch (error) {
    ElMessage.error('获取活动列表失败')
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    type: null,
    status: null,
    auditStatus: null
  })
  handleSearch()
}

const handleCreate = () => {
  dialogTitle.value = '创建活动'
  Object.assign(form, {
    id: null,
    merchantId: 1,
    name: '',
    type: 1,
    coverImage: '',
    description: '',
    startsAt: '',
    endsAt: '',
    location: '',
    participationLimit: 0,
    requirementType: 1,
    requirementValue: 0,
    rewardConfig: '{"type":"points","value":100}'
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑活动'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      if (form.id) {
        await updateActivity(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await createActivity(form)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    }
  })
}

const handleAudit = async (row, auditStatus) => {
  try {
    await auditActivity(row.id, auditStatus)
    ElMessage.success(auditStatus === 1 ? '审核通过' : '审核拒绝')
    fetchData()
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

const handleToggleStatus = async (row, status) => {
  try {
    await toggleActivityStatus(row.id, status)
    ElMessage.success(status === 1 ? '上线成功' : '下线成功')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该活动吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteActivity(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleView = (row) => {
  ElMessage.info('查看功能待实现')
}
</script>

<style scoped>
.activity-manage {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
