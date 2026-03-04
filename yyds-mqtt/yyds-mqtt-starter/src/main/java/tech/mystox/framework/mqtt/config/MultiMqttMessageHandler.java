package tech.mystox.framework.mqtt.config;

import org.springframework.context.SmartLifecycle;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

/**
 * \* @author: mystox
 * \* Date: 2019/11/22 17:21
 * \* Description: 批量消息处理器
 * \
 */
public class MultiMqttMessageHandler extends AbstractMessageHandler implements SmartLifecycle {
    private final AtomicBoolean running = new AtomicBoolean();
    private volatile Map<Integer, MessageHandler> mqttHandlerMap;
    private static LongAdder longAdder = new LongAdder();

    //final MqttConfig mqttConfig;
    CreateMqttOutBoundInterface createMqttOutBoundInterface;

    //@Value("${mqtt.sender.count:10}")
    private final Integer handlerCount;

    //public MultiMqttMessageHandler() {
    //    this.mqttConfig = mqttConfig;
    //}

    public MultiMqttMessageHandler(CreateMqttOutBoundInterface createMqttOutbound, Integer mqttSenderCount) {
        this.handlerCount = mqttSenderCount;
        this.createMqttOutBoundInterface = createMqttOutbound;
    }

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
            mqttHandlerMap.put(i, createMqttOutBoundInterface.create());
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
    public void handleMessageInternal(Message<?> message) {
//        Random random = new Random();
        longAdder.add(1);
        int key = longAdder.intValue();
        if (key > handlerCount - 1) {
            longAdder.reset();
            key = 0;
        }
        MyMqttPahoMessageHandler messageHandler = (MyMqttPahoMessageHandler) mqttHandlerMap.get(key);
        messageHandler.handleMessageInternal(message);
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        this.stop();
    }

    @Override
    public int getPhase() {
        return 0;
    }
}