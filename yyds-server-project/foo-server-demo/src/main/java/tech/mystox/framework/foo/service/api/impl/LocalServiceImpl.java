package tech.mystox.framework.foo.service.api.impl;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.mystox.framework.api.test.LocalService;
import tech.mystox.framework.api.test.entity.OperaParam;
import tech.mystox.framework.api.test.entity.ParamEnum;
import tech.mystox.framework.api.test.entity.ReturnEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2020/6/29, 16:13.
 * company:
 * description:
 * update record:
 */
@Service
public class LocalServiceImpl implements LocalService {
    @Value("${server.name}")
    private String serverName;

    @Override
    public String hello() {
        System.out.println("hello");
        return "hello" + serverName;
    }

    @Override
    public String hello(String param) {
        System.out.println("hello" + param);
        return "hello foo service" + serverName;
    }

    @Override
    public List<String> helloList(String param) {
        ArrayList<String> strings = new ArrayList<>();
        System.out.println(param);
        strings.add("abc");
        strings.add("123");
        strings.add(serverName);
        return strings;
    }

    @Override
    public Map helloMap(String param) {
        Map map = new HashMap();
        map.put("a", "1");
        map.put("b", "2");
        map.put(serverName, serverName);
        return map;
    }

    @Override
    public String helloParams(String param1, Integer param2) {
        //todo do something
        System.out.println(param1 + "  " + param2);
        return serverName + param1 + param2;
    }

    @Override
    public void helloParams(String param1, Integer param2, long param3) {
        System.out.println("hello param3 void");
    }

    @Override
    public ReturnEntity helloWait(String param, OperaParam operaParam) {
        try {
            System.out.println(param + JSON.toJSON(operaParam));
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("time out end");
        return new ReturnEntity();
    }

    @Override
    public ParamEnum helloEnum(ParamEnum alarm) {
        System.out.println(alarm);
        return ParamEnum.OOB;
    }

    @Override
    public ParamEnum helloEnumEntity(OperaParam operaParam, ParamEnum alarm) {
        System.out.println(operaParam);
        System.out.println(alarm);
        return ParamEnum.OOB;
    }

    @Override
    public String testPackage(String payload) {
        System.out.println("testPackage receive payload length" + payload.length());
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < 1024 * 47 * 3) {
            i++;
            sb.append(i).append("+");
        }
        System.out.println("testPackage" + i);
        return sb.toString();
    }
}
