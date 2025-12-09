// 导入Pinia状态管理库
import { defineStore } from 'pinia'
// 导入Vue组合式API函数
import { ref } from 'vue'
// 导入分类和标签API
import { categoryApi, tagApi } from '@/api'
// 导入分类和标签类型定义
import type { Category, Tag } from '@/types'

// 定义分类状态管理store
export const useCategoryStore = defineStore('category', () => {
  // 状态定义
  // 分类列表
  const categories = ref<Category[]>([])
  // 当前分类详情
  const currentCategory = ref<Category | null>(null)
  // 加载状态
  const loading = ref(false)

  // 方法
  // 获取分类列表方法
  const fetchCategories = async () => {
    try {
      // 设置加载状态为true
      loading.value = true
      // 调用API获取分类列表
      const res = await categoryApi.getCategories()
      // 更新分类列表状态
      categories.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      // 设置加载状态为false
      loading.value = false
    }
  }

  // 根据ID获取分类详情方法
  const fetchCategoryById = async (id: number) => {
    try {
      // 设置加载状态为true
      loading.value = true
      // 调用API获取分类详情
      const res = await categoryApi.getCategoryById(id)
      // 更新当前分类状态
      currentCategory.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      // 设置加载状态为false
      loading.value = false
    }
  }

  // 创建分类方法
  const createCategory = async (category: any) => {
    try {
      // 调用API创建分类
      const res = await categoryApi.createCategory(category)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 更新分类方法
  const updateCategory = async (id: number, category: any) => {
    try {
      // 调用API更新分类
      const res = await categoryApi.updateCategory(id, category)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 删除分类方法
  const deleteCategory = async (id: number) => {
    try {
      // 调用API删除分类
      const res = await categoryApi.deleteCategory(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 返回状态和方法
  return {
    categories,
    currentCategory,
    loading,
    fetchCategories,
    fetchCategoryById,
    createCategory,
    updateCategory,
    deleteCategory
  }
})

// 定义标签状态管理store
export const useTagStore = defineStore('tag', () => {
  // 状态定义
  // 标签列表
  const tags = ref<Tag[]>([])
  // 当前标签详情
  const currentTag = ref<Tag | null>(null)
  // 加载状态
  const loading = ref(false)

  // 方法
  // 获取标签列表方法
  const fetchTags = async () => {
    try {
      // 设置加载状态为true
      loading.value = true
      // 调用API获取标签列表
      const res = await tagApi.getTags()
      // 更新标签列表状态
      tags.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      // 设置加载状态为false
      loading.value = false
    }
  }

  // 根据ID获取标签详情方法
  const fetchTagById = async (id: number) => {
    try {
      // 设置加载状态为true
      loading.value = true
      // 调用API获取标签详情
      const res = await tagApi.getTagById(id)
      // 更新当前标签状态
      currentTag.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      // 设置加载状态为false
      loading.value = false
    }
  }

  // 根据文章ID获取标签列表方法
  const fetchTagsByPostId = async (postId: number) => {
    try {
      // 设置加载状态为true
      loading.value = true
      // 调用API获取文章的标签列表
      const res = await tagApi.getTagsByPostId(postId)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      // 设置加载状态为false
      loading.value = false
    }
  }

  // 创建标签方法
  const createTag = async (tag: any) => {
    try {
      // 调用API创建标签
      const res = await tagApi.createTag(tag)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 更新标签方法
  const updateTag = async (id: number, tag: any) => {
    try {
      // 调用API更新标签
      const res = await tagApi.updateTag(id, tag)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 删除标签方法
  const deleteTag = async (id: number) => {
    try {
      // 调用API删除标签
      const res = await tagApi.deleteTag(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 返回状态和方法
  return {
    tags,
    currentTag,
    loading,
    fetchTags,
    fetchTagById,
    fetchTagsByPostId,
    createTag,
    updateTag,
    deleteTag
  }
})