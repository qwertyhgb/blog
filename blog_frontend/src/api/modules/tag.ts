// 导入请求工具
import request from '@/utils/request'

// 定义标签相关的API接口
export const tagApi = {
    // 获取所有标签接口
    getTags: () => {
        return request({
            url: '/tags',
            method: 'get'
        })
    },

    // 根据ID获取标签详情接口
    getTagById: (id: number) => {
        return request({
            url: `/tags/${id}`,
            method: 'get'
        })
    },

    // 根据文章ID获取标签列表接口
    getTagsByPostId: (postId: number) => {
        return request({
            url: `/tags/post/${postId}`,
            method: 'get'
        })
    },

    // 创建标签接口
    createTag: (data: any) => {
        return request({
            url: '/tags',
            method: 'post',
            data
        })
    },

    // 更新标签接口
    updateTag: (id: number, data: any) => {
        return request({
            url: `/tags/${id}`,
            method: 'put',
            data
        })
    },

    // 删除标签接口
    deleteTag: (id: number) => {
        return request({
            url: `/tags/${id}`,
            method: 'delete'
        })
    }
}