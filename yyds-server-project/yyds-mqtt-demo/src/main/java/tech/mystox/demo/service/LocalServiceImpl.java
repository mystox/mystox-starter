package tech.mystox.demo.service;

import tech.mystox.framework.api.test.LocalService;
import tech.mystox.framework.api.test.entity.OperaParam;
import tech.mystox.framework.api.test.entity.ParamEnum;
import tech.mystox.framework.api.test.entity.ReturnEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/8/15, 13:33.
 * company: mystox
 * description:
 * update record:
 */
// @Component
public class LocalServiceImpl implements LocalService {


    @Override
    public String hello() {
        return null;
    }

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

    @Override
    public List<String> helloList(String param) {
        return null;
    }

    @Override
    public Map helloMap(String param) {
        return null;
    }

    @Override
    public String helloParams(String param1, Integer param2) {
        return null;
    }

    @Override
    public void helloParams(String param1, Integer param2, long param3) {

    }

    @Override
    public ReturnEntity helloWait(String param, OperaParam operaParam) {
        return null;
    }

    @Override
    public ParamEnum helloEnum(ParamEnum alarm) {
        System.out.println(alarm);
        return null;
    }

    @Override
    public ParamEnum helloEnumEntity(OperaParam operaParam, ParamEnum alarm) {
        return null;
    }

    @Override
    public String testPackage(String payload) {
        return null;
    }
}
