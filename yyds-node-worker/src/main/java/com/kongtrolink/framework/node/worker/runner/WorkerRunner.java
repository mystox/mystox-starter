package com.kongtrolink.framework.node.worker.runner;

import com.kongtrolink.framework.node.worker.coordinate.CoordinateService;
import com.kongtrolink.framework.node.worker.execute.WorkerExecuteService;
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
public class WorkerRunner implements ApplicationRunner
{
    private Logger logger = LoggerFactory.getLogger(this.getClass() + "[12312312313132]");

    @Autowired
    CoordinateService coordinateService;
    @Autowired
    WorkerExecuteService workerExecuteService;

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        if (coordinateService.startService()) //启动协调注册控制器
            if (workerExecuteService.startService()) //启动工作执行控制器
                logger.info("worker node server start successfully!!!");
            else logger.error("workerExecuteService start error...");
        else logger.error("coordinateService start error...");

    }

}
