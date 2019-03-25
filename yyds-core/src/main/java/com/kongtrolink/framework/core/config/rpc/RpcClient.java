package com.kongtrolink.framework.core.config.rpc;

import com.kongtrolink.framework.core.rpc.ProtocolEnum;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by mystoxlol on 2019/1/21, 13:10.
 * company: kongtrolink
 * description:
 * update record:
 */
//@Component
public class RpcClient
{
    private Configuration conf;

    public RpcClient(Configuration conf)
    {
        this.conf = conf;
    }

    public <T> T getProtocol(Class<T> clazz, long versionID, InetSocketAddress addr, Long timeOut) throws IOException
    {
        //设置protocol引擎
        if (timeOut != null) conf.set("ipc.client.rpc-timeout.ms", String.valueOf(timeOut));//如果包含超时时间条件，则设置超时时间
        RPC.setProtocolEngine(conf, clazz, ProtobufRpcEngine.class);
        return RPC.getProxy(clazz, versionID, addr, conf);
    }

    public <T> T getProxy(Class<T> clazz, InetSocketAddress addr, Long timeOut) throws IOException
    {
        return getProtocol(clazz, ProtocolEnum.VERSION, addr, timeOut);
    }

    public <T> T getProxy(Class<T> clazz, InetSocketAddress addr) throws IOException
    {
        return getProtocol(clazz, ProtocolEnum.VERSION, addr, null);
    }

}
