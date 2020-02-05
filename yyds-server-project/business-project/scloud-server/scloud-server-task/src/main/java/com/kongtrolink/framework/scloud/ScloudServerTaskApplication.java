package com.kongtrolink.framework.scloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})
public class ScloudServerTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScloudServerTaskApplication.class, args);
	}

}
