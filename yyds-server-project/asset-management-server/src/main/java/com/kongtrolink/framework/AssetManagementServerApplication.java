package com.kongtrolink.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework.mqtt"})
@EnableAutoConfiguration(exclude = {MultipartAutoConfiguration.class})
public class AssetManagementServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetManagementServerApplication.class, args);
    }

}
