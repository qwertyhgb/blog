import { defineStore } from 'pinia'
import { ref } from 'vue'
import { postApi } from '@/api'

export const usePostStore = defineStore('post', () => {
  // 状态
  const posts = ref<any[]>([])
  const currentPost = ref<any>(null)
  const loading = ref(false)
  
  // 方法
  const fetchPosts = async () => {
    try {
      loading.value = true
      const res = await postApi.getPosts()
      posts.value = res.data
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