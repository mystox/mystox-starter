package com.kongtrolink.framework.reports.config;

import com.kongtrolink.framework.core.interceptor.SessionTimeoutInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/24 9:18
 * \* Description: 复制这个配置类到具体的模块项目中 类打上@configuration注解 具体bean做个性化配置
 * \
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/AppResources/**").addResourceLocations(ResourceUtils.FILE_URL_PREFIX + "./AppResources/");
    }
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getCommonsMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8");
        multipartResolver.setMaxUploadSize(20971520);
        multipartResolver.setMaxInMemorySize(100);
        return multipartResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(new SessionTimeoutInterceptor()).addPathPatterns("/**","/res/page/back/center.html")
                .excludePathPatterns("/**").excludePathPatterns("/commonFunc/logout",
                "/httpInterface/*", "/commonInterface/*", "/app/v2/user/**", "/phoneFunc/**", "/redirect", "/testMain",
                "/index.html").excludePathPatterns("/error");//"error"的拦截是为了异常的抛出不会重定向

    }
}
