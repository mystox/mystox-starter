package com.kongtrolink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})
public class AlarmServerLevelApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlarmServerLevelApplication.class, args);
	}

}
