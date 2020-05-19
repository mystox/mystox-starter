package com.kongtrolink.framework.service;

import com.kongtrolink.framework.entity.MsgResult;

import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2020/5/19, 15:32.
 * company: kongtrolink
 * description:
 * update record:
 */

public interface IaOpera {

    MsgResult opera(String operaCode, String msg);
    MsgResult opera(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit);
    void operaAsync(String operaCode, String msg);
    void broadcast(String operaCode, String msg);
}
