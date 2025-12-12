import request from '../utils/request'

export const getUserProfile = (userId) => {
  return request({
    url: `/users/${userId}/profile`,
    method: 'GET'
  })
}

export const updateUserProfile = (userId, data) => {
  return request({
    url: `/users/${userId}/profile`,
    method: 'PUT',
    data
  })
}

export const changePassword = (userId, data) => {
  return request({
    url: `/users/${userId}/change-password`,
    method: 'POST',
    data
  })
}

export const setPaymentPassword = (userId, data) => {
  return request({
    url: `/users/${userId}/payment-password`,
    method: 'POST',
    data
  })
}
