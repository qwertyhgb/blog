package com.example.blog;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    
    @Test
    public void generateAndVerifyPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        
        // 生成新哈希
        String newHash = encoder.encode(password);
        System.out.println("===========================================");
        System.out.println("Password: " + password);
        System.out.println("New Hash: " + newHash);
        System.out.println("Verify new hash: " + encoder.matches(password, newHash));
        
        // 验证现有哈希
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";
        System.out.println("\nExisting hash: " + existingHash);
        System.out.println("Verify existing hash: " + encoder.matches(password, existingHash));
        System.out.println("===========================================");
    }
}
