package com.kongtrolink.framework.register.service;

import com.kongtrolink.framework.register.runner.RegisterRunner;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by mystoxlol on 2019/1/16, 10:35.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ServiceRegistryImpl implements ServiceRegistry, Watcher {
    Logger logger = LoggerFactory.getLogger(ServiceRegistryImpl.class);
    private CountDownLatch latch = new CountDownLatch(1);
    private static final int SESSION_TIMEOUT = 60;
    private ZooKeeper zk;
    private String serviceUrl;


    @Lazy
    @Autowired(required = false)
    RegisterRunner registerRunner;


   /* public ServiceRegistryImpl(String zkServers) throws IOException, InterruptedException, KeeperException {
        build(zkServers);
    }
*/
    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }


    @Override
    public ZooKeeper getZk() {
        return this.zk;
    }

    @Override
    public void register(String serviceName, String nodeData) {

    }


    public String create(final String path, byte data[], List<ACL> acl,
                         CreateMode createMode)
            throws KeeperException, InterruptedException {
        return zk.create(path, data, acl, createMode);
    }

    @Override
    public boolean exists(String nodeData) throws KeeperException, InterruptedException {
        return zk.exists(nodeData, true) != null;
    }

    @Override
    public void build(String serviceUrl) throws InterruptedException, IOException, KeeperException {
        this.serviceUrl = serviceUrl;
        if (zk == null) {
            zk = new ZooKeeper(serviceUrl, SESSION_TIMEOUT, this);
            latch.await();
        }
        else {
            zk = new ZooKeeper(serviceUrl, SESSION_TIMEOUT, this::process);
            latch.await();
            registerRunner.multiRegister();
        }

        logger.info("connected to zookeeper "+zk.getState());

    }

    public String getData(String path) throws KeeperException, InterruptedException {
        byte[] data = zk.getData(path, true, null);
        if (data!=null)
        return new String(data);
        else return null;
    }

    public Stat setData(String path, byte[] data) throws KeeperException, InterruptedException {
        Stat stat = zk.setData(path, data, 0);
        logger.info(stat.toString());
        return stat;
    }

    @Override
    public void close() throws InterruptedException {
        if (zk != null)
            zk.close();
    }

    @Override
    public List<String> getChildren(String path) throws KeeperException, InterruptedException {
        return zk.getChildren(path, true);
    }

    private boolean reconnectionFlag = false;
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected && latch.getCount() != 0) {
            logger.warn("zookeeper connected successful...");
            latch.countDown();
        } else if (watchedEvent.getState() == Watcher.Event.KeeperState.Expired) {
            try {
                synchronized (RegisterRunner.class) {
                    logger.warn("zookeeper reconnected...");
                    latch = new CountDownLatch(1);
                    build(getServiceUrl());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }

        }
        logger.info("trigger event type: [{}] content: [{}] ", watchedEvent.getType(), watchedEvent.toString());
    }

}
