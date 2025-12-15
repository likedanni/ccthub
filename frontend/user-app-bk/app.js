// app.js
App({
  /**
   * 全局数据
   */
  globalData: {
    userInfo: null,
    token: null,
    apiBaseUrl: "https://api.example.com", // API基础地址
    version: "1.0.0",
  },

  /**
   * 小程序初始化
   */
  onLaunch: function () {
    console.log("小程序启动");
    this.checkLogin();
    this.checkUpdate();
  },

  /**
   * 小程序显示
   */
  onShow: function () {
    console.log("小程序显示");
  },

  /**
   * ==================== 导航方法 ====================
   */

  /**
   * 页面跳转 - navigateTo
   * @param {string} url - 目标页面路径
   * @param {object} params - 可选参数对象
   */
  navigateTo: function (url, params = {}) {
    const queryString = this.buildQueryString(params);
    const fullUrl = queryString ? `${url}?${queryString}` : url;

    wx.navigateTo({
      url: fullUrl,
      fail: (err) => {
        console.error("页面跳转失败:", err);
        this.showToast("页面跳转失败", "error");
      },
    });
  },

  /**
   * 页面跳转 - redirectTo
   */
  redirectTo: function (url, params = {}) {
    const queryString = this.buildQueryString(params);
    const fullUrl = queryString ? `${url}?${queryString}` : url;

    wx.redirectTo({
      url: fullUrl,
      fail: (err) => {
        console.error("页面重定向失败:", err);
      },
    });
  },

  /**
   * 返回上一页
   * @param {number} delta - 返回的页面数
   */
  navigateBack: function (delta = 1) {
    wx.navigateBack({
      delta: delta,
      fail: () => {
        // 如果返回失败,跳转到首页
        wx.switchTab({
          url: "/pages/home/index",
        });
      },
    });
  },

  /**
   * 构建查询字符串
   */
  buildQueryString: function (params) {
    return Object.keys(params)
      .map(
        (key) => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`
      )
      .join("&");
  },

  /**
   * ==================== 提示方法 ====================
   */

  /**
   * 显示Toast提示
   * @param {string} title - 提示文字
   * @param {string} icon - 图标类型: success, error, loading, none
   * @param {number} duration - 持续时间(ms)
   */
  showToast: function (title, icon = "none", duration = 2000) {
    wx.showToast({
      title: title,
      icon: icon,
      duration: duration,
    });
  },

  /**
   * 显示Loading
   * @param {string} title - 提示文字
   */
  showLoading: function (title = "加载中...") {
    wx.showLoading({
      title: title,
      mask: true,
    });
  },

  /**
   * 隐藏Loading
   */
  hideLoading: function () {
    wx.hideLoading();
  },

  /**
   * 显示模态对话框
   */
  showModal: function (options = {}) {
    const defaultOptions = {
      title: "提示",
      content: "",
      showCancel: true,
      confirmText: "确定",
      cancelText: "取消",
    };

    return new Promise((resolve, reject) => {
      wx.showModal({
        ...defaultOptions,
        ...options,
        success: (res) => {
          if (res.confirm) {
            resolve(true);
          } else {
            resolve(false);
          }
        },
        fail: reject,
      });
    });
  },

  /**
   * ==================== 存储方法 ====================
   */

  /**
   * 设置本地存储
   */
  setStorage: function (key, data) {
    try {
      wx.setStorageSync(key, data);
      return true;
    } catch (e) {
      console.error("存储失败:", e);
      return false;
    }
  },

  /**
   * 获取本地存储
   */
  getStorage: function (key, defaultValue = null) {
    try {
      const value = wx.getStorageSync(key);
      return value || defaultValue;
    } catch (e) {
      console.error("读取存储失败:", e);
      return defaultValue;
    }
  },

  /**
   * 删除本地存储
   */
  removeStorage: function (key) {
    try {
      wx.removeStorageSync(key);
      return true;
    } catch (e) {
      console.error("删除存储失败:", e);
      return false;
    }
  },

  /**
   * 清空本地存储
   */
  clearStorage: function () {
    try {
      wx.clearStorageSync();
      return true;
    } catch (e) {
      console.error("清空存储失败:", e);
      return false;
    }
  },

  /**
   * ==================== 用户认证 ====================
   */

  /**
   * 检查登录状态
   */
  checkLogin: function () {
    const token = this.getStorage("token");
    const userInfo = this.getStorage("userInfo");

    if (token && userInfo) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
      return true;
    }
    return false;
  },

  /**
   * 保存用户信息
   */
  saveUserInfo: function (userInfo, token) {
    this.globalData.userInfo = userInfo;
    this.globalData.token = token;
    this.setStorage("userInfo", userInfo);
    this.setStorage("token", token);
  },

  /**
   * 清除用户信息(退出登录)
   */
  logout: function () {
    this.globalData.userInfo = null;
    this.globalData.token = null;
    this.removeStorage("userInfo");
    this.removeStorage("token");

    // 返回登录页
    wx.reLaunch({
      url: "/pages/login/login",
    });
  },

  /**
   * ==================== 数据格式化 ====================
   */

  /**
   * 格式化价格
   * @param {number} price - 价格
   * @param {number} decimals - 小数位数
   */
  formatPrice: function (price, decimals = 2) {
    if (isNaN(price)) return "0.00";
    return Number(price).toFixed(decimals);
  },

  /**
   * 格式化数字(千分位)
   */
  formatNumber: function (num) {
    if (isNaN(num)) return "0";
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  },

  /**
   * 格式化日期
   * @param {Date|string|number} date - 日期
   * @param {string} format - 格式: YYYY-MM-DD, YYYY-MM-DD HH:mm:ss
   */
  formatDate: function (date, format = "YYYY-MM-DD") {
    const d = new Date(date);
    if (isNaN(d.getTime())) return "";

    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    const hours = String(d.getHours()).padStart(2, "0");
    const minutes = String(d.getMinutes()).padStart(2, "0");
    const seconds = String(d.getSeconds()).padStart(2, "0");

    return format
      .replace("YYYY", year)
      .replace("MM", month)
      .replace("DD", day)
      .replace("HH", hours)
      .replace("mm", minutes)
      .replace("ss", seconds);
  },

  /**
   * 格式化相对时间
   */
  formatRelativeTime: function (date) {
    const now = new Date();
    const target = new Date(date);
    const diff = now - target;

    const minute = 60 * 1000;
    const hour = 60 * minute;
    const day = 24 * hour;

    if (diff < minute) {
      return "刚刚";
    } else if (diff < hour) {
      return Math.floor(diff / minute) + "分钟前";
    } else if (diff < day) {
      return Math.floor(diff / hour) + "小时前";
    } else if (diff < 7 * day) {
      return Math.floor(diff / day) + "天前";
    } else {
      return this.formatDate(date);
    }
  },

  /**
   * ==================== 图片处理 ====================
   */

  /**
   * 选择图片
   */
  chooseImage: function (options = {}) {
    const defaultOptions = {
      count: 1,
      sizeType: ["compressed"],
      sourceType: ["album", "camera"],
    };

    return new Promise((resolve, reject) => {
      wx.chooseImage({
        ...defaultOptions,
        ...options,
        success: (res) => resolve(res.tempFilePaths),
        fail: reject,
      });
    });
  },

  /**
   * 预览图片
   */
  previewImage: function (current, urls) {
    wx.previewImage({
      current: current,
      urls: urls || [current],
    });
  },

  /**
   * ==================== 网络请求 ====================
   */

  /**
   * HTTP请求封装
   */
  request: function (options = {}) {
    const { url, data = {}, method = "GET", header = {} } = options;

    return new Promise((resolve, reject) => {
      wx.request({
        url: this.globalData.apiBaseUrl + url,
        data: data,
        method: method,
        header: {
          "Content-Type": "application/json",
          Authorization: this.globalData.token
            ? `Bearer ${this.globalData.token}`
            : "",
          ...header,
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data);
          } else if (res.statusCode === 401) {
            // 未授权,跳转登录
            this.logout();
            reject(new Error("未授权,请重新登录"));
          } else {
            reject(new Error(res.data.message || "请求失败"));
          }
        },
        fail: (err) => {
          reject(err);
          this.showToast("网络请求失败", "error");
        },
      });
    });
  },

  /**
   * ==================== 其他工具方法 ====================
   */

  /**
   * 防抖函数
   */
  debounce: function (func, wait = 500) {
    let timeout;
    return function (...args) {
      clearTimeout(timeout);
      timeout = setTimeout(() => {
        func.apply(this, args);
      }, wait);
    };
  },

  /**
   * 节流函数
   */
  throttle: function (func, wait = 500) {
    let previous = 0;
    return function (...args) {
      const now = Date.now();
      if (now - previous > wait) {
        func.apply(this, args);
        previous = now;
      }
    };
  },

  /**
   * 深拷贝
   */
  deepClone: function (obj) {
    if (obj === null || typeof obj !== "object") return obj;

    if (obj instanceof Date) return new Date(obj);
    if (obj instanceof Array) return obj.map((item) => this.deepClone(item));

    const cloneObj = {};
    for (let key in obj) {
      if (obj.hasOwnProperty(key)) {
        cloneObj[key] = this.deepClone(obj[key]);
      }
    }
    return cloneObj;
  },

  /**
   * 检查小程序更新
   */
  checkUpdate: function () {
    if (wx.canIUse("getUpdateManager")) {
      const updateManager = wx.getUpdateManager();

      updateManager.onCheckForUpdate((res) => {
        if (res.hasUpdate) {
          updateManager.onUpdateReady(() => {
            wx.showModal({
              title: "更新提示",
              content: "新版本已经准备好，是否重启应用？",
              success: (res) => {
                if (res.confirm) {
                  updateManager.applyUpdate();
                }
              },
            });
          });

          updateManager.onUpdateFailed(() => {
            wx.showModal({
              title: "更新失败",
              content: "新版本下载失败，请删除小程序重新搜索打开",
              showCancel: false,
            });
          });
        }
      });
    }
  },

  /**
   * 获取系统信息
   */
  getSystemInfo: function () {
    try {
      return wx.getSystemInfoSync();
    } catch (e) {
      console.error("获取系统信息失败:", e);
      return {};
    }
  },
});
