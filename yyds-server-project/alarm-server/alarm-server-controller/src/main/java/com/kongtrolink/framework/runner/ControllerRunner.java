package com.kongtrolink.framework.runner;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.config.ReportOperateConfig;
import com.kongtrolink.framework.config.ResloverOperateConfig;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.mqtt.impl.AlarmEntranceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: liudd
 * @Date: 2019/11/6 15:38
 * @Description:
 */
@Service
public class ControllerRunner implements ApplicationRunner {

    @Autowired
    ReportOperateConfig reportOperateConfig;
    @Autowired
    ResloverOperateConfig resloverOperateConfig;
    @Autowired
    AlarmEntranceImpl alarmEntrance;
    @Autowired
    AlarmDao alarmDao;
    private String currentAlarmTable = MongTable.ALARM_CURRENT;
    private static final Logger logger = LoggerFactory.getLogger(ControllerRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        reportOperateConfig.initConfigMap();
//        resloverOperateConfig.initConfigMap();
        ScheduledExecutorService handleExecutor = Executors.newSingleThreadScheduledExecutor();
        handleExecutor.scheduleAtFixedRate(new handleCurAlarmTask(), 10 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * @auther: liudd
     * @date: 2019/11/11 15:58
     * 功能描述:每隔1秒钟将实时告警存储到实时表中
     */
    class handleCurAlarmTask implements Runnable{

        @Override
        public void run() {
            List<Alarm> alarms = alarmEntrance.handleCurAlarmList(null, Contant.ZERO);
            logger.debug("save current alarm, size:{}", alarms.size());
            if(null != alarms && alarms.size() > 0){
                alarmDao.addList(alarms, currentAlarmTable);
            }
        }
    }
}
