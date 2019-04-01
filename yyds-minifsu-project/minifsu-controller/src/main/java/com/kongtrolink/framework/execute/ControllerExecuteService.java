package com.kongtrolink.framework.execute;

import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.core.service.ServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/25, 21:17.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ControllerExecuteService implements ServiceInterface
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModuleInterface executeModule;
    @Autowired
    ModuleInterface rpcModule;
    @Override
    public boolean startService()
    {
        logger.info(this.getClass().getSimpleName()+" service start... ...");
        if(initRpcModule())
            if (initExecuteModule())
                return true;
            else logger.error("init execute module error!!!");
        else logger.error("init RPC module error!!!");

        return false;
    }

    private boolean initExecuteModule()
    {
        return executeModule.init();
    }

    private boolean initRpcModule()
    {
        return rpcModule.init();
    }

    @Override
    public boolean restartService()
    {
        return false;
    }
}