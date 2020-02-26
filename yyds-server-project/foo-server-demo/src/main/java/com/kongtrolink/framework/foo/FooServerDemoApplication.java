package com.kongtrolink.framework.foo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;


@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"},
						exclude = MultipartAutoConfiguration.class)
public class FooServerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FooServerDemoApplication.class, args);
	}

}
