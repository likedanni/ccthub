<template>
    <div v-loading="loading">
        <el-descriptions :column="2" border v-if="detail">
            <el-descriptions-item label="景区ID">{{ detail.id }}</el-descriptions-item>
            <el-descriptions-item label="景区名称">{{ detail.name }}</el-descriptions-item>
            <el-descriptions-item label="景区等级">{{ detail.level || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
                <el-tag :type="getStatusType(detail.status)">
                    {{ getStatusText(detail.status) }}
                </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="省份">{{ detail.province }}</el-descriptions-item>
            <el-descriptions-item label="城市">{{ detail.city }}</el-descriptions-item>
            <el-descriptions-item label="区县">{{ detail.district || '-' }}</el-descriptions-item>
            <el-descriptions-item label="详细地址">{{ detail.address }}</el-descriptions-item>
            <el-descriptions-item label="经度">{{ detail.longitude || '-' }}</el-descriptions-item>
            <el-descriptions-item label="纬度">{{ detail.latitude || '-' }}</el-descriptions-item>
            <el-descriptions-item label="开放时间">{{ detail.openingHours || '-' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detail.contactPhone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="浏览次数">{{ detail.viewCount }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDate(detail.createTime) }}</el-descriptions-item>

            <el-descriptions-item label="景区标签" :span="2">
                <el-tag v-for="tag in detail.tags" :key="tag" style="margin-right: 5px">
                    {{ tag }}
                </el-tag>
                <span v-if="!detail.tags || detail.tags.length === 0">-</span>
            </el-descriptions-item>

            <el-descriptions-item label="景区设施" :span="2">
                <el-tag v-for="facility in detail.facilities" :key="facility" type="success" style="margin-right: 5px">
                    {{ facility }}
                </el-tag>
                <span v-if="!detail.facilities || detail.facilities.length === 0">-</span>
            </el-descriptions-item>

            <el-descriptions-item label="景区介绍" :span="2">
                <div style="white-space: pre-wrap">{{ detail.introduction || '-' }}</div>
            </el-descriptions-item>

            <el-descriptions-item label="门票信息" :span="2">
                <div style="white-space: pre-wrap">{{ detail.ticketInfo || '-' }}</div>
            </el-descriptions-item>

            <el-descriptions-item label="交通信息" :span="2">
                <div style="white-space: pre-wrap">{{ detail.trafficInfo || '-' }}</div>
            </el-descriptions-item>

            <el-descriptions-item label="游玩须知" :span="2">
                <div style="white-space: pre-wrap">{{ detail.notice || '-' }}</div>
            </el-descriptions-item>

            <el-descriptions-item label="封面图片" :span="2">
                <el-image v-if="detail.coverImage" :src="detail.coverImage" style="width: 200px; height: 150px"
                    fit="cover" :preview-src-list="[detail.coverImage]" />
                <span v-else>-</span>
            </el-descriptions-item>

            <el-descriptions-item label="景区图册" :span="2">
                <div v-if="detail.images && detail.images.length > 0" style="display: flex; flex-wrap: wrap; gap: 10px">
                    <el-image v-for="(img, index) in detail.images" :key="index" :src="img"
                        style="width: 120px; height: 90px" fit="cover" :preview-src-list="detail.images"
                        :initial-index="index" />
                </div>
                <span v-else>-</span>
            </el-descriptions-item>
        </el-descriptions>
    </div>
</template>

<script setup>
import { getScenicSpotDetail } from '@/api/scenic';
import { ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';

const props = defineProps({
    scenicId: {
        type: Number,
        required: true
    }
});

const loading = ref(false);
const detail = ref(null);

const loadDetail = async () => {
    loading.value = true;
    try {
        detail.value = await getScenicSpotDetail(props.scenicId);
    } catch (error) {
        ElMessage.error('加载详情失败');
    } finally {
        loading.value = false;
    }
};

const getStatusType = (status) => {
    const types = {
        ACTIVE: 'success',
        INACTIVE: 'danger',
        MAINTENANCE: 'warning'
    };
    return types[status] || 'info';
};

const getStatusText = (status) => {
    const texts = {
        ACTIVE: '开放',
        INACTIVE: '关闭',
        MAINTENANCE: '维护中'
    };
    return texts[status] || status;
};

const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleString('zh-CN');
};

onMounted(() => {
    loadDetail();
});
</script>
