package com.kongtrolink.framework.execute.module;

import com.kongtrolink.framework.core.service.ModuleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/25, 19:25.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class VpnExecuteModule implements ModuleInterface
{


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${vpn.osVersion}")
    private String osVersion;
    @Value("${vpn.agent}")
    private String vpnAgent;
    @Autowired
    private ThreadPoolTaskExecutor vpnExecutor;

    @Override
    public boolean init()
    {
        logger.info("VpnExecute-execute module init");
        initVpnExecutorTask();
        //初始化线程池之类的基础任务服务
        return true;
    }

    /**
     * 初始化vpn业务执行器
     */
    private void initVpnExecutorTask() {

        logger.info("vpn [{}] [{}] executor task init... ",osVersion,vpnAgent);

    }


}
