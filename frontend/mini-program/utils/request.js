const BASE_URL = "http://localhost:8080/api";

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync("token");

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || "GET",
      data: options.data || {},
      header: {
        "Content-Type": "application/json",
        Authorization: token ? `Bearer ${token}` : "",
      },
      success: (res) => {
        if (res.statusCode === 200) {
          // 如果返回的是标准的 ApiResponse 格式
          if (res.data.code !== undefined) {
            if (res.data.code === 200) {
              resolve(res.data.data);
            } else {
              uni.showToast({
                title: res.data.message || "请求失败",
                icon: "none",
              });
              reject(new Error(res.data.message || "请求失败"));
            }
          } else {
            // 直接返回数据
            resolve(res.data);
          }
        } else if (res.statusCode === 401) {
          uni.showToast({
            title: "登录已过期",
            icon: "none",
          });
          uni.removeStorageSync("token");
          uni.navigateTo({
            url: "/pages/login/index",
          });
          reject(new Error("未授权"));
        } else {
          uni.showToast({
            title: "请求失败",
            icon: "none",
          });
          reject(new Error("请求失败"));
        }
      },
      fail: (err) => {
        uni.showToast({
          title: "网络错误",
          icon: "none",
        });
        reject(err);
      },
    });
  });
};

export default request;
