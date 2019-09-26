package com.kongtrolink.framework.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

/**
 * Created by mystox on 2018/6/19.
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 600, redisNamespace = "db02")
public class HttpSessionConfig
{

    //    @Bean
//    public HttpSessionIdResolver httpSessionStrategy()//springboot 2.0 需要将HttpSessionStrategy 改为idResolver
//    {
//        return new CookieHttpSessionIdResolver();
//    }
    @Bean
    public HttpSessionStrategy httpSessionStrategy()
    {
        return new CookieHttpSessionStrategy();
    }

    @Bean
    public ConfigureRedisAction configureRedisAction()
    {
        return ConfigureRedisAction.NO_OP;
    }




}
