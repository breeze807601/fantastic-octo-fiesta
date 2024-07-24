package com.lwl.social_media_platform.domain.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class Concentration {
    private Long id;
    private Long userId;
    private Long toUserId;
    private LocalDateTime createTime;
}
