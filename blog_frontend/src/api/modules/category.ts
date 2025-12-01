import request from '@/utils/request'

export const categoryApi = {
    getCategories: () => {
        return request({
            url: '/categories',
            method: 'get'
        })
    },

    getCategoryById: (id: number) => {
        return request({
            url: `/categories/${id}`,
            method: 'get'
        })
    },

    createCategory: (data: any) => {
        return request({
            url: '/categories',
            method: 'post',
            data
        })
    },

    updateCategory: (id: number, data: any) => {
        return request({
            url: `/categories/${id}`,
            method: 'put',
            data
        })
    },

    deleteCategory: (id: number) => {
        return request({
            url: `/categories/${id}`,
            method: 'delete'
        })
    }
}
