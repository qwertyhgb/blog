package com.example.blog.exception;

/**
 * 业务异常类
 * 
 * 用于处理业务逻辑中的异常情况，如参数校验失败、权限不足等
 */
public class BusinessException extends RuntimeException {
    
    /**
     * 构造函数
     * 
     * @param message 异常信息
     */
    public BusinessException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 异常信息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}