import request from "./request";

export const getUserProfile = (userId) => {
  return request.get(`/users/${userId}/profile`);
};

export const updateUserProfile = (userId, data) => {
  return request.put(`/users/${userId}/profile`, data);
};

export const changePassword = (userId, data) => {
  return request.post(`/users/${userId}/change-password`, data);
};

export const setPaymentPassword = (userId, data) => {
  return request.post(`/users/${userId}/payment-password`, data);
};
