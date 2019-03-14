package com.kongtrolink.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.kongtrolink.framework")
public class MinifsuClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinifsuClientApplication.class, args);
	}

}
