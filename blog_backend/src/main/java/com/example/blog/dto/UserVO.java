package com.example.blog.dto;

import com.example.blog.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户视图对象，不包含密码等敏感信息
 * 
 * 用于向客户端返回用户信息，排除密码等敏感数据
 */
@Data
public class UserVO {
    /** 用户ID */
    private Long id;
    
    /** 用户名 */
    private String username;
    
    /** 昵称 */
    private String nickname;
    
    /** 电子邮箱 */
    private String email;
    
    /** 头像URL */
    private String avatar;
    
    /** 用户角色 */
    private String role;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 从User实体转换为UserVO
     * 
     * @param user 用户实体对象
     * @return 用户视图对象，如果输入为null则返回null
     */
    public static UserVO fromUser(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}