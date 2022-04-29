package tech.mystox.framework.service.Impl;

import org.springframework.context.ApplicationContext;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.entity.OperaContext;
import tech.mystox.framework.entity.RegisterMsg;
import tech.mystox.framework.service.MsgHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2019/8/20, 10:08.
 * company: mystox
 * description:
 * update record:
 */
public class DefaultMsgHandler implements MsgHandler {


    public DefaultMsgHandler(IaENV iaENV, ApplicationContext applicationContext) {
    }

    @Override
    public void addSubTopic(String topic, int qos) {

    }

    @Override
    public void addAckTopic(String topic, int qos) {

    }

    @Override
    public boolean isAckExists(String topic) {
        return false;
    }

    @Override
    public boolean isExists(String topic) {
        return false;
    }

    @Override
    public void removeSubTopic(String... topic) {

    }

    @Override
    public void removeAckSubTopic(String... topic) {

    }

    @Override
    public MsgResult opera(String operaCode, String msg) {
        return null;
    }

    @Override
    public MsgResult opera(OperaContext operaContext) {
        return null;
    }

    @Override
    public MsgResult opera(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public void operaAsync(String operaCode, String msg) {

    }

    @Override
    public void broadcast(String operaCode, String msg) {

    }

    @Override
    public RegisterMsg whereIsCentre() {
        return null;
    }

    @Override
    public void sendToMqtt(String serverCode, String operaCode, String payload) {

    }

    @Override
    public void sendToMqtt(String serverCode, String operaCode, int qos, String payload) {

    }

    @Override
    public MsgResult sendToMqttSync(String serverCode, String operaCode, String payload) {
        return null;
    }

    @Override
    public MsgResult sendToMqttSync(String serverCode, String operaCode, int qos, String payload, long timeout, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public boolean sendToMqttBoolean(String serverCode, String operaCode, int qos, String payload) {
        return false;
    }
}
