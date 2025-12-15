// Shared upload utilities for admin-web
// Provides: beforeUpload(file), uploadHeaders(getToken), parseUploadResponse(response), uploadAction()

export const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

export function beforeUpload(file) {
  const isImage = file.type && file.type.startsWith("image/");
  if (!isImage) {
    // element-plus app usually uses window.$message or similar; use console here and return false
    // calling code should show user-facing message
    console.warn("请上传图片文件");
    return false;
  }
  const isLtMax = file.size <= MAX_FILE_SIZE;
  if (!isLtMax) {
    console.warn("图片大小不能超过 5MB");
    return false;
  }
  return true;
}

export function uploadHeaders(getToken) {
  try {
    const token = typeof getToken === "function" ? getToken() : getToken;
    return { Authorization: token ? `Bearer ${token}` : "" };
  } catch (e) {
    return { Authorization: "" };
  }
}

// Parse various backend upload responses and return the URL string or null
export function parseUploadResponse(res) {
  if (!res) return null;

  // If server returned a string that contains JSON, try to parse it
  if (typeof res === "string") {
    try {
      const parsed = JSON.parse(res);
      return parseUploadResponse(parsed);
    } catch (e) {
      // not JSON, treat as URL string
      return res;
    }
  }

  // If response has nested data property
  if (res.data) {
    // data might be stringified JSON or plain string or object
    if (typeof res.data === "string") {
      // maybe it's a URL or JSON string
      try {
        const parsed = JSON.parse(res.data);
        return parseUploadResponse(parsed);
      } catch (e) {
        return res.data;
      }
    }
    if (res.data.url) return res.data.url;
  }

  // direct url field
  if (res.url) return res.url;

  // ApiResponse might be at top-level with data as map
  if (res.code && res.data && typeof res.data === "object" && res.data.url)
    return res.data.url;

  return null;
}

// Default upload action
export function uploadAction() {
  return "/api/files/upload";
}
