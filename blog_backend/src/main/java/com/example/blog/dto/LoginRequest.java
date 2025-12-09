package com.example.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 登录请求数据传输对象
 * 
 * 用于接收用户登录请求的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    /** 用户名 */
    private String username;
    
    /** 密码 */
    private String password;
}