package com.lwl.social_media_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.domain.pojo.Support;
import com.lwl.social_media_platform.mapper.SupportMapper;
import com.lwl.social_media_platform.service.SupportService;
import org.springframework.stereotype.Service;

@Service
public class SupportServiceImpl extends ServiceImpl<SupportMapper, Support> implements SupportService {
}
