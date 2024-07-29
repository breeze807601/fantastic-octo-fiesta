package com.lwl.social_media_platform.domain.vo;

import com.lwl.social_media_platform.domain.dto.PageDTO;
import com.lwl.social_media_platform.domain.pojo.Comment;
import lombok.Data;

@Data
public class CommentVo {
    private Comment comment;
    private PageDTO<Comment> commentList;
}
