package com.lwl.social_media_platform.domain.vo;

import com.lwl.social_media_platform.domain.pojo.User;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginVo {
    private User user;
    private String token;
}
