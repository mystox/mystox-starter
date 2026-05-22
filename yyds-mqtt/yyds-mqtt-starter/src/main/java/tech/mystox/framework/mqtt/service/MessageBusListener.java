package tech.mystox.framework.mqtt.service;

import java.util.Map;

@FunctionalInterface
public interface MessageBusListener {
    void onMessage(String topic, String payload, Map<String, Object> headers);
}
