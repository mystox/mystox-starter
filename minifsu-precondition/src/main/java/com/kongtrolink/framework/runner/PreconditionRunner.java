package com.kongtrolink.framework.runner;

import com.kongtrolink.framework.execute.PreconditionExecuteService;
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
public class PreconditionRunner implements ApplicationRunner
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PreconditionExecuteService preconditionExecuteService;

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        if (preconditionExecuteService.startService()) //启动工作执行控制器
            logger.info("worker node server start successfully!!!");
        else logger.error("workerExecuteService start error...");

    }

}
