package com.kongtrolink.framework.config;

import com.kongtrolink.framework.mqtt.config.ExecutorConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AssetManagementExecutorConfig extends ExecutorConfig {

    @Value("${executor.threadPool.corePoolSize:10}")
    private int CORE_POOL_SIZE;
    @Value("${executor.threadPool.maxPoolSize:10000}")
    private int MAX_POOL_SIZE;

    @Bean(name = "assetManagementExecutor")
    public ThreadPoolTaskExecutor reportsExecutor()
    {
        return builder( CORE_POOL_SIZE,MAX_POOL_SIZE,2000,10000,"asset-");
    }

    @Bean(name = "assetManagementScheduled")
    ScheduledExecutorService reportsScheduled() {
        return Executors.newScheduledThreadPool(10);
    }
}
