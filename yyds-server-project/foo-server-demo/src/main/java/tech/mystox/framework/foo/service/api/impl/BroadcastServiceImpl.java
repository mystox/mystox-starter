package tech.mystox.framework.foo.service.api.impl;

import org.springframework.stereotype.Service;
import tech.mystox.framework.api.test.BroadcastService;

import java.util.List;

@Service
public class BroadcastServiceImpl implements BroadcastService {
    @Override
    public void callHelloWorld(String name, List<String> home) {
        System.out.println("broadcast body: name:"+name+"msg:"+home.toString());
    }
}
