package com.kongtrolink.framework.core.node.collect;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by mystoxlol on 2019/2/19, 13:13.
 * company: kongtrolink
 * description:
 * update record:
 */
public class BaseNodeDataCollector implements NodeDataCollector
{
    @Value("${server.bindIp}")
    private String ip;

    @Value("${server.name}")
    private String name;

    @Override
    public String getIp()
    {
        return this.ip;
    }


    @Override
    public String getName()
    {
        return this.name;
    }
}
