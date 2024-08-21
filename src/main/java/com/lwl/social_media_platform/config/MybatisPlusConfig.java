package com.lwl.social_media_platform.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MybatisPlus分页
 */
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
         MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
         mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
         return mybatisPlusInterceptor;
    }
}
