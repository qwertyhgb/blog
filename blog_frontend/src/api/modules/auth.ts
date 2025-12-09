// 导入请求工具

import request from "@/utils/request";

// 导入通用响应和用户类型

import type { ApiResponse, User } from "@/types";

// 定义登录/刷新返回的数据结构
export interface AuthTokenResponse {
  token: string;
  refreshToken: string;
}

// 定义认证相关的API接口

export const authApi = {
  // 用户登录接口

  login: (data: { username: string; password: string }) => {
    return request<AuthTokenResponse>({
      url: "/auth/login",

      method: "post",

      data,
    });
  },

  // 用户注册接口

  register: (data: {
    username: string;
    password: string;
    nickname: string;
    email: string;
  }) => {
    return request<null>({
      url: "/auth/register",

      method: "post",

      data,
    });
  },

  // 刷新令牌接口

  refreshToken: (refreshToken: string) => {
    return request<AuthTokenResponse>({
      url: "/auth/refresh",

      method: "post",

      data: { refreshToken },
    });
  },

  // 获取当前用户信息接口

  getCurrentUser: () => {
    return request<User>({
      url: "/auth/me",

      method: "get",
    });
  },
};
