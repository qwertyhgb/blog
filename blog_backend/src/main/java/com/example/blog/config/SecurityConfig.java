package com.example.blog.config;

import com.example.blog.filter.JwtAuthenticationFilter;
import com.example.blog.service.IUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 安全配置类
 * 
 * 负责配置应用程序的安全策略，包括认证、授权、跨域请求等
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final IUserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造函数，注入所需的服务和组件
     * 
     * @param userService             用户服务，用于用户认证
     * @param jwtAuthenticationFilter JWT认证过滤器
     * @param passwordEncoder         密码编码器
     */
    public SecurityConfig(IUserService userService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 配置认证提供者
     * 
     * @return DaoAuthenticationProvider 实例
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * 配置认证管理器
     * 
     * @param AuthenticationConfiguration 认证配置
     * @return AuthenticationManager 实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 配置安全过滤器链
     * 
     * @param HttpSecurity HTTP安全配置
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // 启用跨域支持
                .csrf(csrf -> csrf.disable()) // 禁用CSRF防护，因为使用JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态会话管理
                .authorizeHttpRequests(authz -> authz
                        // 允许未认证访问的路径
                        .requestMatchers("/auth/**").permitAll() // 认证相关接口
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll() // 博客文章查看
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll() // 分类查看
                        .requestMatchers(HttpMethod.GET, "/tags/**").permitAll() // 标签查看
                        .requestMatchers(HttpMethod.GET, "/comments/**").permitAll() // 评论查看
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll() // 文件访问
                        // Swagger UI 和 OpenAPI 文档
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                                "/swagger-resources/**")
                        .permitAll()
                        // Actuator 健康检查端点
                        .requestMatchers("/actuator/**").permitAll()
                        // 管理员路径需要ADMIN角色
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 其他请求需要认证
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 添加JWT过滤器

        return http.build();
    }

    /**
     * 配置跨域资源共享(CORS)
     * 
     * @return CorsConfigurationSource CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 开发环境允许所有来源，生产环境应该配置具体域名
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}