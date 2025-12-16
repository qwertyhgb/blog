// 导入请求工具
import request from "@/utils/request";
// 导入用户类型
import type { User } from "@/types";

// 定义用户更新表单类型
export interface UserUpdateForm {
  nickname?: string;
  email?: string;
  avatar?: string;
}

// 定义修改密码表单类型
export interface ChangePasswordForm {
  oldPassword: string;
  newPassword: string;
}

// 定义用户相关的API接口
export const userApi = {
  // 获取所有用户接口
  getUsers: () => {
    return request<User[]>({
      url: "/users",
      method: "get",
    });
  },

  // 根据ID获取用户详情接口
  getUserById: (id: number) => {
    return request<User>({
      url: `/users/${id}`,
      method: "get",
    });
  },

  // 更新用户信息接口
  updateUser: (id: number, data: UserUpdateForm) => {
    return request<User>({
      url: `/users/${id}`,
      method: "put",
      data,
    });
  },

  // 删除用户接口
  deleteUser: (id: number) => {
    return request<null>({
      url: `/users/${id}`,
      method: "delete",
    });
  },

  // 修改用户密码接口
  changePassword: (id: number, data: ChangePasswordForm) => {
    return request<null>({
      url: `/users/${id}/change-password`,
      method: "post",
      data,
    });
  },
};
