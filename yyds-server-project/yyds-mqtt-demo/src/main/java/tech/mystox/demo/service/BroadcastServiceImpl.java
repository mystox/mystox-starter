package tech.mystox.demo.service;

import org.springframework.stereotype.Service;
import tech.mystox.framework.api.test.BroadcastService;

import java.util.List;

/**
 * Created by mystox on 2021/5/21, 18:26.
 * company:
 * description:
 * update record:
 */
@Service
public class BroadcastServiceImpl implements BroadcastService {
    @Override
    public void callHelloWorld(String name, List<String> home) {

    }

    @Override
    public void callHelloWorld2(String name, List<String> home) {
        System.out.println("demo  demo broadcast body: name:"+name+"msg:"+home.toString());
    }
}
