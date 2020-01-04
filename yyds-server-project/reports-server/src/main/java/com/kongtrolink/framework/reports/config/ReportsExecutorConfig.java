package com.kongtrolink.framework.reports.config;

import com.kongtrolink.framework.mqtt.config.ExecutorConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by mystoxlol on 2018/12/6, 15:23.
 * company: kongtrolink
 * description:
 * update record:
 */
@Configuration
public class ReportsExecutorConfig extends ExecutorConfig
{
    @Value("${executor.threadPool.corePoolSize:10}")
    private int CORE_POOL_SIZE;
    @Value("${executor.threadPool.maxPoolSize:10000}")
    private int MAX_POOL_SIZE;

    @Bean(name = "reportsExecutor")
    public ThreadPoolTaskExecutor reportsExecutor()
    {
        return builder( CORE_POOL_SIZE,MAX_POOL_SIZE,2000,10000,"report-");
    }

    @Bean(name = "reportsScheduled")
    ScheduledExecutorService reportsScheduled() {
        return Executors.newScheduledThreadPool(10);
    }


}