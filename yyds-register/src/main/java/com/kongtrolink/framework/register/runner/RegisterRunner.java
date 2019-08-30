package com.kongtrolink.framework.register.runner;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.register.config.ZookeeperConfig;
import com.kongtrolink.framework.register.entity.RegisterMsg;
import com.kongtrolink.framework.register.entity.RegisterType;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by mystoxlol on 2019/8/28, 13:31.
 * company: kongtrolink
 * description: 注册启动类
 * update record:
 */
@Component
public class RegisterRunner implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(RegisterRunner.class);

    @Value("${server.name}")
    private String serverCode;

    @Autowired
    ServiceScanner localServiceScanner;
    @Autowired
    ServiceScanner jarServiceScanner;
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<RegisterSub> subList = new ArrayList<>();
        subList = scanSubList();
        RegisterMsg registerMsg = getRegisterMsg();
        register(registerMsg, subList);

    }


//    @Autowired
//    ZookeeperConfig zookeeperConfig;


    private ZooKeeper zooKeeper;

    @Lazy
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)//singleton
    @Bean()
    public ZooKeeper zooKeeper() throws IOException {
//        ZkClient zkClient = new ZkClient();
        return zooKeeper;
    }

    ZooKeeper builder(String zookeeperUrl) throws IOException {
        this.zooKeeper = new ZooKeeper(zookeeperUrl, 4000, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected && latch.getCount() != 0) {
                latch.countDown();
            } else if (watchedEvent.getState() == Watcher.Event.KeeperState.Expired) {
                ZooKeeper zooKeeper = null;
                try {
                    synchronized (ZookeeperConfig.class) {
                        latch = new CountDownLatch(1);
                        zooKeeper = zooKeeper();
                        zooKeeper.close();
                        this.zooKeeper = builder("");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                latch = new CountDownLatch(1);
            }
            logger.info("已经触发了[{}]事件！内容: {} ", watchedEvent.getType(), watchedEvent.toString());
        });
        return zooKeeper;

    }

    private void register(RegisterMsg registerMsg, List<RegisterSub> subList) {
        RegisterType registerType = registerMsg.getRegisterType();
        if (RegisterType.ZOOKEEPER.equals(registerType)) {
            try {
                builder(registerMsg.getRegisterUrl());

                for (RegisterSub sub : subList) {
                    String operaCode = sub.getOperaCode();
                    String nodePath = "/mqtt/sub/" + serverCode + "/" + operaCode;

                    if (zooKeeper.exists("/mqtt", true) == null)
                        zooKeeper.create("/mqtt", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    if (zooKeeper.exists("/mqtt/sub", true) == null)
                        zooKeeper.create("/mqtt/sub", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    if (zooKeeper.exists("/mqtt/sub/" + serverCode, true) == null)
                        zooKeeper.create("/mqtt/sub/" + serverCode, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    if (zooKeeper.exists(nodePath, true) == null)
                        zooKeeper.create(nodePath, JSONObject.toJSONBytes(sub), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 扫描服务能力
     *
     * @return
     */
    private List<RegisterSub> scanSubList() {
        List<RegisterSub> subList = new ArrayList<>();
        subList.addAll(scanLocal());
        subList.addAll(scanJar());
        subList.addAll(scanHttp());
        return subList;
    }

    private List<RegisterSub> scanHttp() {
        List<RegisterSub> subList = new ArrayList<>();
        //todo 暂不实现
        return subList;
    }

    private List<RegisterSub> scanLocal() {
        return localServiceScanner.getSubList();
    }

    private List<RegisterSub> scanJar() {
        //todo
        List<RegisterSub> subList = jarServiceScanner.getSubList();
        return subList;
    }


    public RegisterMsg getRegisterMsg() {
        RegisterMsg registerMsg = new RegisterMsg();
        registerMsg.setRegisterType(RegisterType.ZOOKEEPER);
        registerMsg.setRegisterUrl("172.16.5.26:2181,172.6.5.26:2182,172.6.5.26:2183");
        return registerMsg;

    }

}
