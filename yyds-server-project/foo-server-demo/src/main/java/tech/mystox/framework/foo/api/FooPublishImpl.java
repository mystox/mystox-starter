package tech.mystox.framework.foo.api;

import org.springframework.stereotype.Service;
import tech.mystox.framework.entity.MsgResult;

/**
 * Created by mystoxlol on 2019/9/9, 13:15.
 * company: mystox
 * description: 发送消息的demo
 * update record:
 */
@Service
public class FooPublishImpl implements FooPublish {

    // @Autowired
    // MqttSender mqttSender;

    /**
     * 异步的接口实现案例
     * @param serverCode
     * @param operaCode
     * @param payload
     */
    @Override
    public void sendMsg(String serverCode, String operaCode, String payload) {
        // mqttSender.sendToMqtt(serverCode, operaCode, payload);
    }

    /**
     * 同步阻塞的实现案例
     * @param serverCode
     * @param operaCode
     * @param payload
     * @return
     */
    @Override
    public MsgResult sendMsgSyn(String serverCode, String operaCode, String payload) {
        // MsgResult s = mqttSender.sendToMqttSync(serverCode, operaCode, payload);
        return  null;
    }

    public static void main(String[] args)
    {
        Class<FooPublish> fooPublishClass = FooPublish.class;
        ClassLoader classLoader = fooPublishClass.getClassLoader();
    }
}
