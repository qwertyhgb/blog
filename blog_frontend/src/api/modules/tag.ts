import request from '@/utils/request'

export const tagApi = {
    getTags: () => {
        return request({
            url: '/tags',
            method: 'get'
        })
    },

    getTagById: (id: number) => {
        return request({
            url: `/tags/${id}`,
            method: 'get'
        })
    },

    getTagsByPostId: (postId: number) => {
        return request({
            url: `/tags/post/${postId}`,
            method: 'get'
        })
    },

    createTag: (data: any) => {
        return request({
            url: '/tags',
            method: 'post',
            data
        })
    },

    updateTag: (id: number, data: any) => {
        return request({
            url: `/tags/${id}`,
            method: 'put',
            data
        })
    },

    deleteTag: (id: number) => {
        return request({
            url: `/tags/${id}`,
            method: 'delete'
        })
    }
}
