package com.kongtrolink.framework.gateway.iaiot.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})
public class GatewayCommonBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayCommonBaseApplication.class, args);
	}

}
