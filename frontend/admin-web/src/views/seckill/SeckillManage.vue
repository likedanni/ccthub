<template>
    <div class="seckill-manage">
        <el-card class="search-card">
            <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="状态">
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
                    <el-button type="success" @click="handleCreate">创建秒杀</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <el-table :data="tableData" border style="width: 100%">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="title" label="秒杀标题" width="200" />
                <el-table-column prop="seckillPrice" label="秒杀价" width="100" />
                <el-table-column prop="originalPrice" label="原价" width="100" />
                <el-table-column prop="availableInventory" label="剩余库存" width="100" />
                <el-table-column prop="totalInventory" label="总库存" width="100" />
                <el-table-column prop="limitPerUser" label="限购数" width="80" />
                <el-table-column prop="startsAt" label="开始时间" width="160" />
                <el-table-column prop="endsAt" label="结束时间" width="160" />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag v-if="row.status === 0" type="info">未开始</el-tag>
                        <el-tag v-else-if="row.status === 1" type="success">进行中</el-tag>
                        <el-tag v-else-if="row.status === 2" type="warning">已结束</el-tag>
                        <el-tag v-else-if="row.status === 3" type="danger">已取消</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" fixed="right" width="200">
                    <template #default="{ row }">
                        <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                        <el-button v-if="row.status === 0" link type="success" size="small"
                            @click="handleToggleStatus(row, 1)">开始</el-button>
                        <el-button v-if="row.status === 1" link type="warning" size="small"
                            @click="handleToggleStatus(row, 2)">结束</el-button>
                        <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
                :total="pagination.total" :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper" @size-change="handleSearch"
                @current-change="handleSearch" />
        </el-card>

        <!-- 创建/编辑对话框 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
                <el-form-item label="秒杀标题" prop="title">
                    <el-input v-model="form.title" placeholder="请输入秒杀标题" />
                </el-form-item>
                <el-form-item label="商品ID" prop="productId">
                    <el-input-number v-model="form.productId" :min="1" placeholder="请输入商品ID" />
                </el-form-item>
                <el-form-item label="秒杀价" prop="seckillPrice">
                    <el-input-number v-model="form.seckillPrice" :min="0" :precision="2" placeholder="请输入秒杀价" />
                </el-form-item>
                <el-form-item label="原价" prop="originalPrice">
                    <el-input-number v-model="form.originalPrice" :min="0" :precision="2" placeholder="请输入原价" />
                </el-form-item>
                <el-form-item label="总库存" prop="totalInventory">
                    <el-input-number v-model="form.totalInventory" :min="1" placeholder="请输入总库存" />
                </el-form-item>
                <el-form-item label="每人限购" prop="limitPerUser">
                    <el-input-number v-model="form.limitPerUser" :min="1" placeholder="每人限购数量" />
                </el-form-item>
                <el-form-item label="开始时间" prop="startsAt">
                    <el-date-picker v-model="form.startsAt" type="datetime" placeholder="选择开始时间"
                        format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" />
                </el-form-item>
                <el-form-item label="结束时间" prop="endsAt">
                    <el-date-picker v-model="form.endsAt" type="datetime" placeholder="选择结束时间"
                        format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" />
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
import { createSeckill, deleteSeckill, getSeckillList, toggleSeckillStatus, updateSeckill } from '@/api/seckill'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

const searchForm = reactive({
    status: null
})

const tableData = ref([])
const pagination = reactive({
    page: 1,
    size: 10,
    total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('创建秒杀')
const formRef = ref(null)
const form = reactive({
    id: null,
    productId: null,
    title: '',
    seckillPrice: 0,
    originalPrice: 0,
    totalInventory: 100,
    limitPerUser: 1,
    startsAt: '',
    endsAt: ''
})

const rules = {
    title: [{ required: true, message: '请输入秒杀标题', trigger: 'blur' }],
    productId: [{ required: true, message: '请输入商品ID', trigger: 'blur' }],
    seckillPrice: [{ required: true, message: '请输入秒杀价', trigger: 'blur' }],
    originalPrice: [{ required: true, message: '请输入原价', trigger: 'blur' }],
    totalInventory: [{ required: true, message: '请输入总库存', trigger: 'blur' }],
    startsAt: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
    endsAt: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
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
        const res = await getSeckillList(params)
        tableData.value = res.content
        pagination.total = res.totalElements
    } catch (error) {
        ElMessage.error('获取秒杀列表失败')
    }
}

const handleSearch = () => {
    pagination.page = 1
    fetchData()
}

const handleReset = () => {
    searchForm.status = null
    handleSearch()
}

const handleCreate = () => {
    dialogTitle.value = '创建秒杀'
    Object.assign(form, {
        id: null,
        productId: null,
        title: '',
        seckillPrice: 0,
        originalPrice: 0,
        totalInventory: 100,
        limitPerUser: 1,
        startsAt: '',
        endsAt: ''
    })
    dialogVisible.value = true
}

const handleEdit = (row) => {
    dialogTitle.value = '编辑秒杀'
    Object.assign(form, { ...row })
    dialogVisible.value = true
}

const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (!valid) return

        try {
            if (form.id) {
                await updateSeckill(form.id, form)
                ElMessage.success('更新成功')
            } else {
                await createSeckill(form)
                ElMessage.success('创建成功')
            }
            dialogVisible.value = false
            fetchData()
        } catch (error) {
            ElMessage.error(error.message || '操作失败')
        }
    })
}

const handleToggleStatus = async (row, status) => {
    try {
        await toggleSeckillStatus(row.id, status)
        ElMessage.success(status === 1 ? '已开始' : '已结束')
        fetchData()
    } catch (error) {
        ElMessage.error('操作失败')
    }
}

const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm('确定删除该秒杀吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        await deleteSeckill(row.id)
        ElMessage.success('删除成功')
        fetchData()
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
        }
    }
}
</script>

<style scoped>
.seckill-manage {
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
