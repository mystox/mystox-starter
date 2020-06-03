package tech.mystox.framework.service.Impl;

import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.service.MsgHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2020/5/25, 8:50.
 * company: mystox
 * description:
 * update record:
 */
public abstract class MsgAdapter implements MsgHandler {

    private MsgResult operaBalance(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit, boolean setFlag, boolean async, List<String> topicArr, String routePath) {

        return null;
    }

}
