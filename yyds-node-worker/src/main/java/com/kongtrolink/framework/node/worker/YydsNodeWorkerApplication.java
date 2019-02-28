package com.kongtrolink.framework.node.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kongtrolink.framework"})//扫描core路径包
public class YydsNodeWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(YydsNodeWorkerApplication.class, args);
	}

}
