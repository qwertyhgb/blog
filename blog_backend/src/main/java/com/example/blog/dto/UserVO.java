package com.example.blog.dto;

import com.example.blog.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户视图对象，不包含密码等敏感信息
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

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
