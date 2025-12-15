<template>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
            <el-col :span="12">
                <el-form-item label="景区名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入景区名称" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="景区等级" prop="level">
                    <el-select v-model="form.level" placeholder="请选择等级">
                        <el-option label="A级" value="A" />
                        <el-option label="AA级" value="AA" />
                        <el-option label="AAA级" value="AAA" />
                        <el-option label="AAAA级" value="AAAA" />
                        <el-option label="AAAAA级(5A)" value="AAAAA" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>

        <el-row :gutter="20">
            <el-col :span="8">
                <el-form-item label="省份" prop="province">
                    <el-input v-model="form.province" placeholder="请输入省份" />
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="城市" prop="city">
                    <el-input v-model="form.city" placeholder="请输入城市" />
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="区县" prop="district">
                    <el-input v-model="form.district" placeholder="请输入区县" />
                </el-form-item>
            </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="address">
            <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>

        <el-row :gutter="20">
            <el-col :span="12">
                <el-form-item label="经度" prop="longitude">
                    <el-input-number v-model="form.longitude" :precision="6" :step="0.000001" placeholder="请输入经度" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="纬度" prop="latitude">
                    <el-input-number v-model="form.latitude" :precision="6" :step="0.000001" placeholder="请输入纬度" />
                </el-form-item>
            </el-col>
        </el-row>

        <el-row :gutter="20">
            <el-col :span="12">
                <el-form-item label="开放时间" prop="openingHours">
                    <el-input v-model="form.openingHours" placeholder="如: 08:00-18:00" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="联系电话" prop="contactPhone">
                    <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
                </el-form-item>
            </el-col>
        </el-row>

        <el-form-item label="景区介绍" prop="introduction">
            <el-input v-model="form.introduction" type="textarea" :rows="4" placeholder="请输入景区介绍" />
        </el-form-item>

        <el-form-item label="门票信息" prop="ticketInfo">
            <el-input v-model="form.ticketInfo" type="textarea" :rows="3" placeholder="请输入门票信息" />
        </el-form-item>

        <el-form-item label="交通信息" prop="trafficInfo">
            <el-input v-model="form.trafficInfo" type="textarea" :rows="3" placeholder="请输入交通信息" />
        </el-form-item>

        <el-form-item label="游玩须知" prop="notice">
            <el-input v-model="form.notice" type="textarea" :rows="3" placeholder="请输入游玩须知" />
        </el-form-item>

        <el-form-item label="景区标签" prop="tags">
            <el-select v-model="form.tags" multiple filterable allow-create placeholder="请选择或输入标签" style="width: 100%">
                <el-option label="自然风光" value="自然风光" />
                <el-option label="文化古迹" value="文化古迹" />
                <el-option label="红色旅游" value="红色旅游" />
                <el-option label="峡谷" value="峡谷" />
                <el-option label="山水" value="山水" />
                <el-option label="徒步" value="徒步" />
                <el-option label="历史文化" value="历史文化" />
                <el-option label="爱国教育" value="爱国教育" />
            </el-select>
        </el-form-item>

        <el-form-item label="景区设施" prop="facilities">
            <el-select v-model="form.facilities" multiple filterable allow-create placeholder="请选择或输入设施"
                style="width: 100%">
                <el-option label="停车场" value="停车场" />
                <el-option label="游客中心" value="游客中心" />
                <el-option label="餐厅" value="餐厅" />
                <el-option label="卫生间" value="卫生间" />
                <el-option label="观光车" value="观光车" />
                <el-option label="索道" value="索道" />
                <el-option label="游船" value="游船" />
                <el-option label="纪念品商店" value="纪念品商店" />
            </el-select>
        </el-form-item>

        <el-form-item label="封面图片" prop="coverImage">
            <el-input v-model="form.coverImage" placeholder="请输入图片URL或上传" />
            <el-upload action="/api/files/upload" :show-file-list="false" :on-success="handleCoverSuccess"
                :headers="{ Authorization: `Bearer ${getToken()}` }" style="margin-top: 10px">
                <el-button size="small" type="primary">上传封面</el-button>
            </el-upload>
        </el-form-item>

        <el-form-item label="景区图册" prop="images">
            <div style="display: flex; flex-wrap: wrap; gap: 10px">
                <div v-for="(img, index) in form.images" :key="index" style="position: relative">
                    <el-image :src="img" style="width: 100px; height: 100px" fit="cover" />
                    <el-button size="small" type="danger" circle @click="removeImage(index)"
                        style="position: absolute; top: -10px; right: -10px">
                        ×
                    </el-button>
                </div>
            </div>
            <el-upload action="/api/files/upload" :show-file-list="false" :on-success="handleImageSuccess"
                :headers="{ Authorization: `Bearer ${getToken()}` }" style="margin-top: 10px">
                <el-button size="small" type="primary">添加图片</el-button>
            </el-upload>
        </el-form-item>

        <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
                <el-radio label="ACTIVE">开放</el-radio>
                <el-radio label="INACTIVE">关闭</el-radio>
                <el-radio label="MAINTENANCE">维护中</el-radio>
            </el-radio-group>
        </el-form-item>

        <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
                提交
            </el-button>
            <el-button @click="$emit('cancel')">取消</el-button>
        </el-form-item>
    </el-form>
</template>

<script setup>
import { createScenicSpot, updateScenicSpot } from "@/api/scenic";
import { ElMessage } from "element-plus";
import { reactive, ref, watch } from "vue";

const props = defineProps({
    formData: Object,
    isEdit: Boolean,
});

const emit = defineEmits(["submit", "cancel"]);

const formRef = ref();
const submitting = ref(false);

const form = reactive({
    name: "",
    level: "",
    introduction: "",
    province: "",
    city: "",
    district: "",
    address: "",
    longitude: null,
    latitude: null,
    openingHours: "",
    contactPhone: "",
    coverImage: "",
    images: [],
    tags: [],
    facilities: [],
    ticketInfo: "",
    trafficInfo: "",
    notice: "",
    status: "ACTIVE",
});

const rules = {
    name: [{ required: true, message: "请输入景区名称", trigger: "blur" }],
    province: [{ required: true, message: "请输入省份", trigger: "blur" }],
    city: [{ required: true, message: "请输入城市", trigger: "blur" }],
    address: [{ required: true, message: "请输入详细地址", trigger: "blur" }],
};

// 监听formData变化
watch(
    () => props.formData,
    (newVal) => {
        if (newVal) {
            Object.assign(form, {
                ...newVal,
                images: newVal.images || [],
                tags: newVal.tags || [],
                facilities: newVal.facilities || [],
            });
        }
    },
    { immediate: true }
);

// 获取token
const getToken = () => {
    return localStorage.getItem("token") || "";
};

// 封面上传成功
const handleCoverSuccess = (response) => {
    if (response.code === 200) {
        form.coverImage = response.data.url;
        ElMessage.success("上传成功");
    } else {
        ElMessage.error("上传失败");
    }
};

// 图片上传成功
const handleImageSuccess = (response) => {
    if (response.code === 200) {
        if (!form.images) {
            form.images = [];
        }
        form.images.push(response.data.url);
        ElMessage.success("上传成功");
    } else {
        ElMessage.error("上传失败");
    }
};

// 移除图片
const removeImage = (index) => {
    form.images.splice(index, 1);
};

// 提交
const handleSubmit = async () => {
    try {
        await formRef.value.validate();

        submitting.value = true;

        if (props.isEdit) {
            await updateScenicSpot(props.formData.id, form);
            ElMessage.success("更新成功");
        } else {
            await createScenicSpot(form);
            ElMessage.success("创建成功");
        }

        emit("submit");
    } catch (error) {
        if (error !== false) {
            ElMessage.error("操作失败");
        }
    } finally {
        submitting.value = false;
    }
};
</script>

<style scoped>
.el-input-number {
    width: 100%;
}
</style>
