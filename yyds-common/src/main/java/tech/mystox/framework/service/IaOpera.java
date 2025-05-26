package tech.mystox.framework.service;

import tech.mystox.framework.entity.MsgResult;

import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2020/5/19, 15:32.
 * company: mystox
 * description: ia rpc框架操作类
 * update record:
 */

public interface IaOpera {

    //MsgResult opera(String operaCode, Object msg);
    MsgResult opera(String operaCode, Object... msg);

    //MsgResult opera(String groupServiceCode, String operaCode, Object msg);
    MsgResult operaTarget(String groupServiceCode, String operaCode, Object... msg);

    MsgResult opera(String operaCode, int qos, long timeout, TimeUnit timeUnit, Object... msg);

    void operaAsync(String operaCode, Object... msg);
    void operaTargetAsync(String groupServiceCode, String operaCode, Object... msg) throws Exception;

    void broadcast(String operaCode, Object... msg);


}
