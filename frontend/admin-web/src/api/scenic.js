import request from "./request";

/**
 * 获取景区列表
 */
export const getScenicSpotList = (params) => {
  return request.get("/scenic-spots/list", { params });
};

/**
 * 获取景区详情
 */
export const getScenicSpotDetail = (id) => {
  return request.get(`/scenic-spots/${id}`);
};

/**
 * 创建景区
 */
export const createScenicSpot = (data) => {
  return request.post("/scenic-spots", data);
};

/**
 * 更新景区
 */
export const updateScenicSpot = (id, data) => {
  return request.put(`/scenic-spots/${id}`, data);
};

// 更新景区媒体字段（封面/图册）
export const updateScenicSpotMedia = (id, data) => {
  return request.put(`/scenic-spots/${id}/media`, data);
};

/**
 * 更新景区状态
 */
export const updateScenicSpotStatus = (id, status) => {
  return request.put(`/scenic-spots/${id}/status`, { status });
};

/**
 * 删除景区
 */
export const deleteScenicSpot = (id) => {
  return request.delete(`/scenic-spots/${id}`);
};
