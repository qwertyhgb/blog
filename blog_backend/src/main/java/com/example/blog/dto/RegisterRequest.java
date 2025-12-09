package com.example.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 注册请求数据传输对象
 * 
 * 用于接收用户注册请求的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    /** 用户名 */
    private String username;
    
    /** 密码 */
    private String password;
    
    /** 昵称 */
    private String nickname;
    
    /** 电子邮箱 */
    private String email;
}