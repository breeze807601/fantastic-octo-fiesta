package com.lwl.social_media_platform.controller;

import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.pojo.Comment;
import com.lwl.social_media_platform.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public Result<List<Comment>> getComment(@RequestParam("id") Long treadsId){
        return commentService.getComment(treadsId);
    }

    @PostMapping("/save")
    public Result<Comment> saveComment(@RequestBody Comment comment){
        return commentService.saveComment(comment);
    }

    @DeleteMapping("/delete")
    public Result<String> deleteComment(@RequestParam("/id") Long id){
        return commentService.deleteComment(id);
    }
}
