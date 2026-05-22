package tech.mystox.framework.mqtt.service;

public interface MessageBusTransport {

    void publish(String topic, int qos, String payload) throws Exception;

    void subscribe(MessageBusChannel channel, String topic, int qos);

    void unsubscribe(MessageBusChannel channel, String... topics);

    boolean isSubscribed(MessageBusChannel channel, String topic);

    void start();

    void stop();
}
