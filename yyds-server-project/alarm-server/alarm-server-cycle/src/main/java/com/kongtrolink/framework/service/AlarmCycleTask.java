package com.kongtrolink.framework.service;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.enttiy.Alarm;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 11:27
    填充列表中实时告警数量
 */
public class AlarmCycleTask implements Runnable{

    private AlarmDao alarmDao;
    private int count;
    private String currentAlarm = MongTable.ALARM_CURRENT;

    @Override
    public void run() {
        //1，判定队列中告警数量
        int size = CycleHandle.getCurrentAlarmSize();
        System.out.printf("AlarmCycleTask填充队列告警：%s， 设置总数：%s  %n", size, count);
        if(size<count){
            int diff = count - size;
            List<Alarm> alarmList = alarmDao.getAlarmList(currentAlarm, diff);
            System.out.printf("AlarmCycleTask获取新告警：%s %n", alarmList.size());
            CycleHandle.handleCurrentAlarmList(alarmList, Contant.ONE);
        }
    }

    public AlarmCycleTask(AlarmDao alarmDao, int count) {
        this.alarmDao = alarmDao;
        this.count = count;
    }
}
