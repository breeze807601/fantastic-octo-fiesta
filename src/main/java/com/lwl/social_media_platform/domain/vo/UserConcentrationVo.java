package com.lwl.social_media_platform.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain = true)
public class UserConcentrationVo {
    private Long id;
    private String username;
    private String pic;
    private String nickname;
    private String sex;
    private Boolean isFollow;
}
