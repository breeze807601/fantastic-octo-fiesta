package com.lwl.social_media_platform.domain.vo;

import com.lwl.social_media_platform.domain.pojo.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserVo extends User {
    private Boolean isFollow;
    private Long fansNum;
    private Long followNum;
}
