// 导入Pinia状态管理库
import { defineStore } from 'pinia'
// 导入Vue组合式API函数
import { ref } from 'vue'
// 导入评论API
import { commentApi } from '@/api'
// 导入评论类型定义
import type { Comment } from '@/types'

// 定义评论状态管理store
export const useCommentStore = defineStore('comment', () => {
  // 状态定义
  // 评论列表
  const comments = ref<Comment[]>([])
  // 加载状态
  const loading = ref(false)

  // 方法
  // 根据文章ID获取评论列表方法
  const fetchCommentsByPostId = async (postId: number) => {
    try {
      // 设置加载状态为true
      loading.value = true
      // 调用API获取文章的评论列表
      const res = await commentApi.getCommentsByPostId(postId)
      // 更新评论列表状态
      comments.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    } finally {
      // 设置加载状态为false
      loading.value = false
    }
  }

  // 创建评论方法
  const createComment = async (comment: any) => {
    try {
      // 调用API创建评论
      const res = await commentApi.createComment(comment)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 更新评论方法
  const updateComment = async (id: number, comment: any) => {
    try {
      // 调用API更新评论
      const res = await commentApi.updateComment(id, comment)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 删除评论方法
  const deleteComment = async (id: number) => {
    try {
      // 调用API删除评论
      const res = await commentApi.deleteComment(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 审核通过评论方法
  const approveComment = async (id: number) => {
    try {
      // 调用API审核通过评论
      const res = await commentApi.approveComment(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 审核拒绝评论方法
  const rejectComment = async (id: number) => {
    try {
      // 调用API审核拒绝评论
      const res = await commentApi.rejectComment(id)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 返回状态和方法
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