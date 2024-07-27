package com.lwl.social_media_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.common.BaseContext;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.mapper.CommentMapper;
import com.lwl.social_media_platform.domain.pojo.Comment;
import com.lwl.social_media_platform.service.CommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Override
    public Result<Comment> saveComment(Comment comment) {
        Long userId = BaseContext.getCurrentId();
        comment.setUserId(userId)
                .setCreateTime(LocalDateTime.now());
        this.save(comment);
        return Result.success(comment);
    }

    @Override
    public Result<String> deleteComment(Long id) {
        Long userId = BaseContext.getCurrentId();
        Comment comment = this.getById(id);
        if (Objects.equals(comment.getUserId(), userId)) {
            this.removeById(id);
            return Result.success("删除评论成功");
        }else {
            return Result.error("您无法删除他人的评论");
        }
    }
}
