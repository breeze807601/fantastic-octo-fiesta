package com.lwl.social_media_platform.domain.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class Comment {
    private Long id;
    private Long userId;
    private String nickName;
    private String content;
    private LocalDateTime createTime;
    private Long parentId;
}