<template>
  <div class="ticket-form">
    <el-page-header @back="handleBack" :icon="ArrowLeft">
      <template #content>
        <span class="page-title">{{ isEdit ? '编辑票种' : '创建票种' }}</span>
      </template>
    </el-page-header>

    <el-card class="form-card">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        v-loading="loading"
      >
        <el-divider content-position="left">基本信息</el-divider>

        <el-form-item label="所属景区" prop="scenicSpotId">
          <el-select
            v-model="form.scenicSpotId"
            placeholder="请选择景区"
            style="width: 400px"
          >
            <el-option
              v-for="spot in scenicSpots"
              :key="spot.id"
              :label="spot.name"
              :value="spot.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="票种名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入票种名称"
            style="width: 400px"
          />
        </el-form-item>

        <el-form-item label="票种类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">单票</el-radio>
            <el-radio :label="2">联票</el-radio>
            <el-radio :label="3">套票</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="票种描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入票种描述"
            style="width: 600px"
          />
        </el-form-item>

        <el-divider content-position="left">有效期设置</el-divider>

        <el-form-item label="有效期类型" prop="validityType">
          <el-radio-group v-model="form.validityType">
            <el-radio :label="1">指定日期（单日有效）</el-radio>
            <el-radio :label="2">有效天数（多日有效）</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          label="有效天数"
          prop="validDays"
          v-if="form.validityType === 2"
        >
          <el-input-number
            v-model="form.validDays"
            :min="1"
            :max="365"
            placeholder="请输入有效天数"
          />
          <span class="tip">例如：购买后3天内有效</span>
        </el-form-item>

        <el-form-item label="提前预订天数" prop="advanceDays">
          <el-input-number
            v-model="form.advanceDays"
            :min="0"
            :max="365"
            placeholder="请输入提前预订天数"
          />
          <span class="tip">0表示可当天购买</span>
        </el-form-item>

        <el-divider content-position="left">退改签规则</el-divider>

        <el-form-item label="退款规则" required>
          <div class="refund-policy">
            <el-form-item label="24小时内退款率">
              <el-input-number
                v-model="refundPolicy.within_24h"
                :min="0"
                :max="1"
                :step="0.1"
                :precision="1"
              />
              <span class="tip">0.5 = 退50%</span>
            </el-form-item>
            <el-form-item label="48小时内退款率">
              <el-input-number
                v-model="refundPolicy.within_48h"
                :min="0"
                :max="1"
                :step="0.1"
                :precision="1"
              />
              <span class="tip">0.7 = 退70%</span>
            </el-form-item>
            <el-form-item label="48小时外退款率">
              <el-input-number
                v-model="refundPolicy.over_48h"
                :min="0"
                :max="1"
                :step="0.1"
                :precision="1"
              />
              <span class="tip">1.0 = 全额退款</span>
            </el-form-item>
          </div>
        </el-form-item>

        <el-form-item label="改签规则" required>
          <div class="change-policy">
            <el-form-item label="是否允许改签">
              <el-switch v-model="changePolicy.allowed" />
            </el-form-item>
            <el-form-item label="改签手续费（元）" v-if="changePolicy.allowed">
              <el-input-number
                v-model="changePolicy.fee"
                :min="0"
                :precision="2"
              />
            </el-form-item>
            <el-form-item label="提前改签时间（小时）" v-if="changePolicy.allowed">
              <el-input-number
                v-model="changePolicy.advance_hours"
                :min="0"
              />
            </el-form-item>
          </div>
        </el-form-item>

        <el-divider content-position="left">限购设置</el-divider>

        <el-form-item label="每用户限购数量" prop="limitPerUser">
          <el-input-number
            v-model="form.limitPerUser"
            :min="1"
            placeholder="不限制请留空"
          />
        </el-form-item>

        <el-form-item label="每订单限购数量" prop="limitPerOrder">
          <el-input-number
            v-model="form.limitPerOrder"
            :min="1"
            placeholder="不限制请留空"
          />
        </el-form-item>

        <el-form-item label="每日限购数量" prop="limitPerDay">
          <el-input-number
            v-model="form.limitPerDay"
            :min="1"
            placeholder="不限制请留空"
          />
        </el-form-item>

        <el-divider content-position="left">实名与核验</el-divider>

        <el-form-item label="是否需要实名" prop="requireRealName">
          <el-switch v-model="form.requireRealName" />
        </el-form-item>

        <el-form-item label="是否需要身份证" prop="requireIdCard">
          <el-switch v-model="form.requireIdCard" />
        </el-form-item>

        <el-form-item label="核验方式" prop="verificationMode">
          <el-radio-group v-model="form.verificationMode">
            <el-radio :label="1">二维码</el-radio>
            <el-radio :label="2">人脸识别</el-radio>
            <el-radio :label="3">身份证</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-divider content-position="left">状态设置</el-divider>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存' : '创建' }}
          </el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { getScenicSpots } from '@/api/scenic'
import {
    createTicket,
    getTicket,
    updateTicket
} from '@/api/ticket'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)

const isEdit = computed(() => !!route.params.id)
const loading = ref(false)
const submitting = ref(false)
const scenicSpots = ref([])

const form = reactive({
  scenicSpotId: null,
  name: '',
  type: 1,
  description: '',
  validityType: 1,
  validDays: null,
  advanceDays: 0,
  refundPolicy: {},
  changePolicy: {},
  limitPerUser: null,
  limitPerOrder: null,
  limitPerDay: null,
  requireRealName: false,
  requireIdCard: false,
  verificationMode: 1,
  status: 1
})

const refundPolicy = reactive({
  within_24h: 0.5,
  within_48h: 0.7,
  over_48h: 1.0
})

const changePolicy = reactive({
  allowed: true,
  fee: 10.0,
  advance_hours: 24
})

const rules = {
  scenicSpotId: [
    { required: true, message: '请选择景区', trigger: 'change' }
  ],
  name: [
    { required: true, message: '请输入票种名称', trigger: 'blur' },
    { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择票种类型', trigger: 'change' }
  ],
  validityType: [
    { required: true, message: '请选择有效期类型', trigger: 'change' }
  ]
}

// 加载景区列表
const loadScenicSpots = async () => {
  try {
    const res = await getScenicSpots({ size: 1000 })
    // 响应拦截器已解包ApiResponse，直接访问res.content
    scenicSpots.value = res.content || []
  } catch (error) {
    ElMessage.error('加载景区列表失败')
  }
}

// 加载票种详情
const loadTicket = async () => {
  if (!route.params.id) return

  loading.value = true
  try {
    const res = await getTicket(route.params.id)
    // 后端直接返回TicketResponse对象
    Object.assign(form, res)
    // 解析退改签规则
    Object.assign(refundPolicy, res.refundPolicy || {})
    Object.assign(changePolicy, res.changePolicy || {})
  } catch (error) {
    ElMessage.error('加载票种详情失败')
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value.validate()

  // 组装退改签规则
  form.refundPolicy = { ...refundPolicy }
  form.changePolicy = { ...changePolicy }

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateTicket(route.params.id, form)
      ElMessage.success('保存成功')
    } else {
      await createTicket(form)
      ElMessage.success('创建成功')
    }
    router.push('/tickets/list')
  } catch (error) {
    ElMessage.error(isEdit.value ? '保存失败' : '创建失败')
  } finally {
    submitting.value = false
  }
}

// 返回
const handleBack = () => {
  router.push('/tickets/list')
}

onMounted(() => {
  loadScenicSpots()
  loadTicket()
})
</script>

<style scoped>
.ticket-form {
  padding: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
}

.form-card {
  margin-top: 20px;
}

.tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

.refund-policy,
.change-policy {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  background-color: #f5f7fa;
}

:deep(.el-divider__text) {
  font-weight: bold;
  color: #409eff;
}
</style>
