package com.lwl.social_media_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.common.BaseContext;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.dto.PageDTO;
import com.lwl.social_media_platform.domain.pojo.Comment;
import com.lwl.social_media_platform.domain.pojo.User;
import com.lwl.social_media_platform.domain.query.CommentPageQuery;
import com.lwl.social_media_platform.domain.query.ReplyPageQuery;
import com.lwl.social_media_platform.domain.vo.CommentVo;
import com.lwl.social_media_platform.mapper.CommentMapper;
import com.lwl.social_media_platform.service.CommentService;
import com.lwl.social_media_platform.service.UserService;
import com.lwl.social_media_platform.utils.CollUtils;
import com.lwl.social_media_platform.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    private final UserService userService;

    @Override
    public Result<Comment> saveComment(Comment comment) {
        Long userId = BaseContext.getCurrentId();
        User user = userService.getById(userId);
        comment.setUserId(userId)
                .setNickName(user.getNickname())
                .setCreateTime(LocalDateTime.now());
        this.save(comment);
        return Result.success(comment);
    }

    @Override
    public Result<String> deleteComment(Long id) {
        removeComment(id);
        return Result.success("删除成功");
    }

    public Result<List<Comment>> getComment(Long treadsId) {
        List<Comment> commentList = this.list(new LambdaQueryWrapper<Comment>().eq(Comment::getTreadsId, treadsId));
        List<Comment> comments = processComments(commentList);
        return Result.success(comments);
    }

    @Override
    public PageDTO<CommentVo> getComment(CommentPageQuery commentPageQuery) {
        Page<Comment> commentPage = this.page(
                commentPageQuery.toMpPageDefaultSortByCreateTimeDesc(),
                new LambdaQueryWrapper<Comment>().eq(Comment::getTreadsId, commentPageQuery.getTreadsId())
        );

        List<Comment> commentList = commentPage.getRecords();
        ReplyPageQuery replyPageQuery = commentPageQuery.getReplyPageQuery();

        List<CommentVo> commentVoList = commentList.stream().map(comment -> {
            PageDTO<Comment> replyPage = getReply(replyPageQuery.setCommentId(comment.getId()));
            return new CommentVo(comment,replyPage);
        }).toList();

        return PageUtils.of(commentPage, commentVoList);
    }

    @Override
    public PageDTO<Comment> getReply(ReplyPageQuery replyPageQuery) {
        Page<Comment> replyPage = this.page(
                replyPageQuery.toMpPageDefaultSortByCreateTimeDesc(),
                new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, replyPageQuery.getCommentId()));
        return PageUtils.of(replyPage, replyPage.getRecords());
    }


    /**
     * 组装评论
     *
     * @param list 该动态下的全部评论
     * @return 组装后的评论
     */
    private List<Comment> processComments(List<Comment> list) {
        Map<Long, Comment> map = new HashMap<>();
        List<Comment> result = new ArrayList<>();
        // 将所有根评论加入map
        list.stream().peek(comment -> {
            if (comment.getParentId() == null) {
                result.add(comment);
            }
            map.put(comment.getId(), comment);
        });
        // 子评论加入到父评论的 child 中
        list.stream().peek(comment -> {
            Long parentId = comment.getParentId();
            if (parentId != null) { // 当前评论为子评论
                Comment p = map.get(parentId);
                if (p.getChild() == null) {
                    p.setChild(new ArrayList<>()); // child为空 则创建
                }
                p.getChild().add(comment);
            }
        });
        return result;
    }

    /**
     * 递归删除评论以及该评论的子评论
     *
     * @param id 评论id
     */
    private void removeComment(Long id) {
        this.removeById(id);// 删除该评论
        List<Comment> childList = this.list(new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, id));// 获取该评论下的子评论
        // 递归
        if (CollUtils.isNotEmpty(childList)) {
            childList.stream().peek(item -> removeComment(item.getId()));
        }
    }
}
