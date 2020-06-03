package tech.mystox.framework.foo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/24 9:18
 * \* Description: 复制这个配置类到具体的模块项目中 类打上@configuration注解 具体bean做个性化配置
 * \
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getCommonsMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8");
        multipartResolver.setMaxUploadSize(2097152000);
        multipartResolver.setMaxInMemorySize(10000000);
        return multipartResolver;
    }

}
