package com.kongtrolink.framework.gateway.tower.heart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})
public class GatewayTowerHeartApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayTowerHeartApplication.class, args);
	}

}
