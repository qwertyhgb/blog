// 导入请求工具
import request from "@/utils/request";
// 导入分类类型
import type { Category } from "@/types";

// 定义分类表单类型
export interface CategoryForm {
  name: string;
  description?: string;
  sortOrder?: number;
}

// 定义分类相关的API接口
export const categoryApi = {
  // 获取所有分类接口
  getCategories: () => {
    return request<Category[]>({
      url: "/categories",
      method: "get",
    });
  },

  // 根据ID获取分类详情接口
  getCategoryById: (id: number) => {
    return request<Category>({
      url: `/categories/${id}`,
      method: "get",
    });
  },

  // 创建分类接口
  createCategory: (data: CategoryForm) => {
    return request<Category>({
      url: "/categories",
      method: "post",
      data,
    });
  },

  // 更新分类接口
  updateCategory: (id: number, data: CategoryForm) => {
    return request<Category>({
      url: `/categories/${id}`,
      method: "put",
      data,
    });
  },

  // 删除分类接口
  deleteCategory: (id: number) => {
    return request<null>({
      url: `/categories/${id}`,
      method: "delete",
    });
  },
};
