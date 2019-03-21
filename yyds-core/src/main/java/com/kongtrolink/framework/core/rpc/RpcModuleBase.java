package com.kongtrolink.framework.core.rpc;

import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotify;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by mystoxlol on 2019/3/21, 10:30.
 * company: kongtrolink
 * description: 一些共同的rpc客户端方法
 * update record:
 */
public class RpcModuleBase
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    RpcClient rpcClient; //rpc客户端服务
    /**
     * 消息发送
     *
     * @param msgId   msgId可以为空
     * @param addr
     * @param payload
     * @return
     * @throws IOException
     */
    public RpcNotifyProto.RpcMessage postMsg(String msgId, InetSocketAddress addr, String payload) throws IOException
    {
        RpcNotify proxy = rpcClient.getProxy(RpcNotify.class, addr);
        //发送消息体
        RpcNotifyProto.RpcMessage rpcMessage = RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.REQUEST)
                .setPayload(payload)
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                .build();
        logger.info("post message: address:[{}],msgId:[{}],msgType:[{}],payload:[{}]", addr.toString(), msgId, rpcMessage.getType(), payload);
        RpcNotifyProto.RpcMessage rpcResult = proxy.notify(null, rpcMessage);
        return rpcResult;
    }
    /**
     * 消息发送
     * @param addr
     * @param payload
     * @return
     * @throws IOException
     */
    public RpcNotifyProto.RpcMessage postMsg(InetSocketAddress addr, String payload) throws IOException
    {
        return postMsg(null, addr, payload);
    }
}
