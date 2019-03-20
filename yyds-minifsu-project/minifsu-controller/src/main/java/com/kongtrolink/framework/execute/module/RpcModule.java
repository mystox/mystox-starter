package com.kongtrolink.framework.execute.module;

import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.config.rpc.RpcServer;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotify;
import com.kongtrolink.framework.core.service.ModuleInterface;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by mystoxlol on 2019/3/14, 13:48.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class RpcModule implements ModuleInterface
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RpcServer rpcServer;
    @Autowired
    private ExecuteModule executeModule;

    @Autowired
    RpcClient rpcClient; //rpc客户端


    //如果需要服务的情况下需要初始化引擎和实现类等信息
    @Override
    public boolean init()
    {
        logger.info("controller execute-RpcModule init : protocol class[{}], impl class[{}]", RpcNotify.class, executeModule.getClass());
        try
        {
            // 初始化RpcServer.protocol方法实例,远程调用的方法体由此处初始化,初始化多个服务,多次setProtocol()即可
            rpcServer.setProtocol(RpcNotify.class, RpcNotifyProto.RpcNotify.newReflectiveBlockingService(executeModule))
                    .start();

        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

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