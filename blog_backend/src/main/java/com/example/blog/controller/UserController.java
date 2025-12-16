package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.PasswordChangeRequest;
import com.example.blog.dto.UserVO;
import com.example.blog.entity.User;
import com.example.blog.service.IAuthService;
import com.example.blog.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户控制器
 *
 * <p>
 * 包含用户信息查询、更新、删除、密码修改等接口，同时内置权限校验逻辑（管理员/用户本人）。
 * </p>
 */
@RestController
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户信息的增删改查接口")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuthService authService;

    @Operation(summary = "获取所有用户", description = "获取所有用户列表（仅管理员）")
    @GetMapping
    public ApiResponse<List<UserVO>> getAllUsers() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限查看用户列表");
        }

        // 使用 VO 对象返回数据，避免暴露密码等敏感字段
        List<User> users = userService.findAll();
        List<UserVO> userVOs = users.stream()
                .map(UserVO::fromUser)
                .collect(Collectors.toList());
        return ApiResponse.success("获取用户列表成功", userVOs);
    }

    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情")
    @GetMapping("/{id}")
    public ApiResponse<UserVO> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }

        // 只有管理员或用户本人可以查看详细信息
        if (!currentUser.getId().equals(id) && !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限查看该用户信息");
        }

        // 再次查询数据库，确保信息最新
        User user = userService.findById(id);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }

        return ApiResponse.success("获取用户详情成功", UserVO.fromUser(user));
    }

    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    @PutMapping("/{id}")
    public ApiResponse<UserVO> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody User user) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }

        // 只有管理员或用户本人可以修改信息
        if (!currentUser.getId().equals(id) && !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限修改该用户信息");
        }

        // 读取最新数据，避免覆盖其他字段
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return ApiResponse.error("用户不存在");
        }

        // 不允许修改用户名和角色（除非是管理员）
        if ("ADMIN".equals(currentUser.getRole()) && !currentUser.getId().equals(id)) {
            // 管理员修改他人信息
            user.setId(id);
            user.setUsername(existingUser.getUsername()); // 不允许修改用户名
        } else {
            // 用户本人修改信息
            user.setId(id);
            user.setUsername(existingUser.getUsername());
            user.setRole(existingUser.getRole()); // 不允许修改角色
        }

        // 如果没有提供新密码，保持原密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        }

        User updatedUser = userService.update(user);
        return ApiResponse.success("更新用户信息成功", UserVO.fromUser(updatedUser));
    }

    @Operation(summary = "删除用户", description = "删除指定用户（仅管理员）")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }

        // 只有管理员可以删除用户
        if (!"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限删除用户");
        }

        // 确保被删除用户存在
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return ApiResponse.error("用户不存在");
        }

        // 不允许删除自己
        if (currentUser.getId().equals(id)) {
            return ApiResponse.error("不能删除自己的账号");
        }

        // 业务上采用物理删除，如需软删除可在 service 中新增逻辑
        userService.deleteById(id);
        return ApiResponse.success("删除用户成功", null);
    }

    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    @PostMapping("/{id}/change-password")
    public ApiResponse<Void> changePassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody PasswordChangeRequest request) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }

        // 只能修改自己的密码
        if (!currentUser.getId().equals(id)) {
            return ApiResponse.error(403, "只能修改自己的密码");
        }

        // 查出加密密码用于对比旧密码
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return ApiResponse.error("用户不存在");
        }

        // 验证旧密码
        if (!userService.checkPassword(request.getOldPassword(), existingUser.getPassword())) {
            return ApiResponse.error("原密码错误");
        }

        // 更新密码
        userService.updatePassword(id, request.getNewPassword());
        return ApiResponse.success("密码修改成功", null);
    }
}