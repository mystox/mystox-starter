package com.kongtrolink.framework.node.controller.connector;

import com.kongtrolink.framework.core.service.CommandApiInterface;
import com.kongtrolink.framework.core.service.ServiceInterface;
import com.kongtrolink.framework.node.controller.business.module.MessageModule;
import com.kongtrolink.framework.node.controller.connector.module.RouteModule;
import com.kongtrolink.framework.node.controller.connector.module.RpcModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/20, 20:58.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ConnectorService implements ServiceInterface, CommandApiInterface
{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RpcModule rpcModule;
    @Autowired
    RouteModule routeModule;
    @Autowired
    MessageModule messageModule;

    @Override
    public boolean startService()
    {
        logger.info("Connector service starting ... ...");

        if (initRouteModule())//启动路由模块
            if(initRpcModule())//启动rpc模块
                return true;
            else logger.error("connector-rpc module error...");
        else logger.error("connector-message module error...");
        return false;
    }

    private boolean initRpcModule()
    {
        return rpcModule.init();
    }

    private boolean initRouteModule()
    {
        return routeModule.init();
    }


    @Override
    public boolean restartService()
    {
        return false;
    }

    @Override
    public String commandExecute(String Order, String parameter)
    {
        return null;
    }
}
