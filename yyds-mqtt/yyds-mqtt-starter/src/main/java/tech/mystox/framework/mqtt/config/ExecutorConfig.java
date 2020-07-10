package tech.mystox.framework.mqtt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tech.mystox.framework.config.CommonExecutorConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by mystoxlol on 2018/12/6, 15:23.
 * company: mystox
 * description:
 * update record:
 */
@Configuration
public class ExecutorConfig extends CommonExecutorConfig {
    @Value("${executor.threadPool.corePoolSize:10}")
    private int CORE_POOL_SIZE;
    @Value("${executor.threadPool.maxPoolSize:100000}")
    private int MAX_POOL_SIZE;

    @Bean(name = "logExecutor")
    public ThreadPoolTaskExecutor logExecutor()
    {
        return builder(CORE_POOL_SIZE, MAX_POOL_SIZE, 5000, 30000, "log-");
    }


    @Bean(name = "mqttExecutor")
    public ThreadPoolTaskExecutor mqttExecutor()
    {
        return builder(CORE_POOL_SIZE, MAX_POOL_SIZE, 5000, 30000, "mqttExecutor-");
    }

    /*
        @Bean(name = "mqttSenderExecutor")
        public ThreadPoolTaskExecutor mqttSender()
        {
            return builder(CORE_POOL_SIZE, MAX_POOL_SIZE, 2000, 10000, "mqttSender-");
        }*/
    @Bean(name = "mqttScheduled")
    ScheduledExecutorService mqttScheduled() {
        return Executors.newScheduledThreadPool(10);
    }

    @Bean(name = "mqttSenderAckExecutor")
    public ThreadPoolTaskExecutor mqttAck()
    {
        return builder(CORE_POOL_SIZE, MAX_POOL_SIZE, 2000, 10000, "mqttAck-");
    }


}
