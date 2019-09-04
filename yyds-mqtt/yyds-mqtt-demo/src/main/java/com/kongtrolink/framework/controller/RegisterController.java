package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mystoxlol on 2019/8/29, 9:46.
 * company: kongtrolink
 * description:
 * update record:
 */
@Lazy
@RestController
@RequestMapping("/register")
public class RegisterController
{
    ServiceRegistry serviceRegistry;
    @Autowired
    public void setZooKeeper(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }


    @RequestMapping("/getZookeeper")
    public String getZookeeper(@RequestParam(required = false) String path) {
        long sessionId = serviceRegistry.getZk().getSessionId();
        List<String> children = null;
        try {
//            path = "/mqtt";
            String data = serviceRegistry.getData(path);
            System.out.println(data);
            children = serviceRegistry.getChildren(path);
            System.out.println(children);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionId+"："+ JSONObject.toJSONString(children);
    }
}
