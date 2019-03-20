package com.kongtrolink.yyjw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/24 9:18
 * \* Description:
 * \
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/AppResources/**").addResourceLocations(ResourceUtils.FILE_URL_PREFIX + "./AppResources/");
    }
}