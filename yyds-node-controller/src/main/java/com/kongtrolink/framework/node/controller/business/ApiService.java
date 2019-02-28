package com.kongtrolink.framework.node.controller.business;

import com.kongtrolink.framework.core.service.ServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/22, 9:38.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ApiService implements ServiceInterface
{
    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    CommandModule commandModule;
    @Autowired
    TCPModule tcpModule;

    @Override
    public boolean startService()
    {

        if (initTCP())//启动tcp模块
            return true;
        return false;
    }

    private boolean initTCP()
    {
        logger.info("启动tcp模块...");
        tcpModule.init();
        return true;
    }

    @Override
    public boolean restartService()
    {
        return false;
    }
}
