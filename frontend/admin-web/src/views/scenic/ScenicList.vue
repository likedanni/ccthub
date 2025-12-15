<template>
    <div class="scenic-list-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>景区管理</span>
                    <el-button type="primary" @click="handleCreate">新增景区</el-button>
                </div>
            </template>

            <!-- 搜索筛选 -->
            <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="景区名称">
                    <el-input v-model="searchForm.name" placeholder="请输入景区名称" clearable />
                </el-form-item>
                <el-form-item label="省份">
                    <el-input v-model="searchForm.province" placeholder="请输入省份" clearable />
                </el-form-item>
                <el-form-item label="城市">
                    <el-input v-model="searchForm.city" placeholder="请输入城市" clearable />
                </el-form-item>
                <el-form-item label="等级">
                    <el-select v-model="searchForm.level" placeholder="请选择等级" clearable>
                        <el-option label="A级" value="A" />
                        <el-option label="AA级" value="AA" />
                        <el-option label="AAA级" value="AAA" />
                        <el-option label="AAAA级" value="AAAA" />
                        <el-option label="AAAAA级(5A)" value="AAAAA" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                        <el-option label="开放" value="ACTIVE" />
                        <el-option label="关闭" value="INACTIVE" />
                        <el-option label="维护中" value="MAINTENANCE" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 表格 -->
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="name" label="景区名称" width="200" />
                <el-table-column prop="level" label="等级" width="100" />
                <el-table-column label="地址" width="300">
                    <template #default="{ row }">
                        {{ row.province }} {{ row.city }} {{ row.district }} {{ row.address }}
                    </template>
                </el-table-column>
                <el-table-column label="标签" width="200">
                    <template #default="{ row }">
                        <el-tag v-for="tag in row.tags" :key="tag" size="small" style="margin-right: 5px">
                            {{ tag }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">
                            {{ getStatusText(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="viewCount" label="浏览次数" width="100" />
                <el-table-column label="创建时间" width="180">
                    <template #default="{ row }">
                        {{ formatDate(row.createTime) }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="250" fixed="right">
                    <template #default="{ row }">
                        <el-button size="small" @click="handleView(row)">查看</el-button>
                        <el-button size="small" type="primary" @click="handleEdit(row)">
                            编辑
                        </el-button>
                        <el-button size="small" :type="row.status === 'ACTIVE' ? 'warning' : 'success'"
                            @click="handleToggleStatus(row)">
                            {{ row.status === "ACTIVE" ? "关闭" : "开放" }}
                        </el-button>
                        <el-button size="small" type="danger" @click="handleDelete(row)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize"
                :page-sizes="[10, 20, 50, 100]" :total="pagination.total"
                layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" style="margin-top: 20px; justify-content: flex-end" />
        </el-card>

        <!-- 新增/编辑对话框 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" @close="handleDialogClose">
            <scenic-form :form-data="currentRow" :is-edit="isEdit" @submit="handleFormSubmit"
                @cancel="dialogVisible = false" />
        </el-dialog>

        <!-- 查看详情对话框 -->
        <el-dialog v-model="detailVisible" title="景区详情" width="900px">
            <scenic-detail :scenic-id="currentId" v-if="detailVisible" />
        </el-dialog>
    </div>
</template>

<script setup>
import {
    deleteScenicSpot,
    getScenicSpotList,
    updateScenicSpotStatus,
} from "@/api/scenic";
import { ElMessage, ElMessageBox } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import ScenicDetail from "./ScenicDetail.vue";
import ScenicForm from "./ScenicForm.vue";

const loading = ref(false);
const tableData = ref([]);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref("");
const isEdit = ref(false);
const currentRow = ref(null);
const currentId = ref(null);

const searchForm = reactive({
    name: "",
    province: "",
    city: "",
    level: "",
    status: "",
});

const pagination = reactive({
    page: 1,
    pageSize: 10,
    total: 0,
});

// 加载数据
const loadData = async () => {
    loading.value = true;
    try {
        const params = {
            page: pagination.page - 1, // 后端从0开始
            pageSize: pagination.pageSize,
            ...searchForm,
        };

        const data = await getScenicSpotList(params);
        tableData.value = data.content;
        pagination.total = data.total;
    } catch (error) {
        ElMessage.error("加载数据失败");
    } finally {
        loading.value = false;
    }
};

// 搜索
const handleSearch = () => {
    pagination.page = 1;
    loadData();
};

// 重置
const handleReset = () => {
    Object.keys(searchForm).forEach((key) => {
        searchForm[key] = "";
    });
    handleSearch();
};

// 新增
const handleCreate = () => {
    dialogTitle.value = "新增景区";
    isEdit.value = false;
    currentRow.value = null;
    dialogVisible.value = true;
};

// 编辑
const handleEdit = (row) => {
    dialogTitle.value = "编辑景区";
    isEdit.value = true;
    currentRow.value = { ...row };
    dialogVisible.value = true;
};

// 查看
const handleView = (row) => {
    currentId.value = row.id;
    detailVisible.value = true;
};

// 切换状态
const handleToggleStatus = async (row) => {
    const newStatus = row.status === "ACTIVE" ? "INACTIVE" : "ACTIVE";
    const statusText = newStatus === "ACTIVE" ? "开放" : "关闭";

    try {
        await ElMessageBox.confirm(
            `确定要${statusText}景区"${row.name}"吗？`,
            "提示",
            {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            }
        );

        await updateScenicSpotStatus(row.id, newStatus);
        ElMessage.success(`${statusText}成功`);
        loadData();
    } catch (error) {
        if (error !== "cancel") {
            ElMessage.error("操作失败");
        }
    }
};

// 删除
const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm(
            `确定要删除景区"${row.name}"吗？此操作不可恢复！`,
            "警告",
            {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "error",
            }
        );

        await deleteScenicSpot(row.id);
        ElMessage.success("删除成功");
        loadData();
    } catch (error) {
        if (error !== "cancel") {
            ElMessage.error("删除失败");
        }
    }
};

// 表单提交
const handleFormSubmit = () => {
    dialogVisible.value = false;
    loadData();
};

// 对话框关闭
const handleDialogClose = () => {
    currentRow.value = null;
};

// 分页
const handleSizeChange = () => {
    loadData();
};

const handleCurrentChange = () => {
    loadData();
};

// 辅助函数
const getStatusType = (status) => {
    const types = {
        ACTIVE: "success",
        INACTIVE: "danger",
        MAINTENANCE: "warning",
    };
    return types[status] || "info";
};

const getStatusText = (status) => {
    const texts = {
        ACTIVE: "开放",
        INACTIVE: "关闭",
        MAINTENANCE: "维护中",
    };
    return texts[status] || status;
};

const formatDate = (dateString) => {
    if (!dateString) return "-";
    return new Date(dateString).toLocaleString("zh-CN");
};

onMounted(() => {
    loadData();
});
</script>

<style scoped>
.scenic-list-container {
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
