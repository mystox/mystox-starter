package com.kongtrolink.framework.mqtt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.Lifecycle;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

/**
 * \* @Author: mystox
 * \* Date: 2019/11/22 17:21
 * \* Description:
 * \
 */
public class MultiMqttMessageHandler extends AbstractMessageHandler implements Lifecycle {
    private final AtomicBoolean running = new AtomicBoolean();
    private volatile Map<Integer, MessageHandler> mqttHandlerMap;
    private static LongAdder longAdder = new LongAdder();

    @Autowired
    MqttConfig mqttConfig;

    @Value("${spring.mqtt.sender.count:10}")
    private Integer handlerCount;

    @Override
    public void start() {
        if (!this.running.getAndSet(true)) {
            doStart();
        }
    }

    @Override
    public void stop() {
        if (this.running.getAndSet(false)) {
            doStop();
        }
    }

    private void doStart() {
        mqttHandlerMap = new ConcurrentHashMap<>();
        for (int i = 0; i < handlerCount; i++) {
            mqttHandlerMap.put(i, mqttConfig.createMqttOutbound());
        }
    }

    @Override
    public boolean isRunning() {
        return this.running.get();
    }

    private void doStop() {
        for (Map.Entry<Integer, MessageHandler> e : mqttHandlerMap.entrySet()) {
            MessageHandler handler = e.getValue();
            ((MyMqttPahoMessageHandler) handler).doStop();
        }
    }

    @Override
    protected void handleMessageInternal(Message<?> message) throws Exception {
//        Random random = new Random();
        longAdder.add(1);
        int key = longAdder.intValue();
        if (key > handlerCount - 1) {
            longAdder.reset();
            key = 0;
        }
        MyMqttPahoMessageHandler messageHandler = (MyMqttPahoMessageHandler) mqttHandlerMap.get(key);
        System.out.println(messageHandler.toString() +"---------------------------"+key);
        messageHandler.handleMessageInternal(message);
    }
}