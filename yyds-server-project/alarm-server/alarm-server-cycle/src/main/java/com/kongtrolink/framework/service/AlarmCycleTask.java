package com.kongtrolink.framework.service;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.enttiy.Alarm;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 11:27
    填充列表中实时告警数量
 */
public class AlarmCycleTask implements Runnable{

    private AlarmDao alarmDao;
    private int count;
    private String currentAlarm = MongTable.ALARM;

    @Override
    public void run() {
        //1，判定队列中告警数量
        int size = CycleHandle.currentAlarmList.size();
        if(size<count){
            List<Alarm> alarmList = alarmDao.getAlarmList(currentAlarm, (count - size));
            CycleHandle.currentAlarmList.addAll(alarmList);
        }
    }

    public AlarmCycleTask(AlarmDao alarmDao, int count) {
        this.alarmDao = alarmDao;
        this.count = count;
    }
}
