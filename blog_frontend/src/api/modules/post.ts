// 导入请求工具

import request from "@/utils/request";

// 导入文章和分页类型

import type { ApiResponse, PageResult, Post } from "@/types";

// 定义文章创建/更新的表单数据类型（根据后端实际字段可再细化）
export interface PostForm {
  title: string;
  content: string;
  summary?: string;
  coverImage?: string;
  status: number;
  categoryId: number;
  tagIds?: number[];
}

// 定义文章相关的API接口

export const postApi = {
  // 获取文章列表接口（公开列表，通常为分页列表）

  getPosts: (params?: { page?: number; size?: number; keyword?: string }) => {
    return request<PageResult<Post>>({
      url: "/posts",

      method: "get",

      params,
    });
  },

  // 管理后台：获取所有文章（包括草稿）

  getAdminPosts: (params?: { page?: number; size?: number; keyword?: string }) => {
    return request<PageResult<Post>>({
      url: "/posts/admin",

      method: "get",

      params,
    });
  },

  // 根据ID获取文章详情接口

  getPostById: (id: number) => {
    return request<Post>({
      url: `/posts/${id}`,

      method: "get",
    });
  },

  // 根据分类ID获取文章列表接口

  getPostsByCategory: (categoryId: number) => {
    return request<Post[]>({
      url: `/posts/category/${categoryId}`,

      method: "get",
    });
  },

  // 根据标签ID获取文章列表接口

  getPostsByTag: (tagId: number) => {
    return request<Post[]>({
      url: `/posts/tag/${tagId}`,

      method: "get",
    });
  },

  // 创建文章接口

  createPost: (data: PostForm) => {
    return request<Post>({
      url: "/posts",

      method: "post",

      data,
    });
  },

  // 更新文章接口

  updatePost: (id: number, data: PostForm) => {
    return request<Post>({
      url: `/posts/${id}`,

      method: "put",

      data,
    });
  },

  // 删除文章接口

  deletePost: (id: number) => {
    return request<null>({
      url: `/posts/${id}`,

      method: "delete",
    });
  },

  // 点赞文章接口

  likePost: (id: number) => {
    return request<null>({
      url: `/posts/${id}/like`,

      method: "post",
    });
  },
};
