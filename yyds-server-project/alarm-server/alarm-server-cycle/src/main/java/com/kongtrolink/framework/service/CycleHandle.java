package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.dao.AlarmCycleDao;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.mqtt.CIRequestEntity;
import com.kongtrolink.framework.mqtt.CIResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
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

    private static int currentTime = 0;
    private static final Logger logger = LoggerFactory.getLogger(CycleHandle.class);
    private static List<Alarm> currentAlarmList = new ArrayList<>();
    private String currentTable = MongTable.ALARM_CURRENT;
    private String historyTable = MongTable.ALARM_HISTORY;

    /**
     * @auther: liudd
     * @date: 2019/10/21 18:18
     * 功能描述:静态方法同步锁作用在当前类的字节码上
     */
    public static synchronized void handleCurrentAlarmList(List<Alarm> alarmList, String type){
        if(Contant.ONE.equals(type)){
            currentAlarmList.addAll(alarmList);
        }else if(Contant.ZERO.equals(type)){
            currentAlarmList.clear();
            //清空列表，复位计数
            currentTime = 0;
        }
    }

    public static synchronized int getCurrentAlarmSize(){
        return currentAlarmList.size();
    }

    public void handle(){
        ScheduledExecutorService handleScheduler = Executors.newSingleThreadScheduledExecutor();
        handleScheduler.scheduleWithFixedDelay(new handleTask(), 10 * 1000, 10 * 1000,
                TimeUnit.MILLISECONDS);
    }

    public synchronized List<Alarm> beforHandle(){
        if(currentAlarmList.size() < count && currentTime < time){
            currentTime ++ ;
            return null;
        }
        List<Alarm> handleAlarmList = new ArrayList<>();
        handleAlarmList.addAll(currentAlarmList);
        handleCurrentAlarmList(null, Contant.ZERO);
        return handleAlarmList;
    }

    class handleTask implements Runnable{
        public void run() {
            List<Alarm> handleAlarmList = beforHandle();
            if(null == handleAlarmList || handleAlarmList.size() == 0){
                return ;
            }
            logger.info("handleAlarmList.size:{}", handleAlarmList.size());

            //初始化备用map
            Map<String, AlarmCycle> enterpriseServer_cycleMap = new HashMap<>();
            Map<String, List<Alarm>> enterpirseServer_alarmListMap = readyMap(handleAlarmList, enterpriseServer_cycleMap);

            //判定各个告警是否过期
            //liuddtodo 需要考虑多个服务同时修改情况，后期再处理
            Map<String, List<Alarm>> enterServerHistoryAlarmListMap = new HashMap<>();
            List<String> historyAlarmIdList = new ArrayList<>();    // 历史告警id列表
            List<String> currentAlarmIdList = new ArrayList<>();    //实时告警id列表
            List<String> deviceIdList = new ArrayList<>();          // 设备id列表
            Map<String, List<Alarm>> deviceId_alarmListMap = new HashMap<>();   //设备id—历史告警列表map
            Date curTime = new Date();
            for(String enterpirseServer : enterpirseServer_alarmListMap.keySet()){
                AlarmCycle alarmCycle = enterpriseServer_cycleMap.get(enterpirseServer);
                List<Alarm> alarmList = enterpirseServer_alarmListMap.get(enterpirseServer);
                for(Alarm alarm : alarmList){
                    boolean history = AlarmCycle.isHistory(alarmCycle, alarm, curTime);
                    if(history){
                        historyAlarmIdList.add(alarm.getId());
                        //保存设备信息
                        deviceIdList.add(alarm.getDeviceId());

                        List<Alarm> alarms = enterServerHistoryAlarmListMap.get(enterpirseServer);
                        if(null == alarm){
                            alarms = new ArrayList<>();
                        }
                        alarms.add(alarm);
                        enterServerHistoryAlarmListMap.put(enterpirseServer, alarms);

                        List<Alarm> deviceId_alarmList = deviceId_alarmListMap.get(alarm.getDeviceId());
                        if(null == deviceId_alarmList){
                            deviceId_alarmList = new ArrayList<>();
                        }
                        deviceId_alarmList.add(alarm);
                        deviceId_alarmListMap.put(alarm.getDeviceId(), deviceId_alarmList);
                    }else{
                        currentAlarmIdList.add(alarm.getId());
                    }
                }
            }

            //从资产管理获取设备信息并填充到告警对象
            initDeviceInfo(deviceIdList, enterServerHistoryAlarmListMap);

            //删除实时告警
            alarmDao.deleteByIdList(currentTable, historyAlarmIdList);

            //保存历史告警到对应的历史告警表
            for(String enterServer : enterServerHistoryAlarmListMap.keySet()) {
                List<Alarm> historyAlarmList = enterServerHistoryAlarmListMap.get(enterServer);
                alarmDao.addList(historyAlarmList, enterServer + Contant.UNDERLINE+ historyTable);
            }
            //修改实时告警中hc字段值，以便下次再进入告警周期
            alarmDao.updateHC(currentAlarmIdList, false, currentTable);
        }
    }

    public Map<String, List<Alarm>> readyMap(List<Alarm> handleAlarmList, Map<String, AlarmCycle> enterpriseServer_cycleMap){
        Map<String, List<Alarm>> enterpirseServer_alarmListMap = new HashMap<>();
        List<String> enterpriseServerCodeList = new ArrayList<>();
        for(Alarm alarm : handleAlarmList){
            String enterpriseServer = alarm.getEnterpriseServer();
            enterpriseServerCodeList.add(enterpriseServer);
            List<Alarm> alarms = enterpirseServer_alarmListMap.get(enterpriseServer);
            if(null == alarms){
                alarms = new ArrayList<>();
            }
            alarms.add(alarm);
            //根据企业，服务将告警分类
            enterpirseServer_alarmListMap.put(enterpriseServer, alarms);
        }
        //获取企业对应的自定义周期
        List<AlarmCycle> cycleList = alarmCycleDao.getCycleList(enterpriseServerCodeList);
        for(AlarmCycle alarmCycle : cycleList){
            alarmCycle.initEnterpirseServer();
            enterpriseServer_cycleMap.put(alarmCycle.getEnterpriseServer(), alarmCycle);
        }
        return enterpirseServer_alarmListMap;
    }

    public void initDeviceInfo(List<String> deviceIdList, Map<String, List<Alarm>> enterServerHistoryAlarmListMap){
        //liuddtodo 从第三方获取设备信息并填充到告警对象中。将结果转换成json对象列表
        CIRequestEntity requestEntity = new CIRequestEntity();
        requestEntity.setIds(deviceIdList);
        MsgResult msgResult = mqttSender.sendToMqttSyn(assetsServer, getCI, JSONObject.toJSONString(requestEntity));
        //liuddtodo 需要判定返回失败的结果
        int stateCode = msgResult.getStateCode();
        String msg = msgResult.getMsg();
        CIResponseEntity ciResponseEntity = JSONObject.parseObject(msg, CIResponseEntity.class);
        int count = ciResponseEntity.getCount();
        //假设返回
        Map<String, JSONObject> deviceId_jsonObjMap = new HashMap<>();
        for (JSONObject jsonObject : ciResponseEntity.getInfos()) {
            deviceId_jsonObjMap.put(jsonObject.getString("sn"), jsonObject);
        }

        //根据diviceid，填充告警信息
        for (String enterServer : enterServerHistoryAlarmListMap.keySet()) {
            List<Alarm> historyAlarmList = enterServerHistoryAlarmListMap.get(enterServer);
            for (Alarm alarm : historyAlarmList) {
                JSONObject jsonObject = deviceId_jsonObjMap.get(alarm.getDeviceId());
                if (null != jsonObject) {
                    alarm.setDeviceInfos((Map) jsonObject);
                }
            }
        }
    }
}
