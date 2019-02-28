package com.kongtrolink.framework.core.config.rpc;

import com.cass.protorpc.ProtocolEnum;
import com.cass.protorpc.RpcNotify;
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
        //设置protocol引擎
        RPC.setProtocolEngine(conf, RpcNotify.class, ProtobufRpcEngine.class);
    }

    public <T> T getProtocol(Class<T> clazz, long versionID, InetSocketAddress addr) throws IOException
    {

        return RPC.getProxy(clazz,versionID,addr, conf);
    }

    public RpcNotify getNotifyProxy(InetSocketAddress addr) throws IOException
    {
        return getProtocol(RpcNotify.class, ProtocolEnum.VERSION, addr);
    }

}
