package com.lwl.social_media_platform.domain.vo;

import com.lwl.social_media_platform.domain.pojo.User;
import lombok.Data;
@Data
public class UserLoginVo {
    private User user;
    private String token;
}
