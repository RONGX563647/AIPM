import axios from "axios";
import { ElMessage } from "element-plus";

// 创建axios实例
const api = axios.create({
  baseURL: "/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem("token");
    if (token) {
      // 添加token到请求头
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    // 直接返回响应数据
    return response.data;
  },
  (error) => {
    // 处理错误响应
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem("token");
          ElMessage.error("登录已过期，请重新登录");
          // 跳转到登录页
          if (window.location.pathname !== "/login") {
            window.location.href = "/login";
          }
          break;
        case 403:
          ElMessage.error("权限不足，无法访问该资源");
          break;
        case 404:
          ElMessage.error("请求的资源不存在");
          break;
        case 500:
          ElMessage.error("服务器内部错误");
          break;
        default:
          ElMessage.error(
            `请求失败：${error.response.data?.msg || "未知错误"}`,
          );
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      ElMessage.error("网络错误，请检查网络连接");
    } else {
      // 请求配置出错
      ElMessage.error(`请求配置错误：${error.message}`);
    }
    return Promise.reject(error);
  },
);

// 登录请求接口
export interface LoginRequest {
  username: string;
  password: string;
}

// 登录响应接口
export interface LoginResponse {
  code: number;
  msg: string;
  data: string; // token
}

// 注册请求接口
export interface RegisterRequest {
  username: string;
  password: string;
  confirmPassword: string;
  nickname: string;
}

// 注册响应接口
export interface RegisterResponse {
  code: number;
  msg: string;
  data: string; // token
}

// 密码重置请求接口
export interface ResetPasswordRequest {
  username: string;
  newPassword: string;
  confirmPassword: string;
}

// 密码重置响应接口
export interface ResetPasswordResponse {
  code: number;
  msg: string;
  data: boolean;
}

// 认证API封装
export const authApi = {
  /**
   * 用户登录
   * @param data 登录参数
   * @returns 登录响应
   */
  login: (data: LoginRequest): Promise<LoginResponse> => {
    return api.post<LoginResponse>("/sys/user/login", data);
  },

  /**
   * 用户注册
   * @param data 注册参数
   * @returns 注册响应
   */
  register: (data: RegisterRequest): Promise<RegisterResponse> => {
    return api.post<RegisterResponse>("/sys/user/register", {
      username: data.username,
      password: data.password,
      nickname: data.nickname,
    });
  },

  /**
   * 忘记密码
   * @param username 用户名
   * @returns 忘记密码响应
   */
  forgotPassword: (
    username: string,
  ): Promise<{ code: number; msg: string; data: string }> => {
    return api.post("/sys/user/forgot", { username });
  },

  /**
   * 重置密码
   * @param token 重置令牌
   * @param newPassword 新密码
   * @returns 重置密码响应
   */
  resetPassword: (
    token: string,
    newPassword: string,
  ): Promise<{ code: number; msg: string; data: boolean }> => {
    return api.post("/sys/user/reset", { token, newPassword });
  },

  /**
   * 用户登出
   * @returns 登出响应
   */
  logout: (): Promise<any> => {
    return api.post("/sys/user/logout");
  },

  /**
   * 获取当前用户信息
   * @returns 用户信息
   */
  getCurrentUser: (): Promise<any> => {
    return api.get("/sys/user/current");
  },

  getGithubAuthorizeUrl: (): Promise<{
    code: number;
    msg: string;
    data: string;
  }> => {
    return api.get("/sys/user/oauth/github/authorize");
  },
};

// 导出单独的方法，方便组件直接使用
export const login = authApi.login;
export const register = authApi.register;
export const forgotPassword = authApi.forgotPassword;
export const resetPassword = authApi.resetPassword;
export const logout = authApi.logout;
export const getCurrentUser = authApi.getCurrentUser;

// 导出axios实例供其他API使用
export default api;
