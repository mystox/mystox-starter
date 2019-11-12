package com.kongtrolink.framework.runner;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.service.CycleHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @Auther: liudd
 * @Date: 2019/10/14 14:59
 * @Description:周期自定义节拍自动
 */
@Component
public class CycleRunner implements ApplicationRunner {

    @Autowired
    CycleHandle cycleHandle;
    private static final Logger logger = LoggerFactory.getLogger(CycleRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("执行周期告警填充任务和告警处理任务");
        //使用两个单线程任务池定期执行任务
        Timer timer = new Timer(true);
        timer.schedule(new BeforHandleTimerTask(), 10 * 1000, 10 * 1000);
        ScheduledExecutorService handleExecutor = Executors.newSingleThreadScheduledExecutor();
        handleExecutor.scheduleAtFixedRate(new HandleTask(), 10 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    class BeforHandleTimerTask extends TimerTask{
        public void run(){
            cycleHandle.handleCurrentAlarmList(Contant.ONE);
        }
    }

    class HandleTask implements Runnable{
        public void run() {
            cycleHandle.handle();
        }
    }
}
