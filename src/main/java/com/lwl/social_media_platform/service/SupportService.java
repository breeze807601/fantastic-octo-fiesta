package com.lwl.social_media_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwl.social_media_platform.domain.pojo.Support;

import java.util.List;
import java.util.Map;

public interface SupportService extends IService<Support> {

    Map<Long, Long> getCurrentMaxSupportNum(List<Long> treadIds);

}
