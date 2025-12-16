package com.example.blog.service;

import com.example.blog.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * 用户服务接口
 */
public interface IUserService extends UserDetailsService {

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户
     */
    User findByUsername(String username);

    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户
     */
    User findById(Long id);

    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    List<User> findAll();

    /**
     * 注册用户
     * 
     * @param user 用户信息
     * @return 注册成功的用户
     */
    User register(User user);

    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 更新后的用户
     */
    User update(User user);

    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    void deleteById(Long id);

    /**
     * 验证密码
     * 
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean checkPassword(String rawPassword, String encodedPassword);

    /**
     * 更新密码
     * 
     * @param userId      用户ID
     * @param newPassword 新密码
     */
    void updatePassword(Long userId, String newPassword);
}
