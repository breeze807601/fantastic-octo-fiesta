package com.lwl.social_media_platform.controller;

import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.dto.PageDTO;
import com.lwl.social_media_platform.domain.pojo.Comment;
import com.lwl.social_media_platform.domain.query.CommentPageQuery;
import com.lwl.social_media_platform.domain.query.ReplyPageQuery;
import com.lwl.social_media_platform.domain.vo.CommentVo;
import com.lwl.social_media_platform.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public Result<PageDTO<CommentVo>> getComment(CommentPageQuery commentPageQuery){
        return Result.success(commentService.getComment(commentPageQuery));
    }

    @GetMapping("/reply")
    public Result<PageDTO<Comment>> getReply(ReplyPageQuery replyPageQuery){
        return Result.success(commentService.replyPage(replyPageQuery));
    }

    @PostMapping("/save")
    public Result<Comment> saveComment(@RequestBody Comment comment){
        return commentService.saveComment(comment);
    }

    @DeleteMapping("/delete")
    public Result<String> deleteComment(@RequestParam("id") Long id){
        return commentService.deleteComment(id);
    }
}
