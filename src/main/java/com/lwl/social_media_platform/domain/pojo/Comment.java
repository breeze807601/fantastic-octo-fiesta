package com.lwl.social_media_platform.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 评论信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Comment {
    private Long id;
    private String content;
    private Long userId;
    private String nickname;
    private String userPic;
    private Long treadsId;
    private Long toUserId;
    private String toUserNickname;
    private Long parentId;
    private Long rootParentId;
    private LocalDateTime createTime;

}