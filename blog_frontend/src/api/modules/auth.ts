import request from '@/utils/request'

export const authApi = {
    login: (data: { username: string; password: string }) => {
        return request({
            url: '/auth/login',
            method: 'post',
            data
        })
    },

    register: (data: { username: string; password: string; nickname: string; email: string }) => {
        return request({
            url: '/auth/register',
            method: 'post',
            data
        })
    },

    refreshToken: (refreshToken: string) => {
        return request({
            url: '/auth/refresh',
            method: 'post',
            data: { refreshToken }
        })
    },

    getCurrentUser: () => {
        return request({
            url: '/auth/me',
            method: 'get'
        })
    }
}
