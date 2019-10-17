package com.kongtrolink.framework.gateway.runner;

import com.kongtrolink.framework.gateway.service.receiver.ReceiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created by mystoxlol on 2019/10/16, 9:54.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class ReceiverInit implements ApplicationRunner {

    @Autowired
    ReceiveService receiveService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        receiveService.start();
        //启动接收器
    }

}
