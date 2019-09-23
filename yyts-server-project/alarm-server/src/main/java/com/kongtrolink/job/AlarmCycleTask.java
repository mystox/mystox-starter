package com.kongtrolink.job;

import com.kongtrolink.base.Contant;
import com.kongtrolink.base.MongTable;
import com.kongtrolink.enttiy.Alarm;
import com.kongtrolink.enttiy.AlarmCycle;
import com.kongtrolink.query.AlarmCycleQuery;
import com.kongtrolink.query.AlarmQuery;
import com.kongtrolink.service.AlarmCycleService;
import com.kongtrolink.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    AlarmService alarmService;

    public void execute(){
        List<Alarm> historyAlarmList = new ArrayList<>();
        List<String> historyAlarmIdList = new ArrayList<>();

        Date cueDate = new Date();
        AlarmCycleQuery alarmCycleQuery = new AlarmCycleQuery();
        alarmCycleQuery.setCurrentPage(1);
        alarmCycleQuery.setState(Contant.USEING);
        alarmCycleQuery.setPageSize(Integer.MAX_VALUE);
        List<AlarmCycle> alarmCycleList = alarmCycleService.list(alarmCycleQuery);
//        Map<String, AlarmCycle> alarmCycleMap = alarmCycleService.entity2CodeSerrviceMap(alarmCycleList);
        //获取所有实时告警
        for(AlarmCycle alarmCycle : alarmCycleList){
            AlarmQuery alarmQuery = new AlarmQuery();
            alarmQuery.setUniqueCode(alarmCycle.getUniqueCode());
            alarmQuery.setService(alarmCycle.getService());
            alarmQuery.setPageSize(Integer.MAX_VALUE);
            List<Alarm> alarmList = alarmService.list(alarmQuery, MongTable.ALARM);
            for(Alarm alarm : alarmList){
                boolean history = alarmCycle.isHistory(alarm, cueDate);
                if(history){
                    historyAlarmIdList.add(alarm.getId());
//                    alarm.set
                    historyAlarmList.add(alarm);
                }
            }
        }

    }
}
