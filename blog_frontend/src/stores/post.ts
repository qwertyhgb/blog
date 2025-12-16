// 导入Pinia状态管理库
import { defineStore } from "pinia";
// 导入Vue组合式API函数
import { ref } from "vue";
// 导入文章API
import { postApi, type PostForm } from "@/api";
// 导入文章类型定义
import type { Post, PageResult } from "@/types";

// 定义文章状态管理store
export const usePostStore = defineStore("post", () => {
  // ========== 状态定义 ==========
  // 文章列表
  const posts = ref<Post[]>([]);
  // 当前文章详情
  const currentPost = ref<Post | null>(null);
  // 加载状态
  const loading = ref(false);
  // 文章总数
  const total = ref(0);
  // 当前页码
  const currentPage = ref(1);
  // 每页大小
  const pageSize = ref(10);

  // ========== 通用方法 ==========
  /**
   * 通用的 loading 封装方法
   * 自动处理 loading 状态的设置和清除
   */
  const withLoading = async <T>(fn: () => Promise<T>): Promise<T> => {
    try {
      loading.value = true;
      return await fn();
    } finally {
      loading.value = false;
    }
  };

  // ========== 文章列表相关方法 ==========
  /**
   * 获取文章列表方法（公开列表）
   */
  const fetchPosts = async (params?: { page?: number; size?: number; keyword?: string }) => {
    return withLoading(async () => {
      const res = await postApi.getPosts(params);
      posts.value = res.data.records;
      total.value = res.data.total;
      currentPage.value = res.data.current;
      pageSize.value = res.data.size;
      return res;
    });
  };

  /**
   * 获取管理后台文章列表（包含草稿）
   */
  const fetchAdminPosts = async (params?: { page?: number; size?: number; keyword?: string }) => {
    return withLoading(async () => {
      const res = await postApi.getAdminPosts(params);
      posts.value = res.data.records;
      total.value = res.data.total;
      currentPage.value = res.data.current;
      pageSize.value = res.data.size;
      return res;
    });
  };

  /**
   * 根据ID获取文章详情方法
   */
  const fetchPostById = async (id: number) => {
    return withLoading(async () => {
      const res = await postApi.getPostById(id);
      currentPost.value = res.data;
      return res;
    });
  };

  /**
   * 根据分类ID获取文章列表方法
   */
  const fetchPostsByCategory = async (categoryId: number) => {
    return withLoading(async () => {
      const res = await postApi.getPostsByCategory(categoryId);
      posts.value = res.data;
      total.value = res.data.length;
      return res;
    });
  };

  /**
   * 根据标签ID获取文章列表方法
   */
  const fetchPostsByTag = async (tagId: number) => {
    return withLoading(async () => {
      const res = await postApi.getPostsByTag(tagId);
      posts.value = res.data;
      total.value = res.data.length;
      return res;
    });
  };

  // ========== 文章操作方法 ==========
  /**
   * 创建文章方法
   */
  const createPost = async (post: PostForm) => {
    const res = await postApi.createPost(post);
    // 创建成功后可选择刷新列表
    return res;
  };

  /**
   * 更新文章方法
   */
  const updatePost = async (id: number, post: PostForm) => {
    const res = await postApi.updatePost(id, post);
    // 如果当前文章是被更新的文章，同步更新状态
    if (currentPost.value && currentPost.value.id === id) {
      currentPost.value = res.data;
    }
    return res;
  };

  /**
   * 删除文章方法
   */
  const deletePost = async (id: number) => {
    const res = await postApi.deletePost(id);
    // 从列表中移除已删除的文章
    posts.value = posts.value.filter((post) => post.id !== id);
    total.value = Math.max(0, total.value - 1);
    // 如果当前详情是被删除的文章，清空
    if (currentPost.value && currentPost.value.id === id) {
      currentPost.value = null;
    }
    return res;
  };

  /**
   * 点赞文章方法
   */
  const likePost = async (id: number) => {
    await postApi.likePost(id);
    // 更新当前文章详情的点赞数
    if (currentPost.value && currentPost.value.id === id) {
      currentPost.value.likeCount++;
    }
    // 更新列表中对应文章的点赞数
    const postInList = posts.value.find((post) => post.id === id);
    if (postInList) {
      postInList.likeCount++;
    }
  };

  // ========== 辅助方法 ==========
  /**
   * 清空当前文章详情
   */
  const clearCurrentPost = () => {
    currentPost.value = null;
  };

  /**
   * 清空文章列表
   */
  const clearPosts = () => {
    posts.value = [];
    total.value = 0;
    currentPage.value = 1;
  };

  /**
   * 重置所有状态
   */
  const resetStore = () => {
    posts.value = [];
    currentPost.value = null;
    loading.value = false;
    total.value = 0;
    currentPage.value = 1;
    pageSize.value = 10;
  };

  // ========== 返回状态和方法 ==========
  return {
    // 状态
    posts,
    currentPost,
    loading,
    total,
    currentPage,
    pageSize,

    // 查询方法
    fetchPosts,
    fetchAdminPosts,
    fetchPostById,
    fetchPostsByCategory,
    fetchPostsByTag,

    // 操作方法
    createPost,
    updatePost,
    deletePost,
    likePost,

    // 辅助方法
    clearCurrentPost,
    clearPosts,
    resetStore,
  };
});
