import request from '@/utils/request'

export const userApi = {
    getUsers: () => {
        return request({
            url: '/users',
            method: 'get'
        })
    },

    getUserById: (id: number) => {
        return request({
            url: `/users/${id}`,
            method: 'get'
        })
    },

    updateUser: (id: number, data: any) => {
        return request({
            url: `/users/${id}`,
            method: 'put',
            data
        })
    },

    deleteUser: (id: number) => {
        return request({
            url: `/users/${id}`,
            method: 'delete'
        })
    },

    changePassword: (id: number, data: { oldPassword: string; newPassword: string }) => {
        return request({
            url: `/users/${id}/change-password`,
            method: 'post',
            data
        })
    }
}
