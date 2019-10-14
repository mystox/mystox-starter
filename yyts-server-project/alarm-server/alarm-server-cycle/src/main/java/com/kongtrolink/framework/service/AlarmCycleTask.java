package com.kongtrolink.framework.service;

import org.springframework.stereotype.Service;


/**
 * @Auther: liudd
 * @Date: 2019/9/21 11:27
 * @Description:告警周期任务，定时轮询实时告警表。
 * 将满足自定义实时的告警迁移至历史表或者已消除表
 */
@Service
public class AlarmCycleTask {

//    @Autowired
//    AlarmCycleService alarmCycleService;
//    @Autowired
//    AlarmService alarmService;
//
//    public void execute(){
//        List<String> alarmIdList = new ArrayList<>();
//        Map<String, Alarm> deviceIdAlarmMap = new HashMap<>();
//        List<String> deviceIdList = new ArrayList<>();
//        Map<String, List<Alarm>> tableAlarmListMap = new HashMap<>();
//        Date cueDate = new Date();
//        AlarmCycleQuery alarmCycleQuery = new AlarmCycleQuery();
//        alarmCycleQuery.setCurrentPage(1);
//        alarmCycleQuery.setState(Contant.USEING);
//        alarmCycleQuery.setPageSize(Integer.MAX_VALUE);
//        List<AlarmCycle> alarmCycleList = alarmCycleService.list(alarmCycleQuery);
//        for(AlarmCycle alarmCycle : alarmCycleList){
//            AlarmQuery alarmQuery = new AlarmQuery();
//            alarmQuery.setUniqueCode(alarmCycle.getUniqueCode());
//            alarmQuery.setService(alarmCycle.getService());
//            alarmQuery.setPageSize(Integer.MAX_VALUE);
//            List<Alarm> alarmList = alarmService.list(alarmQuery, MongTable.ALARM);
//            for(Alarm alarm : alarmList){
//                boolean history = alarmCycle.isHistory(alarm, cueDate);
//                if(history){
//                    alarmIdList.add(alarm.getId());
//                    alarm.setCycleId(alarmCycle.getId());
//                    //设备编码+告警id作为key，兼容一个设备多个告警
//                    deviceIdAlarmMap.put(alarm.getDeviceId() + alarm.getId(), alarm);
//                    deviceIdList.add(alarm.getDeviceId());
//                    String uniqueService = alarm.getUniqueService();
//                    List<Alarm> alarms = tableAlarmListMap.get(uniqueService);
//                    if(null == alarms){
//                        alarms = new ArrayList<>();
//                    }
//                    alarms.add(alarm);
//                    tableAlarmListMap.put(uniqueService, alarms);
//                }
//            }
//        }
//        //删除实时告警
//        AlarmQuery alarmQuery = new AlarmQuery();
//        alarmQuery.setAlarmIdList(alarmIdList);
//        alarmService.deleteList(alarmQuery, MongTable.ALARM);
//        //liuddtodo 从第三方获取设备信息并填充到告警对象中
//
//        //保存历史告警到对应的历史告警表
//        for(String table : tableAlarmListMap.keySet()){
//            List<Alarm> alarms = tableAlarmListMap.get(table);
//            alarmService.addList(alarms, table);
//        }
//    }
}
