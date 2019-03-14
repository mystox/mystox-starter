package com.kongtrolink.framework.core.node.register;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.RegistryContext;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;

/**
 * Created by mystoxlol on 2019/2/19, 9:11.
 * company: kongtrolink
 * description:
 * update record:
 */
public abstract class DefaultServiceRegistry implements ServerRegistryProvider
{


    @Autowired
    protected ZooKeeper zooKeeper;


    @Override
    public void unregister(Object context)
    {

    }


    @Override
    public byte[] getNodeData(Object context) throws KeeperException, InterruptedException
    {
        RegistryContext registryContext = (RegistryContext) context;
        return zooKeeper.getData(registryContext.getPath(), false, null);
    }

    @Override
    public <T> T getNodeData(Object context, Class<T> clazz) throws KeeperException, InterruptedException
    {
        byte[] data = getNodeData(context);
        if (data != null)
            return JSONObject.parseObject(data, clazz, null);
        else return null;
    }


    @Override
    public void setData(Object context) throws UnsupportedEncodingException, KeeperException, InterruptedException
    {
        RegistryContext registryContext = (RegistryContext) context;
        zooKeeper.setData(registryContext.getPath(), JSONObject.toJSONString(registryContext.getData()).getBytes("utf-8"), -1);
    }

    public boolean exists(String path) throws KeeperException, InterruptedException
    {
        return zooKeeper.exists(path, true) != null;
    }

    public String create(Object context, CreateMode createMode) throws KeeperException, InterruptedException, UnsupportedEncodingException
    {
        RegistryContext registryContext = (RegistryContext) context;
        return zooKeeper.create(registryContext.getPath(),
                JSONObject.toJSONString(registryContext.getData()).getBytes("utf-8"),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
    }


}
