import request from "./request";

// 获取仪表盘统计数据
export const getDashboardStats = () => {
  return request.get("/users/dashboard/stats");
};

// 获取用户列表
export const getUserList = (params) => {
  return request.get("/users/list", { params });
};

// 获取用户详情
export const getUserProfile = (userId) => {
  return request.get(`/users/${userId}/profile`);
};

// 更新用户资料
export const updateUserProfile = (userId, data) => {
  return request.put(`/users/${userId}/profile`, data);
};

// 更新用户状态
export const updateUserStatus = (userId, status) => {
  return request.put(`/users/${userId}/status`, { status });
};

// 修改密码
export const changePassword = (userId, data) => {
  return request.post(`/users/${userId}/change-password`, data);
};

// 设置支付密码
export const setPaymentPassword = (userId, data) => {
  return request.post(`/users/${userId}/payment-password`, data);
};
