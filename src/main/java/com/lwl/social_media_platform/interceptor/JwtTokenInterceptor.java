package com.lwl.social_media_platform.interceptor;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.lwl.social_media_platform.common.BaseContext;
import com.lwl.social_media_platform.utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtTokenInterceptor implements HandlerInterceptor {
    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }
        // 从请求头中获取令牌
        String token = request.getHeader("Authorization");

        // 校验令牌
        try {
            // 获取id
            Long id = JWTUtil.verify(token).getClaim("id").asLong();
            BaseContext.setCurrentId(id);
            // 通过，放行
            return true;
        } catch (Exception ex) {
            response.setStatus(401);
            throw new TokenExpiredException("token令牌已过期或用户未登录");
        }
    }
}
