package com.example.blog.service;

import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.RegisterRequest;
import com.example.blog.entity.User;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface IAuthService {

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 包含 token 和 refreshToken 的 Map
     */
    Map<String, String> login(LoginRequest loginRequest);

    /**
     * 用户注册
     * 
     * @param registerRequest 注册请求
     * @return 注册成功的用户
     */
    User register(RegisterRequest registerRequest);

    /**
     * 刷新访问令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    String refreshToken(String refreshToken);

    /**
     * 获取当前登录用户
     * 
     * @return 当前用户
     */
    User getCurrentUser();
}
