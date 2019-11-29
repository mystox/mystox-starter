package com.kongtrolink.framework.config;

import com.kongtrolink.framework.mqtt.config.ExecutorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * \* @Author: mystox
 * \* Date: 2019/11/22 10:49
 * \* Description:
 * \
 */
@Configuration
public class DemoExecutorConfig extends ExecutorConfig {
    @Bean(name = "demoExecutor")
    public ThreadPoolTaskExecutor demoExecutor()
    {
        return builder( 6,1000000,2000,10,"demo-");
    }

}