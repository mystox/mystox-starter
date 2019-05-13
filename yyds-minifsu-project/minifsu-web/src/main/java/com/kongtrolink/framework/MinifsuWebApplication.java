package com.kongtrolink.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties
@EnableAutoConfiguration(exclude = {MultipartAutoConfiguration.class})
public class MinifsuWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MinifsuWebApplication.class, args);
    }
}
