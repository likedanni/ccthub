import request from "./request";

/**
 * 获取秒杀列表
 */
export function getSeckillList(params) {
  return request({
    url: "/seckills",
    method: "get",
    params,
  });
}

/**
 * 获取秒杀详情
 */
export function getSeckillDetail(id) {
  return request({
    url: `/seckills/${id}`,
    method: "get",
  });
}

/**
 * 创建秒杀
 */
export function createSeckill(data) {
  return request({
    url: "/seckills",
    method: "post",
    data,
  });
}

/**
 * 更新秒杀
 */
export function updateSeckill(id, data) {
  return request({
    url: `/seckills/${id}`,
    method: "put",
    data,
  });
}

/**
 * 删除秒杀
 */
export function deleteSeckill(id) {
  return request({
    url: `/seckills/${id}`,
    method: "delete",
  });
}

/**
 * 修改秒杀状态
 */
export function toggleSeckillStatus(id, status) {
  return request({
    url: `/seckills/${id}/status`,
    method: "put",
    params: { status },
  });
}

/**
 * 获取进行中的秒杀
 */
export function getOngoingSeckills() {
  return request({
    url: "/seckills/ongoing",
    method: "get",
  });
}

/**
 * 处理秒杀购买
 */
export function processPurchase(id, userId, quantity = 1) {
  return request({
    url: `/seckills/${id}/purchase`,
    method: "post",
    params: { userId, quantity },
  });
}
