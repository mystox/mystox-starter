package com.kongtrolink.framework.core.config.rpc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;

/**
 * Created by mystoxlol on 2019/2/25, 15:41.
 * company: kongtrolink
 * description:
 * update record:
 */
public class RpcServer
{

    private Configuration conf;
    private RPC.Builder builder;

    public RpcServer(Configuration conf, RPC.Builder builder, String addr, int port) throws IOException
    {
        this.conf = conf;
        this.builder = builder;

        builderAddress(addr, port);
    }

    void builderAddress(String addr, int port)
    {
        builder.setBindAddress(addr).setPort(port)
        ;
    }

    public RpcServer setProtocol(Class clazz, Object o) throws IOException
    {

        RPC.setProtocolEngine(conf, clazz, ProtobufRpcEngine.class);
        builder.setProtocol(clazz)
                .setInstance(o);
        return this;
    }

    public void start() throws IOException
    {
        builder.setNumHandlers(1000)//并发数
                .setVerbose(false).build().start();
    }
}
