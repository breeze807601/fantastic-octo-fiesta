package com.lwl.social_media_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.pojo.Comment;

public interface CommentService extends IService<Comment> {

    Result<Comment> saveComment(Comment comment);
    Result<String> deleteComment(Long id);
}
