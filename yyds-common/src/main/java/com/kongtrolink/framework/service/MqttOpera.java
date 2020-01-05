package com.kongtrolink.framework.service;

import com.kongtrolink.framework.entity.MsgResult;

import java.util.concurrent.TimeUnit;

/**
 * \* @Author: mystox
 * \* Date: 2020/1/2 14:35
 * \* Description:
 * \
 */
public interface MqttOpera {
    public MsgResult opera(String operaCode, String msg);

    public MsgResult opera(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit);

    public void broadcast(String operaCode, String msg);
    public MsgResult slogin(String registerServerName, String registerServerVersion);
}