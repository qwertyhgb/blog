// 导入axios库和类型定义
import axios, {
  type InternalAxiosRequestConfig,
  type AxiosRequestConfig,
  type AxiosResponse,
} from "axios";
// 导入Naive UI的消息组件
import { createDiscreteApi } from "naive-ui";
// 导入用户状态管理store
import { useUserStore } from "@/stores/user";
// 导入通用响应类型
import type { ApiResponse } from "@/types";

// 创建Naive UI的消息组件实例
const { message } = createDiscreteApi(["message"]);

// 创建axios实例

const api = axios.create({
  // 设置API基础URL，优先使用环境变量中的值，否则使用默认值

  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",

  // 设置请求超时时间为10秒

  timeout: 10000,
}) as typeof axios;

// 是否正在刷新token的状态标志
let isRefreshing = false;
// 等待刷新token的请求队列
let refreshSubscribers: ((token: string) => void)[] = [];

// 将请求添加到队列的方法
const subscribeTokenRefresh = (callback: (token: string) => void) => {
  refreshSubscribers.push(callback);
};

// 刷新token后，执行队列中的请求
const onTokenRefreshed = (token: string) => {
  refreshSubscribers.forEach((callback) => callback(token));
  refreshSubscribers = [];
};

// 请求拦截器

(api as any).interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 获取用户状态管理store
    const userStore = useUserStore();
    // 如果有token，则添加到请求头
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`;
    }
    return config;
  },
  (error: any) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
(api as any).interceptors.response.use(
  (response: AxiosResponse<ApiResponse<any>>) => {
    // 获取响应数据

    const res = response.data;

    // 如果响应码不是200，则判断为错误

    if (res.code !== 200) {
      message.error(res.message || "请求失败");

      return Promise.reject(new Error(res.message || "请求失败"));
    } else {
      // 统一返回 ApiResponse<T>
      return res;
    }
  },

  async (error: any) => {
    // 获取原始请求配置
    const originalRequest = error.config;

    if (error.response) {
      // 401错误且不是刷新token的请求
      if (
        error.response.status === 401 &&
        !originalRequest._retry &&
        !originalRequest.url?.includes("/auth/refresh")
      ) {
        // 获取用户状态管理store
        const userStore = useUserStore();
        // 从本地存储获取刷新token
        const refreshToken = localStorage.getItem("refreshToken");

        // 如果有refreshToken，尝试刷新
        if (refreshToken) {
          if (!isRefreshing) {
            // 设置刷新状态为true
            isRefreshing = true;
            // 标记原始请求已重试
            originalRequest._retry = true;

            try {
              // 尝试刷新token
              await userStore.refreshToken();
              // 设置刷新状态为false
              isRefreshing = false;
              // 执行队列中的请求
              onTokenRefreshed(userStore.token || "");

              // 重试原请求
              originalRequest.headers.Authorization = `Bearer ${userStore.token}`;
              return api(originalRequest);
            } catch (refreshError) {
              // 刷新token失败
              isRefreshing = false;
              // 清空队列
              refreshSubscribers = [];
              // 退出登录
              userStore.logout();
              // 提示用户
              message.error("登录已过期，请重新登录");
              return Promise.reject(refreshError);
            }
          } else {
            // 正在刷新token，将请求加入队列
            return new Promise((resolve) => {
              subscribeTokenRefresh((token: string) => {
                originalRequest.headers.Authorization = `Bearer ${token}`;
                resolve(api(originalRequest));
              });
            });
          }
        } else {
          // 没有refreshToken，退出登录
          userStore.logout();
          message.error("未授权，请重新登录");
        }
      } else {
        // 处理其他HTTP错误状态码
        switch (error.response.status) {
          case 403:
            message.error("拒绝访问");
            break;
          case 404:
            message.error("请求的资源不存在");
            break;
          case 500:
            message.error("服务器内部错误");
            break;
          default:
            message.error(`请求失败: ${error.response.data?.message || "未知错误"}`);
        }
      }
    } else {
      // 网络错误
      message.error("网络错误，请检查网络连接");
    }

    return Promise.reject(error);
  }
);

// 基于axios实例封装带泛型的请求方法
function request<T = any>(config: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return api(config) as Promise<ApiResponse<T>>;
}

// 导出带类型的请求封装
export default request;
