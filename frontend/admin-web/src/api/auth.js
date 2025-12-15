import request from "./request";

// 管理员登录
export const login = (data) => {
  // 添加管理员登录标识
  return request.post("/users/login", { ...data, isAdminLogin: true });
};

export const register = (data) => {
  return request.post("/users/register", data);
};
