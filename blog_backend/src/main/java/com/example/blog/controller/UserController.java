package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.UserVO;
import com.example.blog.entity.User;
import com.example.blog.service.AuthService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;
    
    @GetMapping
    public ApiResponse<List<UserVO>> getAllUsers() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限查看用户列表");
        }
        
        List<User> users = userService.findAll();
        List<UserVO> userVOs = users.stream()
                .map(UserVO::fromUser)
                .collect(Collectors.toList());
        return ApiResponse.success("获取用户列表成功", userVOs);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<UserVO> getUserById(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        // 只有管理员或用户本人可以查看详细信息
        if (!currentUser.getId().equals(id) && !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限查看该用户信息");
        }
        
        User user = userService.findById(id);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        
        return ApiResponse.success("获取用户详情成功", UserVO.fromUser(user));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<UserVO> updateUser(@PathVariable Long id, @RequestBody User user) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        // 只有管理员或用户本人可以修改信息
        if (!currentUser.getId().equals(id) && !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限修改该用户信息");
        }
        
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
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        // 只有管理员可以删除用户
        if (!"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限删除用户");
        }
        
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return ApiResponse.error("用户不存在");
        }
        
        // 不允许删除自己
        if (currentUser.getId().equals(id)) {
            return ApiResponse.error("不能删除自己的账号");
        }
        
        userService.deleteById(id);
        return ApiResponse.success("删除用户成功", null);
    }
    
    @PostMapping("/{id}/change-password")
    public ApiResponse<Void> changePassword(@PathVariable Long id, @RequestBody com.example.blog.dto.PasswordChangeRequest request) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        // 只能修改自己的密码
        if (!currentUser.getId().equals(id)) {
            return ApiResponse.error(403, "只能修改自己的密码");
        }
        
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