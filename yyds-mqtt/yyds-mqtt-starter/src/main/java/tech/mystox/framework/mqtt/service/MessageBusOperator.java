package tech.mystox.framework.mqtt.service;

import org.springframework.messaging.Message;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.entity.MsgRsp;
import tech.mystox.framework.mqtt.service.impl.CallSubpackageMsg;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface MessageBusOperator {

    void sendToMqtt(String serverCode, String operaCode, String payload) throws Exception;

    void sendToMqtt(String serverCode, String operaCode, int qos, String payload) throws Exception;

    boolean sendToMqttBoolean(String serverCode, String operaCode, int qos, String payload);

    MsgResult sendToMqttSync(String serverCode, String operaCode, String payload);

    MsgResult sendToMqttSync(String serverCode, String operaCode, int qos, String payload, long timeout, TimeUnit timeUnit);

    void messageReceiver(Message<String> message);

    Map<String, CallSubpackageMsg<MsgRsp>> getCALLBACKS();
}
