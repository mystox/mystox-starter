package com.kongtrolink.framework.node.controller.business;

import com.kongtrolink.framework.core.service.ServiceInterface;
import com.kongtrolink.framework.node.controller.business.module.CommandModule;
import com.kongtrolink.framework.node.controller.business.module.TCPModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/22, 9:38.
 * company: kongtrolink
 * description: 业务接口
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

        logger.info("ApiService starting ... ...");
        if (initTCP())//启动tcp模块
            return true;
        else logger.error("apiService-tcp module error ...");
        return false;
    }

    private boolean initTCP()
    {
        tcpModule.init();
        return true;
    }

    @Override
    public boolean restartService()
    {
        return false;
    }
}
