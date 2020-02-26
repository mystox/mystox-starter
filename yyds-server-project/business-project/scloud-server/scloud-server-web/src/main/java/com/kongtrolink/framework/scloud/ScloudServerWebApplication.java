package com.kongtrolink.framework.scloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})
@EnableAutoConfiguration(exclude = {MultipartAutoConfiguration.class})
public class ScloudServerWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScloudServerWebApplication.class, args);
	}

}
