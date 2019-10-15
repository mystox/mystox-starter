package com.kongtrolink.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework.mqtt"})
public class AssetManagementServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetManagementServerApplication.class, args);
    }

}
