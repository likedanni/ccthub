import request from './request'

/**
 * 创建支付订单
 */
export function createPayment(data) {
  return request({
    url: "/api/payments",
    method: "post",
    data,
  });
}

/**
 * 查询支付信息
 */
export function getPayment(paymentNo) {
  return request({
    url: `/api/payments/${paymentNo}`,
    method: "get",
  });
}

/**
 * 关闭支付订单
 */
export function closePayment(paymentNo) {
  return request({
    url: `/api/payments/${paymentNo}/close`,
    method: "put",
  });
}

/**
 * 分页查询支付列表
 */
export function getPayments(params) {
  return request({
    url: "/api/payments",
    method: "get",
    params,
  });
}

/**
 * 获取支付统计
 */
export function getPaymentStatistics() {
  return request({
    url: "/api/payments/statistics",
    method: "get",
  });
}

/**
 * 支付回调
 */
export function paymentCallback(paymentNo, data) {
  return request({
    url: `/api/payments/callback/${paymentNo}`,
    method: "post",
    data,
  });
}
