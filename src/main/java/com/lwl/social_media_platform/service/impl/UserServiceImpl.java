package com.lwl.social_media_platform.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.common.exception.LoginException;
import com.lwl.social_media_platform.domain.pojo.User;
import com.lwl.social_media_platform.domain.vo.UserLoginVo;
import com.lwl.social_media_platform.domain.vo.UserVo;
import com.lwl.social_media_platform.mapper.UserMapper;
import com.lwl.social_media_platform.service.UserService;
import com.lwl.social_media_platform.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.lwl.social_media_platform.utils.RedisConstant.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserVo getUserById(Long id) {
        String userVoStr = stringRedisTemplate.opsForValue().get(USER_KEY + id);

        if (StrUtil.isNotBlank(userVoStr)) {
            return JSONUtil.toBean(userVoStr, UserVo.class);
        } else {

            User user = this.getById(id);

            UserVo userVo = BeanUtils.copyProperties(user, UserVo.class);
            stringRedisTemplate.opsForValue().set(USER_KEY + id, JSONUtil.toJsonStr(userVo), USER_TTL, TimeUnit.DAYS);
            return userVo;
        }
    }

    @Override
    public UserVo updateUser(User user) {
        this.updateById(user);

        UserVo userVo = BeanUtils.copyProperties(user, UserVo.class);
        stringRedisTemplate.opsForValue().set(USER_KEY + user.getId(), JSONUtil.toJsonStr(userVo), USER_TTL, TimeUnit.DAYS);

        return userVo;
    }

    @Override
    public UserLoginVo login(String username, String password) {

        User user = this.lambdaQuery().eq(User::getUsername, username).one();

        if (user == null) {
            throw new LoginException("该用户不存在");
        }
        if (!user.getPassword().equals(password)) {
            throw new LoginException("密码错误");
        }

        // 生成token
        String token = UUID.randomUUID().toString();
        user.setPassword("********");
        UserLoginVo userLoginVo = new UserLoginVo()
                .setUser(user)
                .setToken(token);

        stringRedisTemplate.opsForValue().set(USER_LOGIN_KEY + userLoginVo.getToken(), JSONUtil.toJsonStr(userLoginVo),30,TimeUnit.MINUTES);

        return userLoginVo;
    }
}
