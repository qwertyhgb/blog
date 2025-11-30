import request from '@/utils/request'

// 认证相关API
export const authApi = {
  // 登录
  login: (data: { username: string; password: string }) => {
    return request({
      url: '/auth/login',
      method: 'post',
      data
    })
  },
  
  // 注册
  register: (data: { username: string; password: string; nickname: string; email: string }) => {
    return request({
      url: '/auth/register',
      method: 'post',
      data
    })
  },
  
  // 刷新令牌
  refreshToken: (refreshToken: string) => {
    return request({
      url: '/auth/refresh',
      method: 'post',
      data: { refreshToken }
    })
  },
  
  // 获取当前用户信息
  getCurrentUser: () => {
    return request({
      url: '/auth/me',
      method: 'get'
    })
  }
}

// 文章相关API
export const postApi = {
  // 获取文章列表
  getPosts: () => {
    return request({
      url: '/posts',
      method: 'get'
    })
  },
  
  // 获取文章详情
  getPostById: (id: number) => {
    return request({
      url: `/posts/${id}`,
      method: 'get'
    })
  },
  
  // 获取分类文章
  getPostsByCategory: (categoryId: number) => {
    return request({
      url: `/posts/category/${categoryId}`,
      method: 'get'
    })
  },
  
  // 获取标签文章
  getPostsByTag: (tagId: number) => {
    return request({
      url: `/posts/tag/${tagId}`,
      method: 'get'
    })
  },
  
  // 创建文章
  createPost: (data: any) => {
    return request({
      url: '/posts',
      method: 'post',
      data
    })
  },
  
  // 更新文章
  updatePost: (id: number, data: any) => {
    return request({
      url: `/posts/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除文章
  deletePost: (id: number) => {
    return request({
      url: `/posts/${id}`,
      method: 'delete'
    })
  },
  
  // 点赞文章
  likePost: (id: number) => {
    return request({
      url: `/posts/${id}/like`,
      method: 'post'
    })
  }
}

// 分类相关API
export const categoryApi = {
  // 获取分类列表
  getCategories: () => {
    return request({
      url: '/categories',
      method: 'get'
    })
  },
  
  // 获取分类详情
  getCategoryById: (id: number) => {
    return request({
      url: `/categories/${id}`,
      method: 'get'
    })
  },
  
  // 创建分类
  createCategory: (data: any) => {
    return request({
      url: '/categories',
      method: 'post',
      data
    })
  },
  
  // 更新分类
  updateCategory: (id: number, data: any) => {
    return request({
      url: `/categories/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除分类
  deleteCategory: (id: number) => {
    return request({
      url: `/categories/${id}`,
      method: 'delete'
    })
  }
}

// 标签相关API
export const tagApi = {
  // 获取标签列表
  getTags: () => {
    return request({
      url: '/tags',
      method: 'get'
    })
  },
  
  // 获取标签详情
  getTagById: (id: number) => {
    return request({
      url: `/tags/${id}`,
      method: 'get'
    })
  },
  
  // 获取文章标签
  getTagsByPostId: (postId: number) => {
    return request({
      url: `/tags/post/${postId}`,
      method: 'get'
    })
  },
  
  // 创建标签
  createTag: (data: any) => {
    return request({
      url: '/tags',
      method: 'post',
      data
    })
  },
  
  // 更新标签
  updateTag: (id: number, data: any) => {
    return request({
      url: `/tags/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除标签
  deleteTag: (id: number) => {
    return request({
      url: `/tags/${id}`,
      method: 'delete'
    })
  }
}

// 评论相关API
export const commentApi = {
  // 获取文章评论
  getCommentsByPostId: (postId: number) => {
    return request({
      url: `/comments/post/${postId}`,
      method: 'get'
    })
  },
  
  // 获取评论详情
  getCommentById: (id: number) => {
    return request({
      url: `/comments/${id}`,
      method: 'get'
    })
  },
  
  // 创建评论
  createComment: (data: any) => {
    return request({
      url: '/comments',
      method: 'post',
      data
    })
  },
  
  // 更新评论
  updateComment: (id: number, data: any) => {
    return request({
      url: `/comments/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除评论
  deleteComment: (id: number) => {
    return request({
      url: `/comments/${id}`,
      method: 'delete'
    })
  },
  
  // 审核通过评论
  approveComment: (id: number) => {
    return request({
      url: `/comments/${id}/approve`,
      method: 'post'
    })
  },
  
  // 驳回评论
  rejectComment: (id: number) => {
    return request({
      url: `/comments/${id}/reject`,
      method: 'post'
    })
  }
}

// 用户相关API
export const userApi = {
  // 获取用户列表
  getUsers: () => {
    return request({
      url: '/users',
      method: 'get'
    })
  },
  
  // 获取用户详情
  getUserById: (id: number) => {
    return request({
      url: `/users/${id}`,
      method: 'get'
    })
  },
  
  // 更新用户信息
  updateUser: (id: number, data: any) => {
    return request({
      url: `/users/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除用户
  deleteUser: (id: number) => {
    return request({
      url: `/users/${id}`,
      method: 'delete'
    })
  }
}