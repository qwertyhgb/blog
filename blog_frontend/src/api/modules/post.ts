import request from '@/utils/request'

export const postApi = {
    getPosts: (params?: { page?: number; size?: number; keyword?: string }) => {
        return request({
            url: '/posts',
            method: 'get',
            params
        })
    },

    // 管理后台：获取所有文章（包括草稿）
    getAdminPosts: (params?: { page?: number; size?: number; keyword?: string }) => {
        return request({
            url: '/posts/admin',
            method: 'get',
            params
        })
    },

    getPostById: (id: number) => {
        return request({
            url: `/posts/${id}`,
            method: 'get'
        })
    },

    getPostsByCategory: (categoryId: number) => {
        return request({
            url: `/posts/category/${categoryId}`,
            method: 'get'
        })
    },

    getPostsByTag: (tagId: number) => {
        return request({
            url: `/posts/tag/${tagId}`,
            method: 'get'
        })
    },

    createPost: (data: any) => {
        return request({
            url: '/posts',
            method: 'post',
            data
        })
    },

    updatePost: (id: number, data: any) => {
        return request({
            url: `/posts/${id}`,
            method: 'put',
            data
        })
    },

    deletePost: (id: number) => {
        return request({
            url: `/posts/${id}`,
            method: 'delete'
        })
    },

    likePost: (id: number) => {
        return request({
            url: `/posts/${id}/like`,
            method: 'post'
        })
    }
}
