package com.kongtrolink.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties
public class YyjwAppServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(YyjwAppServerApplication.class, args);
	}
}
