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

    MsgResult opera(String operaCode, String msg);

    MsgResult opera(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit);

    void operaAsync(String operaCode, String msg);

    void broadcast(String operaCode, String msg);

}
