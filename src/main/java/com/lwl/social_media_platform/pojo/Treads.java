package com.lwl.social_media_platform.pojo;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Treads {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer support_count;
    private Integer tread_count;
    private String state;
    private LocalDateTime createTime;
}
