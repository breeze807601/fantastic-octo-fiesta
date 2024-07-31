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
    private Long userId;
    private String nickName;
    private String content;
    private LocalDateTime createTime;
    private Long treadsId;
    private Long parentId;
    private Long rootParentId;
    private Long toUserId;
    private String toUserNickName;
    private String toUserPic;

}