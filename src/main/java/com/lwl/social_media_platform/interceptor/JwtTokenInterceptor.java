package com.lwl.social_media_platform.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lwl.social_media_platform.common.BaseContext;
import com.lwl.social_media_platform.common.exception.LoginException;
import com.lwl.social_media_platform.domain.vo.UserLoginVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

import static com.lwl.social_media_platform.utils.RedisConstant.USER_LOGIN_KEY;

@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public JwtTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 基于redis的token检验
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @return boolean
     * @throws Exception exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }
        // 从请求头中获取令牌
        String token = request.getHeader("Authorization");

        String userStr = stringRedisTemplate.opsForValue().get(USER_LOGIN_KEY + token);
        if (StrUtil.isBlank(userStr)) {
            throw new LoginException("token令牌已过期或用户未登录");
        } else {
            UserLoginVo userLoginVo = JSONUtil.toBean(userStr, UserLoginVo.class);
            Long id = userLoginVo.getUser().getId();
            BaseContext.setCurrentId(id);
            stringRedisTemplate.expire(USER_LOGIN_KEY + token, 30, TimeUnit.MINUTES);
            return true;
        }
    }
}
