package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * 存储用户基本信息，包括登录凭证、个人资料和账户状态等
 */
@Data
public class User {
    /** 用户ID，主键 */
    private Long id;
    
    /** 用户名，登录凭证 */
    private String username;
    
    /** 密码，已加密存储 */
    private String password;
    
    /** 昵称，显示名称 */
    private String nickname;
    
    /** 电子邮箱 */
    private String email;
    
    /** 头像URL */
    private String avatar;
    
    /** 用户角色，如USER、ADMIN */
    private String role;
    
    /** 账户状态，0表示禁用，1表示正常 */
    private Integer status;
    
    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 获取用户角色
     * 
     * @return 用户角色字符串
     */
    public String getRole() {
        return role;
    }

    /**
     * 设置用户角色
     * 
     * @param role 用户角色字符串
     */
    public void setRole(String role) {
        this.role = role;
    }
}