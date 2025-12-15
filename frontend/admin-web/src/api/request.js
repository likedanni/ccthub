import router from "@/router";
import axios from "axios";
import { ElMessage } from "element-plus";

const request = axios.create({
  baseURL: "/api",
  timeout: 10000,
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data;

    // 如果返回的是标准的 ApiResponse 格式
    if (res.code !== undefined) {
      if (res.code === 200) {
        return res.data;
      } else {
        ElMessage.error(res.message || "请求失败");
        return Promise.reject(new Error(res.message || "请求失败"));
      }
    }

    // 如果是直接返回的数据
    return res;
  },
  (error) => {
    if (error.response) {
      const status = error.response.status;
      const config = error.config;

      if (status === 401) {
        // 如果是登录接口返回401，说明是管理员权限不足
        if (config.url && config.url.includes("/users/login")) {
          ElMessage.error("请使用管理员账户登录");
        } else {
          // 其他接口401，说明token过期
          ElMessage.error("登录已过期，请重新登录");
          localStorage.removeItem("token");
          router.push("/login");
        }
      } else if (status === 403) {
        ElMessage.error("没有权限访问");
      } else if (status === 404) {
        ElMessage.error("请求的资源不存在");
      } else if (status >= 500) {
        ElMessage.error("服务器错误");
      }
    } else {
      ElMessage.error(error.message || "网络错误");
    }
    return Promise.reject(error);
  }
);

export default request;
