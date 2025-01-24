package tech.mystox.framework.mqtt.config;

import org.springframework.messaging.MessageHandler;

/**
 * Created by mystox on 2025/1/21, 16:12.
 * company:
 * description:
 * update record:
 */
@FunctionalInterface
public interface CreateMqttOutBoundInterface {
    MessageHandler create();
}
