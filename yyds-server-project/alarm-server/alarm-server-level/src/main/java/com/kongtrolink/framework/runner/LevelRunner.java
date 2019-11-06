package com.kongtrolink.framework.runner;

import com.kongtrolink.framework.service.AlarmLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Auther: liudd
 * @Date: 2019/11/6 10:27
 * @Description:
 */
@Component
public class LevelRunner implements ApplicationRunner {

    @Autowired
    AlarmLevelService alarmLevelService;
    //系统运行后，加载登记表，存储各个企业的告警等级,避免频繁从数据库获取等级
    @Override
    public void run(ApplicationArguments args) throws Exception {
        alarmLevelService.initEnterpriseLevelMap();
        alarmLevelService.initAlarmLevelMap();
    }
}
