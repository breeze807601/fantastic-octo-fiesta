package com.lwl.social_media_platform.pojo.vo;

import com.lwl.social_media_platform.pojo.User;
import lombok.Data;
@Data
public class UserLoginVo {
    private User user;
    private String token;
}
