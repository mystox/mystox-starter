package com.kongtrolink.gateway.nb.ctcc.runner;


import com.kongtrolink.gateway.nb.ctcc.execute.MessageExecuteService;
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
public class AlarmRunner implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MessageExecuteService workerExecuteService;

    public void run(ApplicationArguments args) throws Exception {
        if (workerExecuteService.startService()) //启动工作执行控制器
        {
            logger.info("worker node server start successfully!!!");
            return;
        }
        else logger.error("workerExecuteService start error...");
        System.exit(1);
    }

}
