import { defineStore } from 'pinia'
import { ref } from 'vue'
import { categoryApi, tagApi } from '@/api'

export const useCategoryStore = defineStore('category', () => {
  // 状态
  const categories = ref<any[]>([])
  const currentCategory = ref<any>(null)
  const loading = ref(false)
  
  // 方法
  const fetchCategories = async () => {
    try {
      loading.value = true
      const res = await categoryApi.getCategories()
      categories.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  const fetchCategoryById = async (id: number) => {
    try {
      loading.value = true
      const res = await categoryApi.getCategoryById(id)
      currentCategory.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  const createCategory = async (category: any) => {
    try {
      const res = await categoryApi.createCategory(category)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
  const updateCategory = async (id: number, category: any) => {
    try {
      const res = await categoryApi.updateCategory(id, category)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
  const deleteCategory = async (id: number) => {
    try {
      const res = await categoryApi.deleteCategory(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
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

export const useTagStore = defineStore('tag', () => {
  // 状态
  const tags = ref<any[]>([])
  const currentTag = ref<any>(null)
  const loading = ref(false)
  
  // 方法
  const fetchTags = async () => {
    try {
      loading.value = true
      const res = await tagApi.getTags()
      tags.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  const fetchTagById = async (id: number) => {
    try {
      loading.value = true
      const res = await tagApi.getTagById(id)
      currentTag.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  const fetchTagsByPostId = async (postId: number) => {
    try {
      loading.value = true
      const res = await tagApi.getTagsByPostId(postId)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  const createTag = async (tag: any) => {
    try {
      const res = await tagApi.createTag(tag)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
  const updateTag = async (id: number, tag: any) => {
    try {
      const res = await tagApi.updateTag(id, tag)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
  const deleteTag = async (id: number) => {
    try {
      const res = await tagApi.deleteTag(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
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