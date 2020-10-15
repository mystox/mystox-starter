package tech.mystox.framework.mqtt.service;

import tech.mystox.framework.mqtt.service.impl.ChannelSenderImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * \* @Author: mystox
 * \* Date: 2019/11/27 11:01
 * \* Description:
 * \
 */
@Component
public class ExecutorRunner implements ApplicationRunner {
    Logger logger = LoggerFactory.getLogger(ExecutorRunner.class);

    private static LongAdder longAdder = new LongAdder();

    @Value("${executor.runner.rhythm:3}")
    private int rhythm;

    @Autowired
    ThreadPoolTaskExecutor mqExecutor;

    @Autowired
    private ThreadPoolTaskExecutor mqSenderAckExecutor;

    @Autowired
    ScheduledExecutorService mqScheduled;

    @Autowired
    ChannelSenderImpl mqttSender;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mqScheduled.scheduleWithFixedDelay(this::runners, 1, 1, TimeUnit.SECONDS);
    }

    void runners() {
        longAdder.add(1);
        long l = longAdder.longValue();
        //执行器大小
        int mqttExecutorActiveCount = mqExecutor.getActiveCount();
        if (mqttExecutorActiveCount >= 50) {
            logger.warn("mqtt task executor status: pool size:[{}], active count:[{}], max pool size:[{}] ",
                    mqExecutor.getPoolSize(), mqttExecutorActiveCount, mqExecutor.getMaxPoolSize());
        }
        //回复执行器大小
        int mqttSenderAckExecutorActiveCount = mqSenderAckExecutor.getActiveCount();
        logger.debug("mqtt ack executor status: pool size:[{}], active count:[{}], max pool size:[{}] ",
                mqSenderAckExecutor.getPoolSize(), mqttSenderAckExecutorActiveCount, mqSenderAckExecutor.getMaxPoolSize());
        //callback 内存
        int callbackSize = mqttSender.getCALLBACKS().size();
        if (l > 0 && l % rhythm == 0) {
            if (callbackSize >= 50 /*&& callbackSize % 10 == 0*/)
                logger.warn("mqtt sender callback map size: [{}]", callbackSize);
            longAdder.reset();
            logger.debug("mqtt task executor status: pool size:[{}], active count:[{}], max pool size:[{}] ",
                    mqExecutor.getPoolSize(), mqttExecutorActiveCount, mqExecutor.getMaxPoolSize());
            logger.debug("mqtt ack executor status: pool size:[{}], active count:[{}], max pool size:[{}] ",
                    mqSenderAckExecutor.getPoolSize(), mqttSenderAckExecutorActiveCount, mqSenderAckExecutor.getMaxPoolSize());
            logger.debug("mqtt sender callback map size: [{}]", callbackSize);
        }
    }
}