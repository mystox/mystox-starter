package com.kongtrolink.runner;

import com.kongtrolink.service.CreateDeviceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * @Auther: liudd
 * @Date: 2019/10/31 09:42
 * @Description:
 */
@Service
public class DeliverRunner implements ApplicationRunner {

    @Autowired
    CreateDeviceInfoService alarmDeliverService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        alarmDeliverService.handle();
    }
}
