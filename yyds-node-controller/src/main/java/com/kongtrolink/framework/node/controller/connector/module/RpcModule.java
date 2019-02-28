package com.kongtrolink.framework.node.controller.connector.module;

import com.cass.protorpc.RpcNotify;
import com.cass.rpc.protorpc.RpcNotifyProto;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.service.ModuleInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by mystoxlol on 2019/2/21, 9:10.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class RpcModule implements ModuleInterface
{
    @Autowired
    RpcClient rpcClient; //rpc客户端

    /**
     * 消息发送
     * @param addr
     * @param message
     * @return
     * @throws IOException
     */
    public Object postMsg(InetSocketAddress addr, String message) throws IOException
    {
        RpcNotifyProto.RpcMessage rpcMessage = RpcNotifyProto.RpcMessage.newBuilder().setContent(message).build();
        RpcNotify proxy = rpcClient.getNotifyProxy(addr);
        RpcNotifyProto.RpcMessage rpcResult= proxy.notify(null, rpcMessage);
        return rpcResult;
    }

    @Override
    public boolean init()
    {
        return true;
    }
}
