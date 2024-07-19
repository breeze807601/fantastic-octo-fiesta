package com.lwl.social_media_platform.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Concentration {
    private Long id;
    private Long userId;
    private Long toUserId;
    private LocalDateTime createTime;
}
