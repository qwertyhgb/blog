import { defineStore } from 'pinia'
import { ref } from 'vue'
import { commentApi } from '@/api'
import type { Comment } from '@/types'

export const useCommentStore = defineStore('comment', () => {
  // 状态
  const comments = ref<Comment[]>([])
  const loading = ref(false)

  // 方法
  const fetchCommentsByPostId = async (postId: number) => {
    try {
      loading.value = true
      const res = await commentApi.getCommentsByPostId(postId)
      comments.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }

  const createComment = async (comment: any) => {
    try {
      const res = await commentApi.createComment(comment)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  const updateComment = async (id: number, comment: any) => {
    try {
      const res = await commentApi.updateComment(id, comment)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  const deleteComment = async (id: number) => {
    try {
      const res = await commentApi.deleteComment(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  const approveComment = async (id: number) => {
    try {
      const res = await commentApi.approveComment(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  const rejectComment = async (id: number) => {
    try {
      const res = await commentApi.rejectComment(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  return {
    comments,
    loading,
    fetchCommentsByPostId,
    createComment,
    updateComment,
    deleteComment,
    approveComment,
    rejectComment
  }
})