package com.lwl.social_media_platform.domain.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Treads {
    private Long id;
    private Long userId;
    private String content;
//    private Integer supportCount;
//    private Integer treadCount;
    private String state;
    private LocalDateTime createTime;
}
