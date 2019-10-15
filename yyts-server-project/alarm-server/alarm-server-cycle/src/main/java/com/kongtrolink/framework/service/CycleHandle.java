package com.kongtrolink.framework.service;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.dao.AlarmCycleDao;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.query.AlarmCycleQuery;
import com.kongtrolink.framework.query.AlarmQuery;
import java.util.*;

/**
 * @Auther: liudd
 * @Date: 2019/10/14 17:22
 * @Description:
 */
public class CycleHandle implements Runnable{

    AlarmDao alarmDao;
    AlarmCycleDao alarmCycleDao;
    private int count;
    private int time;
    private int currentTime = 0;
    public static List<Alarm> currentAlarmList = new ArrayList<>();
    public static List<String> deviceIdList = new ArrayList<>();
    Map<String, List<Alarm>> idAlarmListMap = new HashMap<>();
    Map<String, List<Alarm>> enSerAlarmListMap = new HashMap<>();

    @Override
    public void run() {
        if(currentAlarmList.size() < count && currentTime <time){
            currentTime ++ ;
            return;
        }
        Date curTime = new Date();
        List<String> enServerList = new ArrayList<>();
        for(Alarm alarm : currentAlarmList){
            String enterpriseServer = alarm.getEnterpriseServer();
            enServerList.add(enterpriseServer);
            List<Alarm> alarms = enSerAlarmListMap.get(enterpriseServer);
            if(null == alarms){
                alarms = new ArrayList<>();
            }
            alarms.add(alarm);
            enSerAlarmListMap.put(enterpriseServer, alarms);
        }

        //获取企业对应的自定义周期
        List<AlarmCycle> cycleList = alarmCycleDao.getCycleList(enServerList);
        Map<String, AlarmCycle> enterServerCycleMap = new HashMap<>();
        for(AlarmCycle alarmCycle : cycleList){
            alarmCycle.initEnterpirseServer();
            enterServerCycleMap.put(alarmCycle.getEnterpriseServer(), alarmCycle);
        }
        //判定各个告警是否过期
        //liuddtodo 需要考虑多个服务同时修改情况
        List<Alarm> historyAlarmList = new ArrayList<>();
        List<String> historyIdList = new ArrayList<>();

        for(String enterServer : enSerAlarmListMap.keySet()){
            AlarmCycle alarmCycle = enterServerCycleMap.get(enterServer);
            List<Alarm> alarmList = enSerAlarmListMap.get(enterServer);
            for(Alarm alarm : alarmList){
                boolean history = AlarmCycle.isHistory(alarmCycle, alarm, curTime);
                if(history){
                    historyAlarmList.add(alarm);
                    historyIdList.add(alarm.getId());
                    //保存设备信息
                    deviceIdList.add(alarm.getDeviceId());
                    List<Alarm> id_alarmList = idAlarmListMap.get(alarm.getDeviceId());
                    if(null == id_alarmList){
                        id_alarmList = new ArrayList<>();
                    }
                    id_alarmList.add(alarm);
                    idAlarmListMap.put(alarm.getDeviceId(), id_alarmList);
                }
            }

        }

        List<String> alarmIdList = new ArrayList<>();
        Map<String, Alarm> deviceIdAlarmMap = new HashMap<>();
        List<String> deviceIdList = new ArrayList<>();
        Map<String, List<Alarm>> tableAlarmListMap = new HashMap<>();
        Date cueDate = new Date();
        AlarmCycleQuery alarmCycleQuery = new AlarmCycleQuery();
        alarmCycleQuery.setCurrentPage(1);
        alarmCycleQuery.setState(Contant.USEING);
        alarmCycleQuery.setPageSize(Integer.MAX_VALUE);
        List<AlarmCycle> alarmCycleList = alarmCycleDao.list(alarmCycleQuery);

        //删除实时告警
        AlarmQuery alarmQuery = new AlarmQuery();
        alarmQuery.setAlarmIdList(alarmIdList);
        alarmDao.deleteList(alarmQuery, MongTable.ALARM);
        //liuddtodo 从第三方获取设备信息并填充到告警对象中

        //保存历史告警到对应的历史告警表
        for(String table : tableAlarmListMap.keySet()){
            List<Alarm> alarms = tableAlarmListMap.get(table);
            alarmDao.addList(alarms, table);
        }

        //清空列表，复位计数
        currentAlarmList.clear();
        currentTime = 0;
    }

    public CycleHandle(AlarmDao alarmDao, AlarmCycleDao alarmCycleDao, int count, int time) {
        this.alarmDao = alarmDao;
        this.alarmCycleDao = alarmCycleDao;
        this.count = count;
        this.time = time;
    }
}
