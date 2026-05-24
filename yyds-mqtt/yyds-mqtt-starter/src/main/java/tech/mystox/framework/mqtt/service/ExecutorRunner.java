package tech.mystox.framework.mqtt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * \* @author: mystox
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


    final ScheduledExecutorService mqttScheduled  ;

    final MessageBusOperator mqttSender;

    public ExecutorRunner(MessageBusOperator mqttSender) {
        this.mqttScheduled = new ScheduledThreadPoolExecutor(1);
        this.mqttSender = mqttSender;
        this.rhythm = 3;
    }



    public MessageBusOperator getMqttSender() {
        return mqttSender;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mqttScheduled.scheduleWithFixedDelay(this::runners, 1, 1, TimeUnit.SECONDS);
    }

    void runners() {
        longAdder.add(1);
        long l = longAdder.longValue();
        //callback 内存
        int callbackSize = mqttSender.getCALLBACKS().size();
        if (l > 0 && l % rhythm == 0) {
            if (callbackSize >= 50 /*&& callbackSize % 10 == 0*/)
                logger.warn("message bus callback map size: [{}]", callbackSize);
            longAdder.reset();
            logger.debug("message bus callback map size: [{}]", callbackSize);
        }
    }
}
