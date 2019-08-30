package com.kongtrolink.framework.controller;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    ZooKeeper zooKeeper;
    @Autowired(required = false)
    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }


    @RequestMapping("/getZookeeper")
    public String getZookeeper() {
        long sessionId = zooKeeper.getSessionId();
        return sessionId+"";
    }
}
