import request from "@/utils/request";

/**
 * 票种管理API
 */

// 创建票种
export function createTicket(data) {
  return request({
    url: "/api/tickets",
    method: "post",
    data,
  });
}

// 更新票种
export function updateTicket(id, data) {
  return request({
    url: `/api/tickets/${id}`,
    method: "put",
    data,
  });
}

// 删除票种
export function deleteTicket(id) {
  return request({
    url: `/api/tickets/${id}`,
    method: "delete",
  });
}

// 获取票种详情
export function getTicket(id) {
  return request({
    url: `/api/tickets/${id}`,
    method: "get",
  });
}

// 分页查询票种
export function getTickets(params) {
  return request({
    url: "/api/tickets",
    method: "get",
    params,
  });
}

// 根据景区查询票种
export function getTicketsByScenicSpot(scenicSpotId) {
  return request({
    url: `/api/tickets/scenic-spot/${scenicSpotId}`,
    method: "get",
  });
}

// 根据景区和状态查询票种
export function getTicketsByScenicSpotAndStatus(scenicSpotId, status) {
  return request({
    url: `/api/tickets/scenic-spot/${scenicSpotId}/status/${status}`,
    method: "get",
  });
}

// 更新票种状态
export function updateTicketStatus(id, status) {
  return request({
    url: `/api/tickets/${id}/status`,
    method: "patch",
    params: { status },
  });
}

/**
 * 票价管理API
 */

// 创建或更新票价
export function saveTicketPrice(data) {
  return request({
    url: "/api/ticket-prices",
    method: "post",
    data,
  });
}

// 批量设置票价
export function batchSaveTicketPrices(data) {
  return request({
    url: "/api/ticket-prices/batch",
    method: "post",
    data,
  });
}

// 删除票价
export function deleteTicketPrice(id) {
  return request({
    url: `/api/ticket-prices/${id}`,
    method: "delete",
  });
}

// 获取票价详情
export function getTicketPrice(id) {
  return request({
    url: `/api/ticket-prices/${id}`,
    method: "get",
  });
}

// 根据票种查询所有票价
export function getTicketPricesByTicket(ticketId) {
  return request({
    url: `/api/ticket-prices/ticket/${ticketId}`,
    method: "get",
  });
}

// 根据日期范围查询票价
export function getTicketPricesByDateRange(ticketId, startDate, endDate) {
  return request({
    url: `/api/ticket-prices/ticket/${ticketId}/date-range`,
    method: "get",
    params: { startDate, endDate },
  });
}
