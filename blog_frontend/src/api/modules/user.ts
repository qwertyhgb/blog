// 导入请求工具
import request from '@/utils/request'

// 定义用户相关的API接口
export const userApi = {
    // 获取所有用户接口
    getUsers: () => {
        return request({
            url: '/users',
            method: 'get'
        })
    },

    // 根据ID获取用户详情接口
    getUserById: (id: number) => {
        return request({
            url: `/users/${id}`,
            method: 'get'
        })
    },

    // 更新用户信息接口
    updateUser: (id: number, data: any) => {
        return request({
            url: `/users/${id}`,
            method: 'put',
            data
        })
    },

    // 删除用户接口
    deleteUser: (id: number) => {
        return request({
            url: `/users/${id}`,
            method: 'delete'
        })
    },

    // 修改用户密码接口
    changePassword: (id: number, data: { oldPassword: string; newPassword: string }) => {
        return request({
            url: `/users/${id}/change-password`,
            method: 'post',
            data
        })
    }
}