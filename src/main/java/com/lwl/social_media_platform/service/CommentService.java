package com.lwl.social_media_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.dto.PageDTO;
import com.lwl.social_media_platform.domain.pojo.Comment;
import com.lwl.social_media_platform.domain.query.CommentPageQuery;
import com.lwl.social_media_platform.domain.query.ReplyPageQuery;
import com.lwl.social_media_platform.domain.vo.CommentVo;

public interface CommentService extends IService<Comment> {

    Result<Comment> saveComment(Comment comment);
    Result<String> deleteComment(Long id);
    PageDTO<CommentVo> getComment(CommentPageQuery commentPageQuery);
    PageDTO<Comment> getReply(ReplyPageQuery replyPageQuery);
}
