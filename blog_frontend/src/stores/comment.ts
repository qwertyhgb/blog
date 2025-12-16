// 导入Pinia状态管理库
import { defineStore } from "pinia";
// 导入Vue组合式API函数
import { ref } from "vue";
// 导入评论API
import { commentApi, type CommentForm } from "@/api";
// 导入评论类型定义
import type { Comment, PageResult } from "@/types";

// ========== 评论 Store ==========
export const useCommentStore = defineStore("comment", () => {
  // ========== 状态定义 ==========
  const comments = ref<Comment[]>([]);
  const loading = ref(false);
  const total = ref(0);
  const currentPage = ref(1);
  const pageSize = ref(20);

  // ========== 通用方法 ==========
  /**
   * 通用的 loading 封装方法
   */
  const withLoading = async <T>(fn: () => Promise<T>): Promise<T> => {
    try {
      loading.value = true;
      return await fn();
    } finally {
      loading.value = false;
    }
  };

  // ========== 评论相关方法 ==========
  /**
   * 获取管理后台评论列表（分页）
   */
  const fetchAdminComments = async (params?: { page?: number; size?: number; status?: number }) => {
    return withLoading(async () => {
      const res = await commentApi.getAdminComments(params);
      comments.value = res.data.records;
      total.value = res.data.total;
      currentPage.value = res.data.current;
      pageSize.value = res.data.size;
      return res;
    });
  };

  /**
   * 根据文章ID获取评论列表方法
   */
  const fetchCommentsByPostId = async (postId: number) => {
    return withLoading(async () => {
      const res = await commentApi.getCommentsByPostId(postId);
      comments.value = res.data;
      return res;
    });
  };

  /**
   * 创建评论方法
   */
  const createComment = async (comment: CommentForm) => {
    const res = await commentApi.createComment(comment);
    // 创建成功后，添加到评论列表
    if (res.data) {
      comments.value.push(res.data);
    }
    return res;
  };

  /**
   * 更新评论方法
   */
  const updateComment = async (id: number, comment: Partial<CommentForm>) => {
    const res = await commentApi.updateComment(id, comment);
    // 更新列表中的对应评论
    const index = comments.value.findIndex((c) => c.id === id);
    if (index !== -1 && res.data) {
      comments.value[index] = res.data;
    }
    return res;
  };

  /**
   * 删除评论方法
   */
  const deleteComment = async (id: number) => {
    const res = await commentApi.deleteComment(id);
    // 从列表中移除已删除的评论
    comments.value = comments.value.filter((comment) => comment.id !== id);
    total.value = Math.max(0, total.value - 1);
    return res;
  };

  /**
   * 审核通过评论方法
   */
  const approveComment = async (id: number) => {
    const res = await commentApi.approveComment(id);
    // 更新评论状态为已通过（status: 1）
    const comment = comments.value.find((c) => c.id === id);
    if (comment) {
      comment.status = 1;
    }
    return res;
  };

  /**
   * 审核拒绝评论方法
   */
  const rejectComment = async (id: number) => {
    const res = await commentApi.rejectComment(id);
    // 更新评论状态为已拒绝（status: 2）
    const comment = comments.value.find((c) => c.id === id);
    if (comment) {
      comment.status = 2;
    }
    return res;
  };

  // ========== 辅助方法 ==========
  /**
   * 清空评论列表
   */
  const clearComments = () => {
    comments.value = [];
    total.value = 0;
  };

  /**
   * 重置所有状态
   */
  const resetStore = () => {
    comments.value = [];
    loading.value = false;
    total.value = 0;
    currentPage.value = 1;
    pageSize.value = 20;
  };

  /**
   * 根据父评论ID过滤子评论
   */
  const getChildComments = (parentId: number) => {
    return comments.value.filter((comment) => comment.parentId === parentId);
  };

  /**
   * 获取顶级评论（没有父评论的评论）
   */
  const getTopLevelComments = () => {
    return comments.value.filter((comment) => !comment.parentId);
  };

  // ========== 返回状态和方法 ==========
  return {
    // 状态
    comments,
    loading,
    total,
    currentPage,
    pageSize,

    // 查询方法
    fetchAdminComments,
    fetchCommentsByPostId,

    // 操作方法
    createComment,
    updateComment,
    deleteComment,
    approveComment,
    rejectComment,

    // 辅助方法
    clearComments,
    resetStore,
    getChildComments,
    getTopLevelComments,
  };
});
