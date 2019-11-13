package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.dao.AlarmCycleDao;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.enttiy.InformMsg;
import com.kongtrolink.framework.mqtt.CIResponseEntity;
import com.kongtrolink.framework.util.RedisUtils;
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
    @Autowired
    RedisUtils redisUtils;

    @Value("${cycle.count:300}")
    private int count;
    @Value("${cycle.time:5}")
    private int time;
    @Value("${assets.serverVersion:theAssets}")
    private String assetsServer;
    @Value("${assets.operaCode:getCI}")
    private String getCI;
    @Value("${hcOverTime: 5}")
    private int hcOverTime;
    @Value("${spring.redis.pendingAlarm}")
    private String pendingAlarm;

    private static int currentTime = 0;

    private static final Logger logger = LoggerFactory.getLogger(CycleHandle.class);
    private static List<Alarm> currentAlarmList = new ArrayList<>();
    private String currentTable = MongTable.ALARM_CURRENT;
    private String historyTable = Contant.UNDERLINE + MongTable.ALARM_HISTORY + Contant.UNDERLINE;

    /**
     * @auther: liudd
     * @date: 2019/10/21 18:18
     * 功能描述:静态方法同步锁作用在当前类的字节码上
     */
    public synchronized List<Alarm> handleCurrentAlarmList(String type){
        if(Contant.ONE.equals(type)){
            //从数据库获取实时告警，并加入到当前队列
            int size = currentAlarmList.size();
            if(size<count) {
                Date curTime = new Date();
                int diff = count - size;
                List<Alarm> alarmList = alarmDao.getCurrentAlarmList(currentTable, diff, curTime);
                //去除重复告警
                alarmList.removeAll(currentAlarmList);
                if(alarmList.size() == 0){
                    return null;
                }
                //修改获取到的实时告警属性，防止再次获取到
                List<String> idList = alarmDao.entity2IdList(alarmList);
                long overTimeLong = curTime.getTime() + (hcOverTime * 60 * 1000);
                alarmDao.updateHcTime(idList, new Date(overTimeLong), currentTable);
                currentAlarmList.addAll(alarmList);
                logger.info("size:{}, diff:{}, alarmList.size:{}, currentAlarmList.size:{}", size, diff, alarmList.size(), currentAlarmList.size());
            }
            return null;
        }else{
            if(currentAlarmList.size() < count && currentTime < time){
                currentTime ++ ;
                return null;
            }
            List<Alarm> handleAlarmList = new ArrayList<>();
            handleAlarmList.addAll(currentAlarmList);
            currentAlarmList.clear();
            //清空列表，复位计数
            currentTime = 0;
            logger.info("handleAlarmList.currentTime:{}, currentAlarmList.size:{}", handleAlarmList.size(), currentTime, currentAlarmList.size());
            return handleAlarmList;
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/11/5 18:32
     * 功能描述:处理实时告警
     */
    public void handle(){
        List<Alarm> handleAlarmList = handleCurrentAlarmList(Contant.ZERO);
        if(null == handleAlarmList || handleAlarmList.size() == 0){
            return ;
        }
        logger.info("handleAlarmList.size:{}", handleAlarmList.size());
        //初始化备用map
        Map<String, AlarmCycle> enterpriseServer_cycleMap = new HashMap<>();
        Map<String, List<Alarm>> enterpirseServer_alarmListMap = readyMap(handleAlarmList, enterpriseServer_cycleMap);

        Map<String, List<Alarm>> tableHistoryAlarmListMap = new HashMap<>();    //表名-历史告警列表map
        List<String> historyAlarmIdList = new ArrayList<>();    // 历史告警id列表
        List<String> currentAlarmIdList = new ArrayList<>();    //实时告警id列表
        List<String> deviceIdList = new ArrayList<>();          // 设备id列表
        Map<String, List<Alarm>> enterServerDeviceId_alarmListMap = new HashMap<>();   //enterServerDeviceId—历史告警列表map
        Map<String, JSONObject> redisAlarmMap = new HashMap<>();
        Date curTime = new Date();
        for(String enterpirseServer : enterpirseServer_alarmListMap.keySet()){
            AlarmCycle alarmCycle = getAlarmCycle(enterpriseServer_cycleMap, enterpirseServer);
            if(null == alarmCycle){
                logger.info("{} alarmCycle is null!", enterpirseServer);
                return ;
            }
            List<Alarm> alarmList = enterpirseServer_alarmListMap.get(enterpirseServer);
            for(Alarm alarm : alarmList){
                boolean history = AlarmCycle.isHistory(alarmCycle, alarm, curTime);
                if(history){
                    //以enterpriseCode_serverCode为键，保存历史告警列表，为后面批量存储做准备
                    String table = alarm.getHistoryTable();
                    List<Alarm> tableHistoryAlarmList = tableHistoryAlarmListMap.get(table);
                    if(null == tableHistoryAlarmList){
                        tableHistoryAlarmList = new ArrayList<>();
                    }
                    tableHistoryAlarmList.add(alarm);
                    tableHistoryAlarmListMap.put(table, tableHistoryAlarmList);

                    historyAlarmIdList.add(alarm.getId());
                    //修改redis中所在表
                    String redisKey = pendingAlarm + Contant.COLON + alarm.getKey();
                    JSONObject redisJson = (JSONObject)redisUtils.get(redisKey);
                    if(null != redisJson){
                        redisJson.put("flag", Contant.ZERO);
                        Alarm byKey = alarmDao.getByKey(alarm.getKey(), currentTable);
                        if(null != byKey){
                            redisJson.put("treport", alarm.getTreport());
                        }
                        redisAlarmMap.put(redisKey, redisJson);
                    }
                    //保存设备id信息
                    String deviceId = alarm.getDeviceId();
                    if(!deviceIdList.contains(deviceId)) {
                        deviceIdList.add(deviceId);
                    }

                    //以enterServerDeviceId为键，保存历史告警列表，为后期填充设备信息做准备
                    String key = enterpirseServer + Contant.UNDERLINE + deviceId;
                    List<Alarm> enterServerDeviceId_alarmList = enterServerDeviceId_alarmListMap.get(key);
                    if(enterServerDeviceId_alarmList == null){
                        enterServerDeviceId_alarmList = new ArrayList<>();
                    }
                    enterServerDeviceId_alarmList.add(alarm);
                    enterServerDeviceId_alarmListMap.put(key, enterServerDeviceId_alarmList);
                }else{
                    currentAlarmIdList.add(alarm.getId());
                }
            }
        }

        //修改实时告警中hc字段值，以便下次再进入告警周期
        logger.info("update current alarm, size:{}", currentAlarmIdList.size());
        alarmDao.updateHcTime(currentAlarmIdList, null, currentTable);
        if(historyAlarmIdList.size() == 0){
            return ;
        }

        //从资产管理获取设备信息并填充到告警对象
        boolean result = initDeviceInfo(deviceIdList, enterServerDeviceId_alarmListMap);
        if(!result){
            logger.info("initDeviceInfo fail");
            //如果获取设备信息失败，所有实时告警重新存入实时告警表
            historyAlarmIdList.addAll(currentAlarmIdList);
            alarmDao.updateHcTime(historyAlarmIdList, null, currentTable);
            return ;
        }
        //批量修改redis上告警flag
        redisUtils.mset(redisAlarmMap);
        //保存历史告警到对应的历史告警表
        for(String tale : tableHistoryAlarmListMap.keySet()) {
            List<Alarm> historyAlarmList = tableHistoryAlarmListMap.get(tale);
            logger.info("save history alarm, enterServer:{}, size:{}", tale, historyAlarmIdList.size());
            alarmDao.addList(historyAlarmList, tale);
        }
        //删除实时告警
        logger.info("delete history alarm from current table, size:{}", historyAlarmIdList.size());
        alarmDao.deleteByIdList(currentTable, historyAlarmIdList);
    }

    /**
     * @auther: liudd
     * @date: 2019/11/5 18:39
     * 功能描述:获取enterpriseServerCode-cycle 对应map和enterpriseServerCode-List<alarm>对应map
     */
    public Map<String, List<Alarm>> readyMap(List<Alarm> handleAlarmList, Map<String, AlarmCycle> enterpriseServer_cycleMap){
        List<String> enterpriseServerCodeList = new ArrayList<>();
        Map<String, List<Alarm>> enterpirseServer_alarmListMap = new HashMap<>();
        for(Alarm alarm : handleAlarmList){
            String enterpriseServer = alarm.getEnterpriseServer();
            if(!enterpriseServerCodeList.contains(enterpriseServer)) {
                enterpriseServerCodeList.add(enterpriseServer);
            }
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
            enterpriseServer_cycleMap.put(alarmCycle.getEnterpriseServer(), alarmCycle);
        }
        return enterpirseServer_alarmListMap;
    }

    private AlarmCycle getAlarmCycle(Map<String, AlarmCycle> enterpriseServer_cycleMap, String enterpirseServer){
        AlarmCycle alarmCycle = enterpriseServer_cycleMap.get(enterpirseServer);
        if(null == alarmCycle){
            alarmCycle = enterpriseServer_cycleMap.get(Contant.SYSTEM);
        }
        return alarmCycle;
    }

    /**
     * @auther: liudd
     * @date: 2019/11/1 15:51
     * 功能描述:获取用户信息，如果获取不到，暂时先放回实时表，并修改获取次数
     */
    public boolean initDeviceInfo(List<String> deviceIdList, Map<String, List<Alarm>> enterServerDeviceId_alarmListMap){
        JSONObject resJsonObject = new JSONObject();
        resJsonObject.put("sns", deviceIdList);
        try {
            MsgResult msgResult = mqttSender.sendToMqttSyn(assetsServer, getCI, resJsonObject.toJSONString());
            logger.info("---getCI : msg:{}, operate:{}, result:{}", deviceIdList.toString(), assetsServer+Contant.UNDERLINE+getCI, msgResult);
            if(1 == msgResult.getStateCode()) {
                CIResponseEntity ciResponseEntity = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
                for (JSONObject deviceJson : ciResponseEntity.getInfos()) {
                    String enterpriseCode = deviceJson.getString("enterpriseCode");
                    String serverCode = deviceJson.getString("serverCode");
                    String sn = deviceJson.getString("sn");
                    String key = enterpriseCode+ Contant.UNDERLINE + serverCode + Contant.UNDERLINE + sn;
                    List<Alarm> enterServerDeviceId_alarmList = enterServerDeviceId_alarmListMap.get(key);
                    for(Alarm alarm : enterServerDeviceId_alarmList){
                        alarm.setDeviceInfos((Map) deviceJson);
                    }
                }
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.info("---getCI: remote call error, msgSize:{}, operate:{}, result:{}",deviceIdList.size(),
                    assetsServer+Contant.UNDERLINE+getCI, e.getMessage());
            return false;
        }
    }
}
