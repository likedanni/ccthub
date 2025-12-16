import request from '@/utils/request'

/**
 * 获取钱包列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getWallets(params) {
  return request({
    url: '/wallet/list',
    method: 'get',
    params
  })
}

/**
 * 获取钱包信息
 * @param {Number} userId - 用户ID
 * @returns {Promise}
 */
export function getWalletInfo(userId) {
  return request({
    url: '/wallet/info',
    method: 'get',
    params: { userId }
  })
}

/**
 * 获取钱包流水
 * @param {Number} userId - 用户ID
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getWalletTransactions(userId, params) {
  return request({
    url: '/wallet/transactions',
    method: 'get',
    params: {
      userId,
      ...params
    }
  })
}

/**
 * 钱包充值
 * @param {Number} userId - 用户ID
 * @param {Object} data - 充值数据
 * @returns {Promise}
 */
export function rechargeWallet(userId, data) {
  return request({
    url: '/wallet/recharge',
    method: 'post',
    params: { userId },
    data
  })
}

/**
 * 冻结/解冻钱包
 * @param {Number} userId - 用户ID
 * @param {Number} status - 状态 (0-冻结, 1-正常)
 * @returns {Promise}
 */
export function updateWalletStatus(userId, status) {
  return request({
    url: '/wallet/status',
    method: 'put',
    params: { userId, status }
  })
}

/**
 * 设置支付密码
 * @param {Number} userId - 用户ID
 * @param {Object} data - 密码数据
 * @returns {Promise}
 */
export function setPayPassword(userId, data) {
  return request({
    url: '/wallet/pay-password/set',
    method: 'post',
    params: { userId },
    data
  })
}

/**
 * 修改支付密码
 * @param {Number} userId - 用户ID
 * @param {String} oldPassword - 旧密码
 * @param {Object} data - 新密码数据
 * @returns {Promise}
 */
export function changePayPassword(userId, oldPassword, data) {
  return request({
    url: '/wallet/pay-password/change',
    method: 'post',
    params: { userId, oldPassword },
    data
  })
}
