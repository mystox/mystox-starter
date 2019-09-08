package com.kongtrolink.framework.service;

import com.kongtrolink.framework.api.LocalService;
import org.springframework.stereotype.Component;

/**
 * Created by mystoxlol on 2019/8/15, 13:33.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class LocalServiceImpl implements LocalService {
    @Override
    public String hello(String param) {
        try {
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("这是local收到的消息：============"+param);
        return "hello_ack" + System.currentTimeMillis();
}
}
