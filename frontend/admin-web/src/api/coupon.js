import request from "./request";

/**
 * 获取优惠券列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getCoupons(params) {
  return request({
    url: "/coupons/list",
    method: "get",
    params,
  });
}

/**
 * 获取优惠券详情
 * @param {Number} id - 优惠券ID
 * @returns {Promise}
 */
export function getCouponDetail(id) {
  return request({
    url: `/coupons/${id}`,
    method: "get",
  });
}

/**
 * 创建优惠券
 * @param {Object} data - 优惠券数据
 * @returns {Promise}
 */
export function createCoupon(data) {
  return request({
    url: "/coupons",
    method: "post",
    data,
  });
}

/**
 * 更新优惠券
 * @param {Number} id - 优惠券ID
 * @param {Object} data - 优惠券数据
 * @returns {Promise}
 */
export function updateCoupon(id, data) {
  return request({
    url: `/coupons/${id}`,
    method: "put",
    data,
  });
}

/**
 * 删除优惠券
 * @param {Number} id - 优惠券ID
 * @returns {Promise}
 */
export function deleteCoupon(id) {
  return request({
    url: `/coupons/${id}`,
    method: "delete",
  });
}

/**
 * 更新优惠券状态
 * @param {Number} id - 优惠券ID
 * @param {Number} status - 状态
 * @returns {Promise}
 */
export function updateCouponStatus(id, status) {
  return request({
    url: `/coupons/${id}/status`,
    method: "put",
    params: { status },
  });
}

/**
 * 发放优惠券
 * @param {Number} couponId - 优惠券ID
 * @param {Object} data - 发放数据
 * @returns {Promise}
 */
export function grantCoupon(couponId, data) {
  return request({
    url: `/coupons/${couponId}/grant`,
    method: "post",
    data,
  });
}

/**
 * 获取用户优惠券列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getUserCoupons(params) {
  return request({
    url: "/coupons/user",
    method: "get",
    params,
  });
}

/**
 * 获取用户可用优惠券
 * @param {Number} userId - 用户ID
 * @param {Number} amount - 订单金额
 * @returns {Promise}
 */
export function getAvailableCoupons(userId, amount) {
  return request({
    url: "/coupons/available",
    method: "get",
    params: { userId, amount },
  });
}

/**
 * 使用优惠券
 * @param {Number} userCouponId - 用户优惠券ID
 * @param {String} orderNo - 订单号
 * @returns {Promise}
 */
export function useCoupon(userCouponId, orderNo) {
  return request({
    url: `/coupons/user/${userCouponId}/use`,
    method: "post",
    params: { orderNo },
  });
}

/**
 * 获取用户优惠券统计
 * @param {Number} userId - 用户ID
 * @returns {Promise}
 */
export function getUserCouponStats(userId) {
  return request({
    url: "/coupons/user/stats",
    method: "get",
    params: { userId },
  });
}
