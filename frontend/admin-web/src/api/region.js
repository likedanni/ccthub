import request from "./request";

/**
 * 获取所有省份
 */
export const getProvinces = () => {
  return request({
    url: "/regions/provinces",
    method: "get",
  });
};

/**
 * 根据省份代码获取城市列表
 */
export const getCitiesByProvince = (provinceCode) => {
  return request({
    url: "/regions/cities",
    method: "get",
    params: { provinceCode },
  });
};

/**
 * 根据城市代码获取区县列表
 */
export const getDistrictsByCity = (cityCode) => {
  return request({
    url: "/regions/districts",
    method: "get",
    params: { cityCode },
  });
};
