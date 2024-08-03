package com.lwl.social_media_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwl.social_media_platform.domain.pojo.User;
import com.lwl.social_media_platform.domain.vo.UserVo;

public interface UserService extends IService<User> {
    UserVo getUserById(Long id);
}
