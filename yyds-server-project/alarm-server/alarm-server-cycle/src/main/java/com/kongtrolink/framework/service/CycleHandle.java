package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.dao.AlarmCycleDao;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.mqtt.CIRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: liudd
 * @Date: 2019/10/14 17:22
 * @Description:
 */
//liuddtodo 后期需要将修改队列的方法同步
@Service
public class CycleHandle{

    @Autowired
    AlarmDao alarmDao;
    @Autowired
    AlarmCycleDao alarmCycleDao;
    @Autowired
    MqttSender mqttSender;
    @Value("${cycle.count:300}")
    private int count;
    @Value("${cycle.time:5}")
    private int time;
    @Value("${cycle.assets:theAssets}")
    private String assetsServer;
    @Value("${cycle.getCI:getCI}")
    private String getCI;

    private int currentTime = 0;

    public static List<Alarm> currentAlarmList = new ArrayList<>();
    private String currentTable = MongTable.ALARM_CURRENT;
    private String historyTable = MongTable.ALARM_HISTORY;

    public void handle(){
        ScheduledExecutorService handleScheduler = Executors.newSingleThreadScheduledExecutor();
        handleScheduler.scheduleWithFixedDelay(new handleTask(), 10 * 1000, 10 * 1000,
                TimeUnit.MILLISECONDS);
    }


    class handleTask implements Runnable{
        public void run() {
            if(currentAlarmList.size() < count && currentTime <time){
                currentTime ++ ;
                return;
            }
            List<String> deviceIdList = new ArrayList<>();
            Map<String, List<Alarm>> deviceId_alarmListMap = new HashMap<>();
            Map<String, List<Alarm>> enterpirseServer_alarmListMap = new HashMap<>();

            Date curTime = new Date();
            List<String> enterpriseServerCodeList = new ArrayList<>();
            for(Alarm alarm : currentAlarmList){
                String enterpriseServer = alarm.getEnterpriseServer();
                enterpriseServerCodeList.add(enterpriseServer);
                List<Alarm> alarms = enterpirseServer_alarmListMap.get(enterpriseServer);
                if(null == alarms){
                    alarms = new ArrayList<>();
                }
                alarms.add(alarm);
                enterpirseServer_alarmListMap.put(enterpriseServer, alarms);
            }

            //获取企业对应的自定义周期
            List<AlarmCycle> cycleList = alarmCycleDao.getCycleList(enterpriseServerCodeList);
            Map<String, AlarmCycle> enterpriseServer_cycleMap = new HashMap<>();
            for(AlarmCycle alarmCycle : cycleList){
                alarmCycle.initEnterpirseServer();
                enterpriseServer_cycleMap.put(alarmCycle.getEnterpriseServer(), alarmCycle);
            }
            //判定各个告警是否过期
            //liuddtodo 需要考虑多个服务同时修改情况，后期再处理
            List<Alarm> historyAlarmList = new ArrayList<>();
            List<String> historyAlarmIdList = new ArrayList<>();

            for(String enterpirseServer : enterpirseServer_alarmListMap.keySet()){
                AlarmCycle alarmCycle = enterpriseServer_cycleMap.get(enterpirseServer);
                List<Alarm> alarmList = enterpirseServer_alarmListMap.get(enterpirseServer);
                for(Alarm alarm : alarmList){
                    boolean history = AlarmCycle.isHistory(alarmCycle, alarm, curTime);
                    if(history){
                        historyAlarmList.add(alarm);
                        historyAlarmIdList.add(alarm.getId());
                        //保存设备信息
                        deviceIdList.add(alarm.getDeviceId());
                        List<Alarm> id_alarmList = deviceId_alarmListMap.get(alarm.getDeviceId());
                        if(null == id_alarmList){
                            id_alarmList = new ArrayList<>();
                        }
                        id_alarmList.add(alarm);
                        deviceId_alarmListMap.put(alarm.getDeviceId(), id_alarmList);
                    }
                }

            }

            //liuddtodo 从第三方获取设备信息并填充到告警对象中。将结果转换成json对象列表
            CIRequestEntity requestEntity = new CIRequestEntity();
            requestEntity.setIds(deviceIdList);
            MsgResult msgResult = mqttSender.sendToMqttSyn(assetsServer, getCI, JSONObject.toJSONString(requestEntity));
            int stateCode = msgResult.getStateCode();
            String msg = msgResult.getMsg();
            List<JSONObject> jsonObjectList = JSONArray.parseArray(msg, JSONObject.class);
            //假设返回
            Map<String, JSONObject> deviceId_jsonObjMap = new HashMap<>();
            for(JSONObject jsonObject : jsonObjectList){
                deviceId_jsonObjMap.put(jsonObject.getString("id"), jsonObject);
            }
            for(Alarm alarm : historyAlarmList){
                JSONObject jsonObject = deviceId_jsonObjMap.get(alarm.getDeviceId());
                if(null != jsonObject){
                    alarm.setDeviceInfos((Map)jsonObject);
                }
            }

            //删除实时告警
            alarmDao.deleteByIdList(currentTable, historyAlarmIdList);

            //保存历史告警到对应的历史告警表
            alarmDao.addList(historyAlarmList, historyTable);

            //清空列表，复位计数
            currentAlarmList.clear();
            currentTime = 0;
        }
    }
}
