package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String avatar;
    private String role;
    private Integer status; // 0禁用，1正常
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}