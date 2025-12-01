package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.RegisterRequest;
import com.example.blog.dto.UserVO;
import com.example.blog.entity.User;
import com.example.blog.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, String> tokenMap = authService.login(loginRequest);
        return ApiResponse.success("登录成功", tokenMap);
    }
    
    @PostMapping("/register")
    public ApiResponse<UserVO> register(@RequestBody RegisterRequest registerRequest) {
        User user = authService.register(registerRequest);
        return ApiResponse.success("注册成功", UserVO.fromUser(user));
    }
    
    @PostMapping("/refresh")
    public ApiResponse<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String token = authService.refreshToken(refreshToken);
        
        Map<String, String> tokenMap = Map.of("token", token);
        return ApiResponse.success("刷新令牌成功", tokenMap);
    }
    
    @GetMapping("/me")
    public ApiResponse<UserVO> getCurrentUser() {
        User user = authService.getCurrentUser();
        return ApiResponse.success("获取当前用户信息成功", UserVO.fromUser(user));
    }
}