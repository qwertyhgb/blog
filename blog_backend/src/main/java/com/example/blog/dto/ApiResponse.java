package com.example.blog.dto;

import lombok.Data;

/**
 * API统一响应格式类
 * 
 * 封装所有API接口的响应数据，提供统一的响应格式
 * 
 * @param <T> 响应数据的泛型类型
 */
@Data
public class ApiResponse<T> {
    /** 响应状态码，200表示成功，其他值表示错误 */
    private Integer code;
    
    /** 响应消息，描述操作结果 */
    private String message;
    
    /** 响应数据，可以是任意类型 */
    private T data;
    
    /**
     * 创建成功响应（默认消息）
     * 
     * @param <T> 数据类型
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("成功");
        response.setData(data);
        return response;
    }
    
    /**
     * 创建成功响应（自定义消息）
     * 
     * @param <T> 数据类型
     * @param message 自定义成功消息
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
    
    /**
     * 创建错误响应（默认状态码500）
     * 
     * @param <T> 数据类型
     * @param message 错误消息
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(500);
        response.setMessage(message);
        return response;
    }
    
    /**
     * 创建错误响应（自定义状态码）
     * 
     * @param <T> 数据类型
     * @param code 错误状态码
     * @param message 错误消息
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
    
    /**
     * 创建错误响应（自定义状态码和数据）
     * 
     * @param <T> 数据类型
     * @param code 错误状态码
     * @param message 错误消息
     * @param data 响应数据
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}