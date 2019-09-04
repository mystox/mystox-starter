package com.kongtrolink.framework.register.service;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * Created by mystoxlol on 2019/1/16, 10:29.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface ServiceRegistry {

    public void register(String serviceName, String nodeData) throws KeeperException, InterruptedException;

    public boolean exists(String nodeData) throws KeeperException, InterruptedException;

    public void build(String serviceUrl) throws InterruptedException, IOException, KeeperException;

    String create(final String path, byte data[], List<ACL> acl, CreateMode createMode)
            throws KeeperException, InterruptedException;

    String getData(String path) throws KeeperException, InterruptedException;
    Stat setData(String path, byte data[]) throws KeeperException, InterruptedException;

    public ZooKeeper getZk();

    public void close() throws InterruptedException;

    List<String> getChildren(String path) throws KeeperException, InterruptedException;
}
