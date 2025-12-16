package com.example.blog.exception;

import lombok.Getter;

/**
 * 业务异常类
 *
 * 用于处理业务逻辑中的异常情况，支持使用错误码枚举
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;
    private final String message;

    /**
     * 使用错误码枚举构造异常
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 使用错误码枚举和自定义消息构造异常
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.message = customMessage;
    }

    /**
     * 使用自定义错误码和消息构造异常
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 使用错误码枚举和异常原因构造异常
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 快捷方法：抛出错误码异常
     */
    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode);
    }

    /**
     * 快捷方法：抛出带自定义消息的错误码异常
     */
    public static BusinessException of(
        ErrorCode errorCode,
        String customMessage
    ) {
        return new BusinessException(errorCode, customMessage);
    }
}
