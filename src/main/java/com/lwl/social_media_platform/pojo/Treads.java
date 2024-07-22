package com.lwl.social_media_platform.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
