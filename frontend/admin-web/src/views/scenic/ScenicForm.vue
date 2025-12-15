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
                    <el-select v-model="formProvinceCode" placeholder="请选择省份" @change="handleProvinceChange"
                        style="width: 100%">
                        <el-option v-for="province in provinces" :key="province.code" :label="province.name"
                            :value="province.code" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="城市" prop="city">
                    <el-select v-model="formCityCode" placeholder="请选择城市" :disabled="!formProvinceCode"
                        @change="handleCityChange" style="width: 100%">
                        <el-option v-for="city in cities" :key="city.code" :label="city.name" :value="city.code" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="区县" prop="district">
                    <el-select v-model="form.district" placeholder="请选择区县" :disabled="!formCityCode"
                        style="width: 100%">
                        <el-option v-for="district in districts" :key="district.code" :label="district.name"
                            :value="district.name" />
                    </el-select>
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
            <div v-if="form.coverImage" style="margin-bottom: 10px">
                <el-image :src="form.coverImage" :preview-src-list="[form.coverImage]"
                    style="width: 200px; height: 150px" fit="cover" />
            </div>
            <el-upload
                :action="uploadAction()"
                :data="{ category: 'scenic' }"
                :show-file-list="false"
                :on-success="handleCoverSuccess"
                :headers="uploadHeaders(getToken)"
                accept="image/*"
                :before-upload="beforeUpload">
                <el-button size="small" type="primary">{{ form.coverImage ? '更换封面' : '上传封面' }}</el-button>
            </el-upload>
        </el-form-item>

        <el-form-item label="景区图册" prop="images">
            <div style="display: flex; flex-wrap: wrap; gap: 10px">
                <div v-for="(img, index) in form.images" :key="index" style="position: relative">
                    <el-image :src="img" :preview-src-list="form.images" :initial-index="index"
                        style="width: 100px; height: 100px" fit="cover" />
                    <el-button size="small" type="danger" circle @click="removeImage(index)"
                        style="position: absolute; top: -10px; right: -10px">
                        ×
                    </el-button>
                </div>
            </div>
            <el-upload
                :action="uploadAction()"
                :data="{ category: 'scenic' }"
                :show-file-list="false"
                :on-success="handleImageSuccess"
                :headers="uploadHeaders(getToken)"
                accept="image/*"
                :before-upload="beforeUpload"
                style="margin-top: 10px">
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
import { getCitiesByProvince, getDistrictsByCity, getProvinces } from "@/api/region";
import { createScenicSpot, updateScenicSpot } from "@/api/scenic";
import { parseUploadResponse, uploadAction, uploadHeaders, beforeUpload as utilBeforeUpload } from "@/utils/upload";
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref, watch } from "vue";

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

// 省市区数据
const provinces = ref([]);
const cities = ref([]);
const districts = ref([]);
const formProvinceCode = ref("");
const formCityCode = ref("");

const rules = {
    name: [{ required: true, message: "请输入景区名称", trigger: "blur" }],
    province: [{ required: true, message: "请选择省份", trigger: "change" }],
    city: [{ required: true, message: "请选择城市", trigger: "change" }],
    address: [{ required: true, message: "请输入详细地址", trigger: "blur" }],
};

// 加载省份列表
const loadProvinces = async () => {
    try {
        const data = await getProvinces();
        provinces.value = data;
    } catch (error) {
        console.error('加载省份列表失败', error);
    }
};

// 回填省市区（在 formData 和 provinces 可用时调用）
const backfillLocation = async (source) => {
    // source 可以是 props.formData 或 form
    const src = source || props.formData || form;
    if (!src) return;

    try {
        if (src.province && provinces.value.length > 0) {
            const province = provinces.value.find(p => p.name === src.province);
            if (province) {
                formProvinceCode.value = province.code;
                const citiesData = await getCitiesByProvince(province.code);
                cities.value = citiesData;

                if (src.city) {
                    const city = citiesData.find(c => c.name === src.city);
                    if (city) {
                        formCityCode.value = city.code;
                        const districtsData = await getDistrictsByCity(city.code);
                        districts.value = districtsData;
                    }
                }
            }
        }
    } catch (err) {
        console.error('回填省市区失败', err);
    }
};

// 省份变化处理
const handleProvinceChange = async (provinceCode) => {
    formCityCode.value = '';
    form.city = '';
    form.district = '';
    cities.value = [];
    districts.value = [];

    if (provinceCode) {
        const selectedProvince = provinces.value.find(p => p.code === provinceCode);
        form.province = selectedProvince ? selectedProvince.name : '';

        try {
            const data = await getCitiesByProvince(provinceCode);
            cities.value = data;
        } catch (error) {
            console.error('加载城市列表失败', error);
        }
    } else {
        form.province = '';
    }
};

// 城市变化处理
const handleCityChange = async (cityCode) => {
    form.district = '';
    districts.value = [];

    if (cityCode) {
        const selectedCity = cities.value.find(c => c.code === cityCode);
        form.city = selectedCity ? selectedCity.name : '';

        try {
            const data = await getDistrictsByCity(cityCode);
            districts.value = data;
        } catch (error) {
            console.error('加载区县列表失败', error);
        }
    } else {
        form.city = '';
    }
};

// 重置表单为初始空值（用于新增时清空旧数据）
const resetForm = () => {
    form.name = "";
    form.level = "";
    form.introduction = "";
    form.province = "";
    form.city = "";
    form.district = "";
    form.address = "";
    form.longitude = null;
    form.latitude = null;
    form.openingHours = "";
    form.contactPhone = "";
    form.coverImage = "";
    form.images = [];
    form.tags = [];
    form.facilities = [];
    form.ticketInfo = "";
    form.trafficInfo = "";
    form.notice = "";
    form.status = "ACTIVE";

    formProvinceCode.value = '';
    formCityCode.value = '';
    cities.value = [];
    districts.value = [];

    if (formRef.value && formRef.value.clearValidate) {
        formRef.value.clearValidate();
    }
};

// 监听formData变化
watch(
    () => props.formData,
    async (newVal) => {
        console.debug('ScenicForm watch triggered, formData id:', newVal && newVal.id, 'provinces loaded:', provinces.value && provinces.value.length);
        if (!newVal) {
            // 新增模式或清空时，重置表单，避免残留上一次编辑的数据
            resetForm();
            return;
        }

        Object.assign(form, {
            ...newVal,
            images: newVal.images || [],
            tags: newVal.tags || [],
            facilities: newVal.facilities || [],
        });
        await backfillLocation(newVal);
    },
    { immediate: true }
);

// 获取token
const getToken = () => {
    return localStorage.getItem("token") || "";
};

// 使用共享上传工具的校验与响应解析
const beforeUpload = (file) => {
    const ok = utilBeforeUpload(file);
    if (!ok) {
        ElMessage.error('请上传图片文件且大小不超过 5MB');
    }
    return ok;
};

// 封面上传成功
const handleCoverSuccess = (response) => {
    console.debug('handleCoverSuccess response:', response);
    const url = parseUploadResponse(response);
    if (url) {
        form.coverImage = url;
        ElMessage.success('上传成功');
        // 不在此处自动提交或关闭弹窗；只更新表单中的封面字段，用户需点击“提交”保存到后端
    } else {
        console.warn('unexpected upload response', response);
        ElMessage.error('上传失败');
    }
};

// 图片上传成功
const handleImageSuccess = (response) => {
    console.debug('handleImageSuccess response:', response);
    const url = parseUploadResponse(response);
    if (url) {
        if (!form.images) form.images = [];
        form.images.push(url);
        ElMessage.success('上传成功');
        // 仅更新表单内的 images 数组，用户点击“提交”时会一并保存
    } else {
        console.warn('unexpected upload response', response);
        ElMessage.error('上传失败');
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

        console.debug('Submitting scenic form, isEdit:', props.isEdit, 'payload:', JSON.parse(JSON.stringify(form)));
        if (props.isEdit) {
            const resp = await updateScenicSpot(props.formData.id, form);
            console.debug('updateScenicSpot response:', resp);
            ElMessage.success("更新成功");
        } else {
            const resp = await createScenicSpot(form);
            console.debug('createScenicSpot response:', resp);
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

// 组件挂载时加载省份列表
onMounted(() => {
    loadProvinces();
});

// 当省份加载完成后，尝试回填（处理先打开表单后加载省份的情况）
watch(provinces, async (val) => {
    if (val && val.length > 0) {
        await backfillLocation(props.formData || form);
    }
});
</script>

<style scoped>
.el-input-number {
    width: 100%;
}
</style>
