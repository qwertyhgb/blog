package com.example.blog.dto;

import lombok.Data;

/**
 * 密码修改请求数据传输对象
 * 
 * 用于接收用户修改密码请求的数据
 */
@Data
public class PasswordChangeRequest {
    /** 原密码 */
    private String oldPassword;
    
    /** 新密码 */
    private String newPassword;
}