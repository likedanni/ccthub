import request from "./request";

/**
 * 获取活动列表
 */
export function getActivityList(params) {
  return request({
    url: "/activities",
    method: "get",
    params,
  });
}

/**
 * 获取活动详情
 */
export function getActivityDetail(id) {
  return request({
    url: `/activities/${id}`,
    method: "get",
  });
}

/**
 * 创建活动
 */
export function createActivity(data) {
  return request({
    url: "/activities",
    method: "post",
    data,
  });
}

/**
 * 更新活动
 */
export function updateActivity(id, data) {
  return request({
    url: `/activities/${id}`,
    method: "put",
    data,
  });
}

/**
 * 删除活动
 */
export function deleteActivity(id) {
  return request({
    url: `/activities/${id}`,
    method: "delete",
  });
}

/**
 * 审核活动
 */
export function auditActivity(id, auditStatus) {
  return request({
    url: `/activities/${id}/audit`,
    method: "put",
    params: { auditStatus },
  });
}

/**
 * 修改活动状态
 */
export function toggleActivityStatus(id, status) {
  return request({
    url: `/activities/${id}/status`,
    method: "put",
    params: { status },
  });
}

/**
 * 获取进行中的活动
 */
export function getOngoingActivities() {
  return request({
    url: "/activities/ongoing",
    method: "get",
  });
}

/**
 * 获取热门活动
 */
export function getHotActivities(limit = 10) {
  return request({
    url: "/activities/hot",
    method: "get",
    params: { limit },
  });
}
