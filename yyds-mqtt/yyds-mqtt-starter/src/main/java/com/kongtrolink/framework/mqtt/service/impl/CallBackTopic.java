package com.kongtrolink.framework.mqtt.service.impl;

import com.kongtrolink.framework.entity.MqttResp;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by mystoxlol on 2019/9/5, 8:49.
 * company: kongtrolink
 * description: 回调加锁函数，从ack topic 回复中获取消息结果
 * update record:
 */
public class CallBackTopic implements Callable<MqttResp> {
    private CountDownLatch latch = new CountDownLatch(1);

    private MqttResp result;

    public void callback(MqttResp result) {
        this.result = result;
        latch.countDown();
    }

    @Override
    public MqttResp call() throws Exception {
        latch.await();
        return result;
    }

}
