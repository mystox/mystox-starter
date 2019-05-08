package com.kongtrolink.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by mystoxlol on 2018/12/6, 15:23.
 * company: kongtrolink
 * description:
 * update record:
 */
@Configuration
public class ExecutorConfig
{
    @Value("${executor.threadPool.corePoolSize}")
    private int CORE_POOL_SIZE;
    @Value("${executor.threadPool.maxPoolSize}")
    private int MAX_POOL_SIZE;

    @Bean(name = "vpnExecute")
    public ThreadPoolTaskExecutor taskExecutor()
    {
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        poolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        //线程池维护线程的最大数量
        poolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        //线程池所使用的缓冲队列
        poolTaskExecutor.setQueueCapacity(200);
        //线程池维护线程所允许的空闲时间
        poolTaskExecutor.setKeepAliveSeconds(10000);
        poolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return poolTaskExecutor;
    }
}
