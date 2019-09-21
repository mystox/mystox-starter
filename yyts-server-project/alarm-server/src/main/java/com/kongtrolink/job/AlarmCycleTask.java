package com.kongtrolink.job;

import com.kongtrolink.enttiy.AlarmCycle;
import com.kongtrolink.query.AlarmCycleQuery;
import com.kongtrolink.service.AlarmCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 11:27
 * @Description:告警周期任务，定时轮询实时告警表。
 * 将满足自定义实时的告警迁移至历史表或者已消除表
 */
@Service
public class AlarmCycleTask {

    @Autowired
    AlarmCycleService alarmCycleService;

    public void execute(){
        AlarmCycleQuery alarmCycleQuery = new AlarmCycleQuery();
        alarmCycleQuery.setCurrentPage(1);
        alarmCycleQuery.setPageSize(Integer.MAX_VALUE);
        List<AlarmCycle> alarmCycleList = alarmCycleService.list(alarmCycleQuery);
        Map<String, AlarmCycle> alarmCycleMap = alarmCycleService.entity2CodeSerrviceMap(alarmCycleList);
    }
}
