package com.example.blog.exception;

import lombok.Getter;

/**
 * 错误码枚举
 * <p>
 * 统一管理系统中的所有错误码和错误信息
 */
@Getter
public enum ErrorCode {

    // ========== 通用错误码 1000-1999 ==========
    SUCCESS(200, "操作成功"),
    INTERNAL_ERROR(1000, "系统内部错误"),
    INVALID_PARAMETER(1001, "参数验证失败"),
    RESOURCE_NOT_FOUND(1002, "资源不存在"),
    OPERATION_FAILED(1003, "操作失败"),

    // ========== 认证/授权错误码 2000-2999 ==========
    UNAUTHORIZED(2000, "未授权，请先登录"),
    FORBIDDEN(2001, "没有权限访问此资源"),
    TOKEN_EXPIRED(2002, "令牌已过期"),
    TOKEN_INVALID(2003, "令牌无效"),
    BAD_CREDENTIALS(2004, "用户名或密码错误"),
    REFRESH_TOKEN_EXPIRED(2005, "刷新令牌已过期"),
    REFRESH_TOKEN_INVALID(2006, "刷新令牌无效"),

    // ========== 用户相关错误码 3000-3999 ==========
    USER_NOT_FOUND(3000, "用户不存在"),
    USERNAME_ALREADY_EXISTS(3001, "用户名已存在"),
    EMAIL_ALREADY_EXISTS(3002, "邮箱已被使用"),
    USER_DISABLED(3003, "用户已被禁用"),
    PASSWORD_INCORRECT(3004, "密码错误"),
    OLD_PASSWORD_INCORRECT(3005, "原密码错误"),

    // ========== 文章相关错误码 4000-4999 ==========
    POST_NOT_FOUND(4000, "文章不存在"),
    POST_ALREADY_DELETED(4001, "文章已被删除"),
    POST_NOT_PUBLISHED(4002, "文章未发布"),
    POST_TITLE_REQUIRED(4003, "文章标题不能为空"),
    POST_CONTENT_REQUIRED(4004, "文章内容不能为空"),
    POST_AUTHOR_REQUIRED(4005, "文章作者不能为空"),
    POST_CATEGORY_REQUIRED(4006, "文章分类不能为空"),

    // ========== 分类相关错误码 5000-5999 ==========
    CATEGORY_NOT_FOUND(5000, "分类不存在"),
    CATEGORY_NAME_REQUIRED(5001, "分类名称不能为空"),
    CATEGORY_ALREADY_EXISTS(5002, "分类已存在"),
    CATEGORY_HAS_POSTS(5003, "分类下还有文章，无法删除"),

    // ========== 标签相关错误码 6000-6999 ==========
    TAG_NOT_FOUND(6000, "标签不存在"),
    TAG_NAME_REQUIRED(6001, "标签名称不能为空"),
    TAG_ALREADY_EXISTS(6002, "标签已存在"),
    TAG_HAS_POSTS(6003, "标签下还有文章，无法删除"),

    // ========== 评论相关错误码 7000-7999 ==========
    COMMENT_NOT_FOUND(7000, "评论不存在"),
    COMMENT_CONTENT_REQUIRED(7001, "评论内容不能为空"),
    COMMENT_POST_REQUIRED(7002, "评论文章不能为空"),
    COMMENT_ALREADY_DELETED(7003, "评论已被删除"),
    COMMENT_NOT_ALLOWED(7004, "不允许评论"),
    PARENT_COMMENT_NOT_FOUND(7005, "父评论不存在"),

    // ========== 文件相关错误码 8000-8999 ==========
    FILE_UPLOAD_FAILED(8000, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(8001, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(8002, "文件大小超过限制"),
    FILE_NOT_FOUND(8003, "文件不存在"),
    FILE_DELETE_FAILED(8004, "文件删除失败"),

    // ========== 业务逻辑错误码 9000-9999 ==========
    DUPLICATE_OPERATION(9000, "重复操作"),
    OPERATION_TOO_FREQUENT(9001, "操作过于频繁，请稍后再试"),
    DATA_INTEGRITY_VIOLATION(9002, "数据完整性冲突"),
    INVALID_STATE(9003, "无效的状态");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取枚举
     */
    public static ErrorCode fromCode(Integer code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
    }
}
