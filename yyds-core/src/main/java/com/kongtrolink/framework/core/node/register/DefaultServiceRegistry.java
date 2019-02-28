package com.kongtrolink.framework.core.node.register;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;

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
    public Object getNodeData(Object context)
    {
        return null;
    }

    @Override
    public void setData(Object context)
    {
        //todo 设置节点信息
    }

}
