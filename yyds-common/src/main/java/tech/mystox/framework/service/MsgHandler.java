package tech.mystox.framework.service;

import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.entity.OperaContext;
import tech.mystox.framework.entity.RegisterMsg;

import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2019/8/20, 10:08.
 * company: mystox
 * description:
 * update record:
 */
public interface MsgHandler {
    public void addSubTopic(String topic, int qos);

    public void addAckTopic(String topic, int qos);

    public boolean isAckExists(String topic);

    public boolean isExists(String topic);

    public void removeSubTopic(String... topic);

    public void removeAckSubTopic(String... topic);

    public MsgResult opera(String operaCode, String msg);

    /**
     * 指定组内通讯
     */
    public MsgResult operaGroupCode(String groupCode, String operaCode, String msg);

    public MsgResult opera(OperaContext operaContext);

    public MsgResult opera(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit);

    public void operaAsync(String operaCode, String msg);

    public void broadcast(String operaCode, String msg);

    // MsgResult slogin(String registerServerName, String registerServerVersion);
    public RegisterMsg getRegisterMsg();

    void sendToMqtt(String serverCode, String operaCode,
                    String payload) throws Exception;

    void sendToMqtt(String serverCode, String operaCode,
                    int qos,
                    String payload) throws Exception;

    /**
     * 指定注册服务通讯
     */
    MsgResult sendToMqttSync(String groupServerCode, String operaCode,
                             String payload);

    MsgResult sendToMqttSync(String serverCode, String operaCode,
                             int qos, String payload, long timeout, TimeUnit timeUnit);

    public boolean sendToMqttBoolean(String serverCode, String operaCode, int qos, String payload);


}
