package com.kongtrolink.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class MinifsuCntbGWApplication
{

	public static void main(String[] args) {
		SpringApplication.run(MinifsuCntbGWApplication.class, args);
	}

}
