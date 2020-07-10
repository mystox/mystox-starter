package tech.mystox.framework.config;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public abstract class CommonExecutorConfig {

    protected ThreadPoolTaskExecutor builder(int corePoolSize, int maxPoolSize, int queueCapacity, int aliveSecondis, String threadName) {
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        poolTaskExecutor.setCorePoolSize(corePoolSize);
        //线程池维护线程的最大数量
        poolTaskExecutor.setMaxPoolSize(maxPoolSize);
        //线程池所使用的缓冲队列
        poolTaskExecutor.setQueueCapacity(queueCapacity);
        //线程池维护线程所允许的空闲时间
        poolTaskExecutor.setKeepAliveSeconds(aliveSecondis);
        poolTaskExecutor.setThreadNamePrefix(threadName);
        return poolTaskExecutor;
    }
}
