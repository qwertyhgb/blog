package com.example.blog;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String rawPassword = "admin123";
        String storedHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";
        
        System.out.println("Testing password: " + rawPassword);
        System.out.println("Stored hash: " + storedHash);
        System.out.println("Password matches: " + encoder.matches(rawPassword, storedHash));
        
        // Generate new hash
        String newHash = encoder.encode(rawPassword);
        System.out.println("\nNew hash for 'admin123': " + newHash);
    }
}
