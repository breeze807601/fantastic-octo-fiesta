package com.lwl.social_media_platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.pojo.Concentration;

public interface ConcentrationService extends IService<Concentration> {

    Result<String> saveConcentration(Concentration concentration);

    Result<String> cancelConcentration(Long toUserId);

    Result<Page<Concentration>> getFollow(int page,int pageSize);
}
