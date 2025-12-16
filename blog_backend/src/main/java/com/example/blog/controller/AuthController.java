package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.RegisterRequest;
import com.example.blog.dto.UserVO;
import com.example.blog.entity.User;
import com.example.blog.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * <p>
 * 对外提供用户登录、注册、刷新令牌、查询当前用户等接口，所有接口返回统一格式的
 * {@link ApiResponse}，方便前端统一处理。
 * </p>
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "用户登录、注册、令牌刷新等接口")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回 JWT 令牌")
    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        // AuthService 会校验凭证并生成访问令牌与刷新令牌
        Map<String, String> tokenMap = authService.login(loginRequest);
        return ApiResponse.success("登录成功", tokenMap);
    }

    @Operation(summary = "用户注册", description = "注册新用户账号")
    @PostMapping("/register")
    public ApiResponse<UserVO> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        // 注册成功返回 UserVO，避免直接暴露实体字段（如密码）
        User user = authService.register(registerRequest);
        return ApiResponse.success("注册成功", UserVO.fromUser(user));
    }

    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @PostMapping("/refresh")
    public ApiResponse<Map<String, String>> refreshToken(
            @RequestBody Map<String, String> request) {
        // 仅携带 refreshToken 即可刷新；异常会在 AuthService 中统一抛出
        String refreshToken = request.get("refreshToken");
        String token = authService.refreshToken(refreshToken);

        Map<String, String> tokenMap = Map.of("token", token);
        return ApiResponse.success("刷新令牌成功", tokenMap);
    }

    @Operation(summary = "获取当前用户", description = "获取当前登录用户的信息")
    @GetMapping("/me")
    public ApiResponse<UserVO> getCurrentUser() {
        // 若用户未登录，AuthService 会抛出 BusinessException 由全局异常处理
        User user = authService.getCurrentUser();
        return ApiResponse.success(
                "获取当前用户信息成功",
                UserVO.fromUser(user));
    }
}
