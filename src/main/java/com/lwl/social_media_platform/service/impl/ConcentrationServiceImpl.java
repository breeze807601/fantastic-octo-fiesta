package com.lwl.social_media_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.common.BaseContext;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.mapper.ConcentrationMapper;
import com.lwl.social_media_platform.domain.pojo.Concentration;
import com.lwl.social_media_platform.service.ConcentrationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service

public class ConcentrationServiceImpl extends ServiceImpl<ConcentrationMapper, Concentration> implements ConcentrationService {
    @Override
    public Result<String> saveConcentration(Concentration concentration) {
        Long userID = BaseContext.getCurrentId();
        concentration.setUserId(userID)
                .setCreateTime(LocalDateTime.now());
        this.save(concentration);
        return Result.success("关注成功");
    }

    @Override
    public Result<String> cancelConcentration(Long id) {
        this.removeById(id);
        return Result.success("取消关注成功");
    }

    @Override
    public Result<Page<Concentration>> getFollow(int page,int pageSize) {
        Long userID = BaseContext.getCurrentId();
        Page<Concentration> concentrationPage = new Page<>(page,pageSize);
        return Result.success(this.page(concentrationPage, new LambdaQueryWrapper<Concentration>().eq(Concentration::getUserId, userID)));
    }
}
