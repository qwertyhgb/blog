// 导入请求工具
import request from "@/utils/request";
// 导入评论和分页类型
import type { Comment, PageResult } from "@/types";

// 定义评论表单类型
export interface CommentForm {
  postId: number;
  content: string;
  parentId?: number;
}

// 定义评论相关的API接口
export const commentApi = {
  // 管理后台：获取所有评论（分页）
  getAdminComments: (params?: { page?: number; size?: number; status?: number }) => {
    return request<PageResult<Comment>>({
      url: "/comments/admin",
      method: "get",
      params,
    });
  },

  // 根据文章ID获取评论列表接口
  getCommentsByPostId: (postId: number) => {
    return request<Comment[]>({
      url: `/comments/post/${postId}`,
      method: "get",
    });
  },

  // 根据ID获取评论详情接口
  getCommentById: (id: number) => {
    return request<Comment>({
      url: `/comments/${id}`,
      method: "get",
    });
  },

  // 创建评论接口
  createComment: (data: CommentForm) => {
    return request<Comment>({
      url: "/comments",
      method: "post",
      data,
    });
  },

  // 更新评论接口
  updateComment: (id: number, data: Partial<CommentForm>) => {
    return request<Comment>({
      url: `/comments/${id}`,
      method: "put",
      data,
    });
  },

  // 删除评论接口
  deleteComment: (id: number) => {
    return request<null>({
      url: `/comments/${id}`,
      method: "delete",
    });
  },

  // 审核通过评论接口
  approveComment: (id: number) => {
    return request<null>({
      url: `/comments/${id}/approve`,
      method: "post",
    });
  },

  // 审核拒绝评论接口
  rejectComment: (id: number) => {
    return request<null>({
      url: `/comments/${id}/reject`,
      method: "post",
    });
  },
};
