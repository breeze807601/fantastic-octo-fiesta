package com.lwl.social_media_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.common.BaseContext;
import com.lwl.social_media_platform.domain.pojo.Concentration;
import com.lwl.social_media_platform.domain.pojo.User;
import com.lwl.social_media_platform.domain.vo.UserVo;
import com.lwl.social_media_platform.mapper.UserMapper;
import com.lwl.social_media_platform.service.ConcentrationService;
import com.lwl.social_media_platform.service.UserService;
import com.lwl.social_media_platform.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final ConcentrationService concentrationService;
    @Override
    public UserVo getUserById(Long id) {
        Long currentUserId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Concentration> wrapper = new LambdaQueryWrapper<>();

        User user = this.getById(id);

        Concentration isFollow = concentrationService.getOne(
                wrapper.eq(Concentration::getUserId, currentUserId)
                        .eq(Concentration::getToUserId, user.getId())
        );

        long followCount = concentrationService.count(wrapper.eq(Concentration::getUserId, id));
        long fansCount = concentrationService.count(wrapper.eq(Concentration::getToUserId, id));

        UserVo userVo = BeanUtils.copyProperties(user, UserVo.class);
        userVo.setIsFollow(isFollow != null)
                .setFansNum(fansCount)
                .setFollowNum(followCount);

        return userVo;
    }
}
