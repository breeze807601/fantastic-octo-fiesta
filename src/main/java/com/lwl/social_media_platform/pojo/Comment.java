package com.lwl.social_media_platform.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long userId;
    private String nickName;
    private String content;
    private LocalDateTime createTime;
    private Long parentId;
}