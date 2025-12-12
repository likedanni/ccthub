import request from "../utils/request";

export const login = (data) => {
  return request({
    url: "/users/login",
    method: "POST",
    data,
  });
};

export const register = (data) => {
  return request({
    url: "/users/register",
    method: "POST",
    data,
  });
};

export const wxLogin = (code) => {
  return request({
    url: "/users/wx-login",
    method: "POST",
    data: { code },
  });
};
