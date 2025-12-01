import { defineStore } from 'pinia'
import { ref } from 'vue'
import { postApi } from '@/api'
import type { Post } from '@/types'

export const usePostStore = defineStore('post', () => {
  // 状态
  const posts = ref<Post[]>([])
  const currentPost = ref<Post | null>(null)
  const loading = ref(false)
  const total = ref(0)

  // 方法
  const fetchPosts = async (params?: any) => {
    try {
      loading.value = true
      const res = await postApi.getPosts(params)
      if (res.data && Array.isArray(res.data.records)) {
        posts.value = res.data.records
        total.value = res.data.total
      } else if (Array.isArray(res.data)) {
        posts.value = res.data
        total.value = res.data.length
      } else {
        posts.value = []
        total.value = 0
      }
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }

  const fetchPostById = async (id: number) => {
    try {
      loading.value = true
      const res = await postApi.getPostById(id)
      currentPost.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }

  const fetchPostsByCategory = async (categoryId: number) => {
    try {
      loading.value = true
      const res = await postApi.getPostsByCategory(categoryId)
      posts.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }

  const fetchPostsByTag = async (tagId: number) => {
    try {
      loading.value = true
      const res = await postApi.getPostsByTag(tagId)
      posts.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }

  const createPost = async (post: any) => {
    try {
      const res = await postApi.createPost(post)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  const updatePost = async (id: number, post: any) => {
    try {
      const res = await postApi.updatePost(id, post)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  const deletePost = async (id: number) => {
    try {
      const res = await postApi.deletePost(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  const likePost = async (id: number) => {
    try {
      const res = await postApi.likePost(id)
      if (currentPost.value && currentPost.value.id === id) {
        currentPost.value.likeCount++
      }
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

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