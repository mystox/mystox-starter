package com.kongtrolink.framework.scloud.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * Created by Eric on 2020/2/20.
 */
@Configuration
public class CommonsMultipartResolverConfig {

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getCommonsMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(2048000);
        multipartResolver.setMaxInMemorySize(2048);
        return multipartResolver;
    }
}
