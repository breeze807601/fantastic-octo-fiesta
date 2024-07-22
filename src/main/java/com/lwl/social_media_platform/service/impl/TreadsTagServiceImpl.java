package com.lwl.social_media_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.mapper.TagMapper;
import com.lwl.social_media_platform.mapper.TreadsTagMapper;
import com.lwl.social_media_platform.pojo.Tag;
import com.lwl.social_media_platform.pojo.TreadsTag;
import com.lwl.social_media_platform.service.TagService;
import com.lwl.social_media_platform.service.TreadsTagService;
import org.springframework.stereotype.Service;

@Service
public class TreadsTagServiceImpl extends ServiceImpl<TreadsTagMapper, TreadsTag> implements TreadsTagService {
}
