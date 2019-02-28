package com.kongtrolink.framework.node.worker.execute.module;

import com.cass.protorpc.RpcNotify;
import com.kongtrolink.framework.core.config.rpc.RpcServer;
import com.kongtrolink.framework.core.service.ModuleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by mystoxlol on 2019/2/25, 19:21.
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
    private RpcNotify executeModule;

    @Override
    public boolean init()
    {
        logger.info("workerExecute-RPC module init : protocol class[{}], impl class[{}]",RpcNotify.class,executeModule.getClass());
        try
        {
            rpcServer.setProtocol(RpcNotify.class, executeModule).start();
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
