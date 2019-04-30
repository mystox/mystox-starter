package com.kongtrolink.gateway.nb.ctcc;


import com.kongtrolink.gateway.nb.ctcc.iot.NbIotServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

/**
 * 项目启动主程序
 */
@org.springframework.boot.autoconfigure.SpringBootApplication(scanBasePackages = "com.kongtrolink")
@ComponentScan(value = "com.kongtrolink")
public class SpringBootApplication implements CommandLineRunner {

    @Resource
    NbIotServer nbIotServer;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringBootApplication.class);
    }

    @Override
    public void run(String... strings) throws Exception {
        //NB的连接电信平台获取 token
        nbIotServer.start();
    }

}
