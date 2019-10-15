package com.kongtrolink.framework.runner;

import com.kongtrolink.framework.dao.AlarmCycleDao;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.service.AlarmCycleTask;
import com.kongtrolink.framework.service.CycleHandle;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
    private AlarmCycleDao alarmCycleDao;
    @Autowired
    private AlarmDao alarmDao;
    @Value("${cycle.count:300}")
    private int count;
    @Value("${cycle.interval:5}")
    private int interval;
    //轮询次数.如果到达轮询次数，即使实时告警未到达指定数量，也处理
    @Value("${cycle.time:5}")
    private int time;

    private Logger logger = org.slf4j.LoggerFactory.getLogger(CycleRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("执行周期告警填充任务和告警处理任务");
        ScheduledExecutorService taskScheduler = Executors.newSingleThreadScheduledExecutor();
        taskScheduler.scheduleAtFixedRate(new AlarmCycleTask(alarmDao, count), 10 * 1000, 10 * 1000,
                TimeUnit.MILLISECONDS);
        ScheduledExecutorService handleScheduler = Executors.newSingleThreadScheduledExecutor();
        handleScheduler.scheduleWithFixedDelay(new CycleHandle(alarmDao, alarmCycleDao, count, time), 10 * 1000, 10 * 1000,
                TimeUnit.MILLISECONDS);
    }
}
