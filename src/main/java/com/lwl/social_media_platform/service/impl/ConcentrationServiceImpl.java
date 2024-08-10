package com.lwl.social_media_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.common.BaseContext;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.dto.PageDTO;
import com.lwl.social_media_platform.domain.pojo.Concentration;
import com.lwl.social_media_platform.domain.pojo.User;
import com.lwl.social_media_platform.domain.query.ConcentrationPageQuery;
import com.lwl.social_media_platform.domain.vo.UserVo;
import com.lwl.social_media_platform.mapper.ConcentrationMapper;
import com.lwl.social_media_platform.service.ConcentrationService;
import com.lwl.social_media_platform.service.UserService;
import com.lwl.social_media_platform.utils.BeanUtils;
import com.lwl.social_media_platform.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcentrationServiceImpl extends ServiceImpl<ConcentrationMapper, Concentration> implements ConcentrationService {
    private final UserService userService;
    @Override
    public Result<String> saveConcentration(Concentration concentration) {
        Long userID = BaseContext.getCurrentId();
        concentration.setUserId(userID)
                .setCreateTime(LocalDateTime.now());
        this.save(concentration);
        return Result.success("关注成功");
    }

    @Override
    public Result<String> cancelConcentration(Long toUserId) {
        Long userId = BaseContext.getCurrentId();
        this.remove(
                new LambdaQueryWrapper<Concentration>()
                        .eq(Concentration::getToUserId,toUserId)
                        .eq(Concentration::getUserId,userId)
        );
        return Result.success("取消关注成功");
    }

    @Override
    public Result<PageDTO<UserVo>> getConcentration(ConcentrationPageQuery concentrationPageQuery) {
        return Result.success(getUserVoPageDTO(Concentration::getToUserId,Concentration::getUserId,concentrationPageQuery));
    }

    @Override
    public Result<PageDTO<UserVo>> getToConcentration(ConcentrationPageQuery concentrationPageQuery) {
        return Result.success(getUserVoPageDTO(Concentration::getUserId,Concentration::getToUserId,concentrationPageQuery));
    }

    @Override
    public Result<Long> getConcentrationNum() {
        return Result.success(getNum(Concentration::getToUserId));
    }

    @Override
    public Result<Long> getToConcentrationNum() {
        return Result.success(getNum(Concentration::getUserId));
    }

    /**
     * 根据传入方法 判断是获取关注列表还是粉丝列表
     * @param user 获取当前用户关注列表/获取当前该用户的粉丝列表
     * @param toUser 根据 user 获取详细用户信息
     * @param concentrationPageQuery 分页条件
     * @return userVo 分页
     */
    private PageDTO<UserVo> getUserVoPageDTO(SFunction<Concentration,Long> user,SFunction<Concentration,Long> toUser,ConcentrationPageQuery concentrationPageQuery){
        Long userId = BaseContext.getCurrentId();
        Page<Concentration> concentrationPage = this.lambdaQuery()
                .eq(user, userId)
                .page(concentrationPageQuery.toMpPageDefaultSortByCreateTimeDesc());

        List<Concentration> concentrationList = concentrationPage.getRecords();

        List<Long> toUserIdList = concentrationList.stream().map(toUser).toList();
        List<User> toUserList = userService.list(new LambdaQueryWrapper<User>().in(User::getId, toUserIdList));

        List<UserVo> toUserVoList = toUserList.stream().map(userItem -> BeanUtils.copyProperties(userItem, UserVo.class)).toList();

        return PageUtils.of(concentrationPage, toUserVoList);
    }

    /**
     * 获取关注数
     * @param function 根据传入方法判断获取关注数还是被关注数
     * @return 关注数
     */
    private Long getNum(SFunction<Concentration,Long> function){
        Long userId = BaseContext.getCurrentId();
        return this.lambdaQuery().eq(function, userId).count();
    }

}
