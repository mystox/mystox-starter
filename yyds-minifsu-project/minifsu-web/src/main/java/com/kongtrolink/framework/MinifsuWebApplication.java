package com.kongtrolink.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@SpringBootApplication
@EnableConfigurationProperties
@EnableAutoConfiguration(exclude = {MultipartAutoConfiguration.class})
@EnableRedisHttpSession
public class MinifsuWebApplication
{
	public static void main(String[] args) {
		SpringApplication.run(MinifsuWebApplication.class, args);
	}
}
