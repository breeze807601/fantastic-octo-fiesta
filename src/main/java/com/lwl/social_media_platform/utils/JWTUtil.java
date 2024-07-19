package com.lwl.social_media_platform.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

public class JWTUtil {
    public static final String key = "lwl";
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param map
     * @return
     */
    public static String createJWT(Map<String, String> map) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        Calendar instance = Calendar.getInstance();
        // 默认1天过期
        instance.add(Calendar.DATE, 1);

        //创建jwt builder
        JWTCreator.Builder builder = JWT.create();

        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });

        String token = builder.withExpiresAt(instance.getTime())  //指定令牌过期时间
                .sign(Algorithm.HMAC256(key));  // sign
        return token;
    }

    /**
     * 验证token  合法性
     */
    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(key)).build().verify(token);
    }
    /**
     * 获取token信息方法
     */
//    public static DecodedJWT getTokenInfo(String token) {
//        DecodedJWT verify = JWT.require(Algorithm.HMAC256(key)).build().verify(token);
//        return verify;
//    }

}
