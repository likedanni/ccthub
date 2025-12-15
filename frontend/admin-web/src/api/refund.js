import request from "./request";

/**
 * 创建退款申请
 */
export function createRefund(data) {
  return request({
    url: "/refunds",
    method: "post",
    data,
  });
}

/**
 * 审核退款申请
 */
export function auditRefund(data) {
  return request({
    url: "/refunds/audit",
    method: "put",
    data,
  });
}

/**
 * 查询退款信息
 */
export function getRefund(refundNo) {
  return request({
    url: `/refunds/${refundNo}`,
    method: "get",
  });
}

/**
 * 分页查询退款列表
 */
export function getRefunds(params) {
  return request({
    url: "/refunds",
    method: "get",
    params,
  });
}

/**
 * 获取退款统计
 */
export function getRefundStatistics() {
  return request({
    url: "/refunds/statistics",
    method: "get",
  });
}
