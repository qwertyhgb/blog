// 导入请求工具
import request from "@/utils/request";

// 定义分类相关的API接口
export const categoryApi = {
  // 获取所有分类接口
  getCategories: () => {
    return request({
      url: "/categories",
      method: "get",
    });
  },

  // 根据ID获取分类详情接口
  getCategoryById: (id: number) => {
    return request({
      url: `/categories/${id}`,
      method: "get",
    });
  },

  // 创建分类接口
  createCategory: (data: any) => {
    return request({
      url: "/categories",
      method: "post",
      data,
    });
  },

  // 更新分类接口
  updateCategory: (id: number, data: any) => {
    return request({
      url: `/categories/${id}`,
      method: "put",
      data,
    });
  },

  // 删除分类接口
  deleteCategory: (id: number) => {
    return request({
      url: `/categories/${id}`,
      method: "delete",
    });
  },
};
