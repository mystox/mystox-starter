package com.kongtrolink.framework.execute.module;

import com.cass.protorpc.RpcNotify;
import com.cass.rpc.protorpc.RpcNotifyProto;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.config.rpc.RpcServer;
import com.kongtrolink.framework.core.service.ModuleInterface;
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
        RpcNotifyProto.RpcMessage rpcMessage = RpcNotifyProto.RpcMessage.newBuilder().setContent(message).build();
        RpcNotify proxy = rpcClient.getNotifyProxy(addr);
        RpcNotifyProto.RpcMessage rpcResult = proxy.notify(null, rpcMessage);
        return rpcResult;
    }

    //如果需要服务的情况下需要初始化引擎和实现类等信息
    @Override
    public boolean init()
    {

        logger.info("workerExecute-RPC module init : protocol class[{}], impl class[{}]", RpcNotify.class, executeModule.getClass());
        try
        {
            rpcServer.setProtocol(RpcNotify.class,
                    (executeModule)).start();
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }


        return true;
    }
}