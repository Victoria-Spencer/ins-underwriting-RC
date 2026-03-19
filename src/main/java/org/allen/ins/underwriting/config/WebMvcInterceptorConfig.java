package org.allen.ins.underwriting.config;

import org.allen.ins.underwriting.common.interceptor.TraceIdInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SpringMVC拦截器配置
 * 注册TraceId拦截器，指定拦截范围
 */
@Configuration
public class WebMvcInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TraceIdInterceptor())
                // 拦截核保/风控相关的所有接口
                .addPathPatterns(
                        "/underwriting/**",    // 核保相关接口
                        "/rc/decision/**"    // 风控决策相关接口
                )
                // 排除静态资源等无关接口
                .excludePathPatterns(
                        "/static/**",
                        "/swagger-ui/**"
                );
    }
}