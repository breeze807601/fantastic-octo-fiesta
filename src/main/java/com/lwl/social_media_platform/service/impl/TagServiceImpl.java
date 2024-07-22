package com.lwl.social_media_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.mapper.TagMapper;
import com.lwl.social_media_platform.mapper.UserMapper;
import com.lwl.social_media_platform.pojo.Tag;
import com.lwl.social_media_platform.pojo.User;
import com.lwl.social_media_platform.service.TagService;
import com.lwl.social_media_platform.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
}
