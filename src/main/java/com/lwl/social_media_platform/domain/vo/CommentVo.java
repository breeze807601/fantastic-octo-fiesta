package com.lwl.social_media_platform.domain.vo;

import com.lwl.social_media_platform.domain.dto.PageDTO;
import com.lwl.social_media_platform.domain.pojo.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CommentVo {
    private Comment comment;
    private PageDTO<Comment> replyPage;
}
