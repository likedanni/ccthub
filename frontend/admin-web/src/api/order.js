import request from "./request";

/**
 * 订单API
 */

// 创建订单
export function createOrder(data) {
  return request({
    url: "/orders",
    method: "post",
    data,
  });
}

// 支付订单
export function payOrder(id) {
  return request({
    url: `/orders/${id}/pay`,
    method: "post",
  });
}

// 取消订单
export function cancelOrder(id) {
  return request({
    url: `/orders/${id}/cancel`,
    method: "post",
  });
}

// 查询订单详情
export function getOrderDetail(id) {
  return request({
    url: `/orders/${id}`,
    method: "get",
  });
}

// 根据订单号查询
export function getOrderByOrderNo(orderNo) {
  return request({
    url: `/orders/by-no/${orderNo}`,
    method: "get",
  });
}

// 查询用户订单
export function getUserOrders(userId) {
  return request({
    url: `/orders/user/${userId}`,
    method: "get",
  });
}

// 查询所有订单
export function getAllOrders() {
  return request({
    url: "/orders",
    method: "get",
  });
}

// 查询核销码信息
export function getVerificationInfo(verificationCode) {
  return request({
    url: `/verifications/${verificationCode}`,
    method: "get",
  });
}

// 核销票券
export function verifyTicket(verificationCode, staffId) {
  return request({
    url: `/verifications/${verificationCode}/verify`,
    method: "post",
    params: { staffId },
  });
}

// 批量核销
export function batchVerify(orderId, staffId) {
  return request({
    url: `/verifications/batch/${orderId}`,
    method: "post",
    params: { staffId },
  });
}
