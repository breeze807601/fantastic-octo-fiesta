package com.lwl.social_media_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.domain.pojo.User;
import com.lwl.social_media_platform.domain.vo.UserVo;
import com.lwl.social_media_platform.mapper.UserMapper;
import com.lwl.social_media_platform.service.UserService;
import com.lwl.social_media_platform.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public UserVo getUserById(Long id) {

        User user = this.getById(id);

        return BeanUtils.copyProperties(user, UserVo.class);
    }
}
