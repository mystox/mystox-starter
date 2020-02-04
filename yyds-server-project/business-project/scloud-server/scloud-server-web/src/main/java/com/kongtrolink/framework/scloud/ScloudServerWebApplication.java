package com.kongtrolink.framework.scloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})
public class ScloudServerWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScloudServerWebApplication.class, args);
	}

}
