package com.example.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置类
 * 
 * 负责配置密码加密方式，使用BCrypt算法对用户密码进行加密存储
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 配置密码编码器Bean
     * 
     * @return PasswordEncoder BCrypt密码编码器实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}