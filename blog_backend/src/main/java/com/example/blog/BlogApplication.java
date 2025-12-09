package com.example.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 博客系统主启动类
 * 
 * @SpringBootApplication: Spring Boot 核心注解，包含自动配置、组件扫描等
 * @MapperScan: 指定 MyBatis Mapper 接口所在包路径，实现接口的自动代理
 */
@SpringBootApplication
@MapperScan("com.example.blog.mapper")
public class BlogApplication {

    /**
     * 主方法，应用程序入口点
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

}