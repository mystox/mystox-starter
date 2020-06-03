package tech.mystox.framework.service;

import tech.mystox.framework.api.LocalService;
import tech.mystox.framework.stereotype.OperaCode;
import tech.mystox.framework.stereotype.Register;
import org.springframework.stereotype.Component;

/**
 * Created by mystoxlol on 2019/8/15, 13:33.
 * company: mystox
 * description:
 * update record:
 */
@Component
@Register
public class LocalServiceImpl implements LocalService {


    @Override
    @OperaCode
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
