package tech.mystox.demo.config;

import org.springframework.beans.factory.annotation.Value;
import tech.mystox.framework.config.CommonExecutorConfig;
import tech.mystox.framework.mqtt.config.ExecutorConfig;
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
public class DemoExecutorConfig extends CommonExecutorConfig {
    @Value("${executor.threadPool.corePoolSize:10}")
    private int CORE_POOL_SIZE;
    @Value("${executor.threadPool.maxPoolSize:100000}")
    private int MAX_POOL_SIZE;
    @Bean(name = "demoExecutor")
    public ThreadPoolTaskExecutor demoExecutor()
    {
        return builder( CORE_POOL_SIZE,MAX_POOL_SIZE,2000,10,"demo-");
    }
}