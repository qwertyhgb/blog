// 导入Pinia状态管理库
import { defineStore } from "pinia";
// 导入Vue组合式API函数
import { ref } from "vue";
// 导入分类和标签API
import { categoryApi, tagApi, type CategoryForm, type TagForm } from "@/api";
// 导入分类和标签类型定义
import type { Category, Tag } from "@/types";

// ========== 分类 Store ==========
export const useCategoryStore = defineStore("category", () => {
  // ========== 状态定义 ==========
  const categories = ref<Category[]>([]);
  const currentCategory = ref<Category | null>(null);
  const loading = ref(false);

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

  // ========== 分类相关方法 ==========
  /**
   * 获取分类列表方法
   */
  const fetchCategories = async () => {
    return withLoading(async () => {
      const res = await categoryApi.getCategories();
      categories.value = res.data;
      return res;
    });
  };

  /**
   * 根据ID获取分类详情方法
   */
  const fetchCategoryById = async (id: number) => {
    return withLoading(async () => {
      const res = await categoryApi.getCategoryById(id);
      currentCategory.value = res.data;
      return res;
    });
  };

  /**
   * 创建分类方法
   */
  const createCategory = async (category: CategoryForm) => {
    const res = await categoryApi.createCategory(category);
    // 创建成功后刷新列表
    await fetchCategories();
    return res;
  };

  /**
   * 更新分类方法
   */
  const updateCategory = async (id: number, category: CategoryForm) => {
    const res = await categoryApi.updateCategory(id, category);
    // 更新当前分类
    if (currentCategory.value && currentCategory.value.id === id) {
      currentCategory.value = res.data;
    }
    // 刷新列表
    await fetchCategories();
    return res;
  };

  /**
   * 删除分类方法
   */
  const deleteCategory = async (id: number) => {
    const res = await categoryApi.deleteCategory(id);
    // 从列表中移除
    categories.value = categories.value.filter((cat) => cat.id !== id);
    // 清空当前分类
    if (currentCategory.value && currentCategory.value.id === id) {
      currentCategory.value = null;
    }
    return res;
  };

  /**
   * 清空当前分类
   */
  const clearCurrentCategory = () => {
    currentCategory.value = null;
  };

  /**
   * 重置状态
   */
  const resetStore = () => {
    categories.value = [];
    currentCategory.value = null;
    loading.value = false;
  };

  // ========== 返回状态和方法 ==========
  return {
    // 状态
    categories,
    currentCategory,
    loading,

    // 方法
    fetchCategories,
    fetchCategoryById,
    createCategory,
    updateCategory,
    deleteCategory,
    clearCurrentCategory,
    resetStore,
  };
});

// ========== 标签 Store ==========
export const useTagStore = defineStore("tag", () => {
  // ========== 状态定义 ==========
  const tags = ref<Tag[]>([]);
  const currentTag = ref<Tag | null>(null);
  const loading = ref(false);

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

  // ========== 标签相关方法 ==========
  /**
   * 获取标签列表方法
   */
  const fetchTags = async () => {
    return withLoading(async () => {
      const res = await tagApi.getTags();
      tags.value = res.data;
      return res;
    });
  };

  /**
   * 根据ID获取标签详情方法
   */
  const fetchTagById = async (id: number) => {
    return withLoading(async () => {
      const res = await tagApi.getTagById(id);
      currentTag.value = res.data;
      return res;
    });
  };

  /**
   * 根据文章ID获取标签列表方法
   */
  const fetchTagsByPostId = async (postId: number) => {
    return withLoading(async () => {
      const res = await tagApi.getTagsByPostId(postId);
      return res;
    });
  };

  /**
   * 创建标签方法
   */
  const createTag = async (tag: TagForm) => {
    const res = await tagApi.createTag(tag);
    // 创建成功后刷新列表
    await fetchTags();
    return res;
  };

  /**
   * 更新标签方法
   */
  const updateTag = async (id: number, tag: TagForm) => {
    const res = await tagApi.updateTag(id, tag);
    // 更新当前标签
    if (currentTag.value && currentTag.value.id === id) {
      currentTag.value = res.data;
    }
    // 刷新列表
    await fetchTags();
    return res;
  };

  /**
   * 删除标签方法
   */
  const deleteTag = async (id: number) => {
    const res = await tagApi.deleteTag(id);
    // 从列表中移除
    tags.value = tags.value.filter((tag) => tag.id !== id);
    // 清空当前标签
    if (currentTag.value && currentTag.value.id === id) {
      currentTag.value = null;
    }
    return res;
  };

  /**
   * 清空当前标签
   */
  const clearCurrentTag = () => {
    currentTag.value = null;
  };

  /**
   * 重置状态
   */
  const resetStore = () => {
    tags.value = [];
    currentTag.value = null;
    loading.value = false;
  };

  // ========== 返回状态和方法 ==========
  return {
    // 状态
    tags,
    currentTag,
    loading,

    // 方法
    fetchTags,
    fetchTagById,
    fetchTagsByPostId,
    createTag,
    updateTag,
    deleteTag,
    clearCurrentTag,
    resetStore,
  };
});
