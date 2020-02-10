package com.kongtrolink.framework.business.scloud.his;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})
public class ScloudHistoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScloudHistoryApplication.class, args);
	}

}
