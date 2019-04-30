package com.kongtrolink.gateway.nb.cmcc;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 项目启动主程序
 */
@org.springframework.boot.autoconfigure.SpringBootApplication(scanBasePackages = "com.kongtrolink")
@ComponentScan(value = "com.kongtrolink")
public class SpringBootApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringBootApplication.class);
    }


}
