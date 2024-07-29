package com.lwl.social_media_platform.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Comment {
    private Long id;
    private Long userId;
    private String nickName;
    private String content;
    private LocalDateTime createTime;
    private Long treadsId;
    private Long parentId;
    private Long rootParenId;
    private List<Comment> child;
}