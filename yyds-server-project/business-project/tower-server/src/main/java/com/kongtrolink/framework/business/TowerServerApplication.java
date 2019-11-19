package com.kongtrolink.framework.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})
public class TowerServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TowerServerApplication.class, args);
	}

}
