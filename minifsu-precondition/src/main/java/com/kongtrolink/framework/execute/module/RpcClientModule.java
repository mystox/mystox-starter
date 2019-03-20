package com.kongtrolink.framework.execute.module;

import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotify;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by mystoxlol on 2019/3/14, 14:12.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class RpcClientModule
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RpcClient rpcClient; //rpc客户端

    /**
     * 消息发送
     *
     * @param addr
     * @param message
     * @return
     * @throws IOException
     */
    public Object postMsg(InetSocketAddress addr, String message) throws IOException
    {
        logger.debug("post message: address:{}, context: {}",addr.toString(),message);
        RpcNotifyProto.RpcMessage rpcMessage = RpcNotifyProto.RpcMessage.newBuilder().setContent(message).build();
        rpcMessage.getDefaultInstanceForType();
        RpcNotify proxy = rpcClient.getProxy(RpcNotify.class,addr);
        RpcNotifyProto.RpcMessage rpcResult = proxy.notify(null, rpcMessage);
        return rpcResult;
    }
}
