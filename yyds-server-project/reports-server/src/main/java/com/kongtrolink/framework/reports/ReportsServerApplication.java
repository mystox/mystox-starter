package com.kongtrolink.framework.reports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})
public class ReportsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportsServerApplication.class, args);
	}

}
