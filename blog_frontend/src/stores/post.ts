// 导入Pinia状态管理库
import { defineStore } from 'pinia'
// 导入Vue组合式API函数
import { ref } from 'vue'
// 导入文章API
import { postApi } from '@/api'
// 导入文章类型定义
import type { Post } from '@/types'

// 定义文章状态管理store
export const usePostStore = defineStore('post', () => {
  // 状态定义
  // 文章列表
  const posts = ref<Post[]>([])
  // 当前文章详情
  const currentPost = ref<Post | null>(null)
  // 加载状态
  const loading = ref(false)
  // 文章总数
  const total = ref(0)

  // 方法
  // 获取文章列表方法
  const fetchPosts = async (params?: any) => {
    try {
      // 设置加载状态为true
      loading.value = true
      // 调用API获取文章列表
      const res = await postApi.getPosts(params)
      // 处理不同的响应数据结构
      if (res.data && Array.isArray(res.data.records)) {
        // 分页数据结构
        posts.value = res.data.records
        total.value = res.data.total
      } else if (Array.isArray(res.data)) {
        // 普通数组结构
        posts.value = res.data
        total.value = res.data.length
      } else {
        // 空数据
        posts.value = []
        total.value = 0
      }
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      // 设置加载状态为false
      loading.value = false
    }
  }

  // 根据ID获取文章详情方法
  const fetchPostById = async (id: number) => {
    try {
      // 设置加载状态为true
      loading.value = true
      // 调用API获取文章详情
      const res = await postApi.getPostById(id)
      // 更新当前文章状态
      currentPost.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      // 设置加载状态为false
      loading.value = false
    }
  }

  // 根据分类ID获取文章列表方法
  const fetchPostsByCategory = async (categoryId: number) => {
    try {
      // 设置加载状态为true
      loading.value = true
      // 调用API获取分类下的文章列表
      const res = await postApi.getPostsByCategory(categoryId)
      // 更新文章列表状态
      posts.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      // 设置加载状态为false
      loading.value = false
    }
  }

  // 根据标签ID获取文章列表方法
  const fetchPostsByTag = async (tagId: number) => {
    try {
      // 设置加载状态为true
      loading.value = true
      // 调用API获取标签下的文章列表
      const res = await postApi.getPostsByTag(tagId)
      // 更新文章列表状态
      posts.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      // 设置加载状态为false
      loading.value = false
    }
  }

  // 创建文章方法
  const createPost = async (post: any) => {
    try {
      // 调用API创建文章
      const res = await postApi.createPost(post)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 更新文章方法
  const updatePost = async (id: number, post: any) => {
    try {
      // 调用API更新文章
      const res = await postApi.updatePost(id, post)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 删除文章方法
  const deletePost = async (id: number) => {
    try {
      // 调用API删除文章
      const res = await postApi.deletePost(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 点赞文章方法
  const likePost = async (id: number) => {
    try {
      // 调用API点赞文章
      const res = await postApi.likePost(id)
      // 如果当前文章是被点赞的文章，更新点赞数
      if (currentPost.value && currentPost.value.id === id) {
        currentPost.value.likeCount++
      }
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 返回状态和方法
  return {
    posts,
    currentPost,
    loading,
    total,
    fetchPosts,
    fetchPostById,
    fetchPostsByCategory,
    fetchPostsByTag,
    createPost,
    updatePost,
    deletePost,
    likePost
  }
})