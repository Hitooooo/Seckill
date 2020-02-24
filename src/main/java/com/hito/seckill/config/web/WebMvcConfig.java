package com.hito.seckill.config.web;

import com.hito.seckill.common.access.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 配置拦截器和参数解析器
 *
 * @author HitoM
 * @date 2020/2/16 22:48
 **/
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    private final UserArgumentResolver userArgumentResolver;
    private final AccessInterceptor accessInterceptor;

    public WebMvcConfig(UserArgumentResolver userArgumentResolve, AccessInterceptor accessInterceptor) {
        this.accessInterceptor = accessInterceptor;
        this.userArgumentResolver = userArgumentResolve;
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

    @Override
    protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }
}