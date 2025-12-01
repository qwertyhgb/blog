package com.example.blog.service;

import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.RegisterRequest;
import com.example.blog.entity.User;
import com.example.blog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public Map<String, String> login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = userService.findByUsername(loginRequest.getUsername());
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("refreshToken", refreshToken);
        tokenMap.put("type", "Bearer");
        
        return tokenMap;
    }
    
    public User register(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setNickname(registerRequest.getNickname());
        user.setEmail(registerRequest.getEmail());
        
        return userService.register(user);
    }
    
    public String refreshToken(String refreshToken) {
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        User user = userService.findByUsername(username);
        
        if (user != null && jwtUtil.validateToken(refreshToken, username)) {
            return jwtUtil.generateToken(user.getUsername(), user.getRole());
        }
        
        throw new RuntimeException("无效的刷新令牌");
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userService.findByUsername(username);
        }
        return null;
    }
}