package tech.mystox.framework.mqtt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tech.mystox.framework.mqtt.service.impl.ChannelSenderImpl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * \* @Author: mystox
 * \* Date: 2019/11/27 11:01
 * \* Description:
 * \
 */
//@Component
public class ExecutorRunner implements ApplicationRunner {
    Logger logger = LoggerFactory.getLogger(ExecutorRunner.class);

    private static LongAdder longAdder = new LongAdder();

    //@Value("${executor.runner.rhythm:3}")
    private int rhythm;

    final ThreadPoolTaskExecutor mqttExecutor;

    private final ThreadPoolTaskExecutor mqttSenderAckExecutor;

    final ScheduledExecutorService mqttScheduled;

    final ChannelSenderImpl mqttSender;

    public ExecutorRunner(ThreadPoolTaskExecutor mqttExecutor, ThreadPoolTaskExecutor mqttSenderAckExecutor, ScheduledExecutorService mqttScheduled, ChannelSenderImpl mqttSender) {
        this.mqttExecutor = mqttExecutor;
        this.mqttSenderAckExecutor = mqttSenderAckExecutor;
        this.mqttScheduled = mqttScheduled;
        this.mqttSender = mqttSender;
        this.rhythm = 3;
    }

    public ThreadPoolTaskExecutor getMqttExecutor() {
        return mqttExecutor;
    }

    public ThreadPoolTaskExecutor getMqttSenderAckExecutor() {
        return mqttSenderAckExecutor;
    }

    public ScheduledExecutorService getMqttScheduled() {
        return mqttScheduled;
    }

    public ChannelSenderImpl getMqttSender() {
        return mqttSender;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mqttScheduled.scheduleWithFixedDelay(this::runners, 1, 1, TimeUnit.SECONDS);
    }

    void runners() {
        longAdder.add(1);
        long l = longAdder.longValue();
        //执行器大小
        int mqttExecutorActiveCount = mqttExecutor.getActiveCount();
        if (mqttExecutorActiveCount >= 50) {
            logger.warn("mqtt task executor status: pool size:[{}], active count:[{}], max pool size:[{}] ",
                    mqttExecutor.getPoolSize(), mqttExecutorActiveCount, mqttExecutor.getMaxPoolSize());
        }
        //回复执行器大小
        int mqttSenderAckExecutorActiveCount = mqttSenderAckExecutor.getActiveCount();
        logger.debug("mqtt ack executor status: pool size:[{}], active count:[{}], max pool size:[{}] ",
                mqttSenderAckExecutor.getPoolSize(), mqttSenderAckExecutorActiveCount, mqttSenderAckExecutor.getMaxPoolSize());
        //callback 内存
        int callbackSize = mqttSender.getCALLBACKS().size();
        if (l > 0 && l % rhythm == 0) {
            if (callbackSize >= 50 /*&& callbackSize % 10 == 0*/)
                logger.warn("mqtt sender callback map size: [{}]", callbackSize);
            longAdder.reset();
            logger.debug("mqtt task executor status: pool size:[{}], active count:[{}], max pool size:[{}] ",
                    mqttExecutor.getPoolSize(), mqttExecutorActiveCount, mqttExecutor.getMaxPoolSize());
            logger.debug("mqtt ack executor status: pool size:[{}], active count:[{}], max pool size:[{}] ",
                    mqttSenderAckExecutor.getPoolSize(), mqttSenderAckExecutorActiveCount, mqttSenderAckExecutor.getMaxPoolSize());
            logger.debug("mqtt sender callback map size: [{}]", callbackSize);
        }
    }
}