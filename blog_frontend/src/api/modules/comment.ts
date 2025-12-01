import request from '@/utils/request'

export const commentApi = {
    // 管理后台：获取所有评论（分页）
    getAdminComments: (params?: { page?: number; size?: number; status?: number }) => {
        return request({
            url: '/comments/admin',
            method: 'get',
            params
        })
    },

    getCommentsByPostId: (postId: number) => {
        return request({
            url: `/comments/post/${postId}`,
            method: 'get'
        })
    },

    getCommentById: (id: number) => {
        return request({
            url: `/comments/${id}`,
            method: 'get'
        })
    },

    createComment: (data: any) => {
        return request({
            url: '/comments',
            method: 'post',
            data
        })
    },

    updateComment: (id: number, data: any) => {
        return request({
            url: `/comments/${id}`,
            method: 'put',
            data
        })
    },

    deleteComment: (id: number) => {
        return request({
            url: `/comments/${id}`,
            method: 'delete'
        })
    },

    approveComment: (id: number) => {
        return request({
            url: `/comments/${id}/approve`,
            method: 'post'
        })
    },

    rejectComment: (id: number) => {
        return request({
            url: `/comments/${id}/reject`,
            method: 'post'
        })
    }
}
