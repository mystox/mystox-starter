package tech.mystox.framework.mqtt.config;

import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

/**
 * Created by mystoxlol on 2019/8/5, 14:35.
 * company: mystox
 * description:
 * update record:
 */
public class MqttConfigInstance {

    private static final DirectChannel mqttOutboundChannel = new DirectChannel();
    private static final DirectChannel mqttReplyChannel = new DirectChannel();
    private static final DirectChannel mqttInboundChannel = new DirectChannel();

    private static class SingleInstance {
        private final static MqttConfigInstance instance = new MqttConfigInstance();
        static {
            mqttOutboundChannel.setComponentName("outboundChannel");
            mqttReplyChannel.setComponentName("replyChannel");
            mqttInboundChannel.setComponentName("inboundChannel");
        }
    }

    public static MqttConfigInstance getInstance() {
        return MqttConfigInstance.SingleInstance.instance;
    }


    /**
     * MQTT信息通道（生产者:订阅人）
     *
     * @return {@link MessageChannel}
     */
    public MessageChannel mqttOutboundChannel() {
        return mqttOutboundChannel;
    }

    /**
     * MQTT信息通道（回调）
     *
     * @return
     */
    public MessageChannel mqttReplyChannel() {
        return mqttReplyChannel;
    }

    /**
     * MQTT信息通道（消费者）
     *
     * @return {@link MessageChannel}
     */
    public MessageChannel mqttInboundChannel() {
        return mqttInboundChannel;
    }

}
