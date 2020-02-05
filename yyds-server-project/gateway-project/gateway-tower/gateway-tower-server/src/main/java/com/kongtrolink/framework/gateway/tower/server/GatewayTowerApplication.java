package com.kongtrolink.framework.gateway.tower.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})
public class GatewayTowerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayTowerApplication.class, args);
	}

}
