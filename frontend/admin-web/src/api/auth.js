import request from "./request";

export const login = (data) => {
  return request.post("/users/login", data);
};

export const register = (data) => {
  return request.post("/users/register", data);
};
