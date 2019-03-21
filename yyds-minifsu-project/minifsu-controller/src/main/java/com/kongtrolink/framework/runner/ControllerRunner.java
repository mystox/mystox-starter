package com.kongtrolink.framework.runner;

import com.kongtrolink.framework.execute.ControllerExecuteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created by mystoxlol on 2019/2/25, 21:30.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class ControllerRunner implements ApplicationRunner
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ControllerExecuteService controllerExecuteService;

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        if (controllerExecuteService.startService()) //启动工作执行控制器
        {
            logger.info("controllerExecuteService start successfully!!!");
            return;
        }
        else logger.error("controllerExecuteService start error...");
        System.exit(1);
    }

}
