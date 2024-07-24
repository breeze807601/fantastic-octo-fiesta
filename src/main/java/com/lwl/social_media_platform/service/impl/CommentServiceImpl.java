package com.lwl.social_media_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.mapper.CommentMapper;
import com.lwl.social_media_platform.domain.pojo.Comment;
import com.lwl.social_media_platform.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}
