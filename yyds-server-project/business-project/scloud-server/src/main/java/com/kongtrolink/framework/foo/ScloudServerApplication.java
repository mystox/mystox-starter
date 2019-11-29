package com.kongtrolink.framework.foo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})
public class ScloudServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScloudServerApplication.class, args);
	}

}
