package com.kongtrolink.framework.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

import java.net.UnknownHostException;

/**
 * Created by mystox on 2018/6/19.
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800, redisNamespace = "db02")
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

    /**
     * 设置spring session redis 序列化方式
     * @return
     */
    @Bean
    public SessionRepository sessionRepository(RedisTemplate<Object,Object> redisTemplate) throws UnknownHostException {
        RedisOperationsSessionRepository sessionRepository =  new RedisOperationsSessionRepository(redisTemplate);
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        sessionRepository.setDefaultSerializer(fastJsonRedisSerializer);
        sessionRepository.setDefaultMaxInactiveInterval(1800);
        return sessionRepository;
    }
}
