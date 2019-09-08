package com.kongtrolink.framework.mqtt.service.impl;

import com.kongtrolink.framework.entity.MqttResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by mystoxlol on 2019/9/5, 8:49.
 * company: kongtrolink
 * description:
 * update record:
 */
public class CallBackTopic implements Callable<MqttResp> {

    private Logger logger = LoggerFactory.getLogger(CallBackTopic.class);

    private Object lockObj = new Object();
    private CountDownLatch latch = new CountDownLatch(1);
    private final Lock lock = new ReentrantLock();
    private final Condition con = lock.newCondition();

    private boolean flag = true;

    private MqttResp result;

    public void callback(MqttResp result) {
        System.out.println(this.toString());
        logger.info("jiesuo");
        this.result = result;
        flag = false;
//        this.lockObj.notify();
        latch.countDown();
//        this.con.signal();
    }

    @Override
    public MqttResp call() throws Exception {
        System.out.println(this.toString());
        logger.info("----------------加锁");
        latch.await();
       /* lock.lock();
        try {
            while(flag) {
                System.out.println("阻塞");
                con.await();
            }

            con.signal();
        } finally {
            lock.unlock();
        }*/
        System.out.println("-----------锁");
        return result;
    }


}
