import request from '@/utils/request'

/**
 * 获取用户积分列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getUserPoints(params) {
  return request({
    url: '/points/list',
    method: 'get',
    params
  })
}

/**
 * 获取用户积分信息
 * @param {Number} userId - 用户ID
 * @returns {Promise}
 */
export function getPointsInfo(userId) {
  return request({
    url: '/points/info',
    method: 'get',
    params: { userId }
  })
}

/**
 * 获取积分流水
 * @param {Number} userId - 用户ID
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getPointsHistory(userId, params) {
  return request({
    url: '/points/history',
    method: 'get',
    params: {
      userId,
      ...params
    }
  })
}

/**
 * 调整用户积分
 * @param {Number} userId - 用户ID
 * @param {Object} data - 调整数据
 * @returns {Promise}
 */
export function adjustPoints(userId, data) {
  return request({
    url: '/points/adjust',
    method: 'post',
    params: { userId },
    data
  })
}

/**
 * 用户签到
 * @param {Number} userId - 用户ID
 * @returns {Promise}
 */
export function dailyCheckin(userId) {
  return request({
    url: '/points/checkin',
    method: 'post',
    params: { userId }
  })
}

/**
 * 分享获取积分
 * @param {Number} userId - 用户ID
 * @returns {Promise}
 */
export function earnFromShare(userId) {
  return request({
    url: '/points/share',
    method: 'post',
    params: { userId }
  })
}

/**
 * 积分兑换
 * @param {Number} userId - 用户ID
 * @param {Number} productId - 商品ID
 * @param {Number} points - 积分数量
 * @returns {Promise}
 */
export function exchangePoints(userId, productId, points) {
  return request({
    url: '/points/exchange',
    method: 'post',
    params: { userId, productId, points }
  })
}

/**
 * 计算积分抵扣金额
 * @param {Number} points - 积分数量
 * @returns {Promise}
 */
export function calculateDeductAmount(points) {
  return request({
    url: '/points/calculate-deduct',
    method: 'get',
    params: { points }
  })
}

/**
 * 获取积分统计
 * @returns {Promise}
 */
export function getPointsStatistics() {
  return request({
    url: '/points/statistics',
    method: 'get'
  })
}
