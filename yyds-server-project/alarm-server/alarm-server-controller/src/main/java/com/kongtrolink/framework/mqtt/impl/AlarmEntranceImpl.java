package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.EnumLevelName;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.config.ReportOperateConfig;
import com.kongtrolink.framework.config.ResloverOperateConfig;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.dao.AuxilaryDao;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.Auxilary;
import com.kongtrolink.framework.mqtt.OperateEntity;
import com.kongtrolink.framework.mqtt.AlarmEntrance;
import com.kongtrolink.framework.mqtt.AlarmMqttEntity;
import com.kongtrolink.framework.service.MqttSender;
import com.kongtrolink.framework.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Auther: liudd
 * @Date: 2019/10/12 15:11
 * @Description:
 */
@Service
public class AlarmEntranceImpl implements AlarmEntrance {

    private String currentAlarmTable = MongTable.ALARM_CURRENT;
    private String historyAlarmTable = MongTable.ALARM_HISTORY;
    private Map<String, Alarm> keyAlarmMap = new HashMap<>();       //实时告警内存map
    private static ConcurrentLinkedQueue<JSONObject> recoverAndAuxilaryAlarmQueue = new ConcurrentLinkedQueue<>();
    @Resource(name = "controllerExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    AlarmDao alarmDao;
    @Autowired
    AuxilaryDao auxilaryDao;
    @Autowired
    ReportOperateConfig reportOperateConfig;
    @Autowired
    ResloverOperateConfig resloverOperateConfig;
    @Autowired
    MqttSender mqttSender;
    //redis存储待消除的告警和所在表信息
    @Autowired
    RedisUtils redisUtils;
    @Value("${spring.redis.pendingAlarm:pendingAlarm}")
    private String pendingAlarm;
    private static final Logger logger = LoggerFactory.getLogger(AlarmEntranceImpl.class);
    private AtomicLong countLong = new AtomicLong(0);
    /**
     * @auther: liudd
     * @date: 2019/11/11 16:15
     * 功能描述:修改实时告警队列1-加入；0-获取; 2-修改
     */
    public synchronized List<Alarm> handleCurAlarmList(Alarm alarm, String type){
        if(Contant.ZERO.equals(type)){
            List<Alarm> toSaveAlarmList = new ArrayList<>();
            toSaveAlarmList.addAll(keyAlarmMap.values());
            keyAlarmMap.clear();
            return toSaveAlarmList;
        }else if(Contant.ONE.equals(type)){
            logger.info("add alarm:{}", alarm.getKey());
            keyAlarmMap.put(alarm.getKey(), alarm);
            return null;
        }else{
            Alarm sourceAlarm = keyAlarmMap.get(alarm.getKey());
            logger.info("get alarm by key :{}, sourceAlarm:{}", alarm.getKey(), sourceAlarm);
            if(null != sourceAlarm){
                return Arrays.asList(sourceAlarm);
            }
            return null;
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/10/14 8:50
     * 功能描述:告警上报，如果已存在未消除告警，则跳过不处理
     * 1,实时告警表
     * 2，历史告警表
     *  告警消除和属性修改，可能需要使用队列来不断循环处理
     */
    @Override
    public void alarmHandle(String payload) {
        long count = countLong.incrementAndGet();
        System.out.println("count:" + count);
        logger.info("alarmHandle - payload:{}", payload);
        AlarmMqttEntity mqttEntity = JSONObject.parseObject(payload, AlarmMqttEntity.class);
        String enterpriseCode = mqttEntity.getEnterpriseCode();
        String serverCode = mqttEntity.getServerCode();
        List<JSONObject> alarmJsonList = mqttEntity.getAlarms();
        if(null == alarmJsonList || alarmJsonList.size() == 0){
            System.out.printf("产生告警为空: %s %n ", payload);
            return ;
        }
        List<Alarm> reportAlarmList = new ArrayList<>();
        //需要存储到redis中告警表
        Map<String, JSONObject> pendingAlarmMap = new HashMap<>();
        Date curDate = new Date();
        String enterServerCode = enterpriseCode + Contant.UNDERLINE + serverCode;
        for(JSONObject jsonObject : alarmJsonList) {
            String flag = jsonObject.getString("flag");
            if(Contant.ONE.equals(flag)){   //告警上报
                Alarm alarm = JSONObject.parseObject(jsonObject.toJSONString(), Alarm.class);
                alarm.setEnterpriseCode(enterpriseCode);
                alarm.setServerCode(serverCode);
                alarm.initKey();
                if(!existAlarm(alarm)){ //判定告警是否存在
                    alarm.setTreport(curDate);
                    reportAlarmList.add(alarm);
                    //将实时告警保存到内存map中
                    handleCurAlarmList(alarm, Contant.ONE);
                    //保存告警部分信息，用于告警消除需要
                    JSONObject redisJson = new JSONObject();
                    redisJson.put("flag", flag);
                    redisJson.put("treport", alarm.getTreport());
                    redisJson.put("targetLevel", alarm.getTargetLevel());
                    pendingAlarmMap.put(pendingAlarm+Contant.COLON+alarm.getKey(), redisJson);
                }
            }else {
                jsonObject.put("enterServerCode", enterServerCode);
                jsonObject.put("enterpriseCode", enterpriseCode);
                jsonObject.put("serverCode", serverCode);
                recoverAndAuxilaryAlarmQueue.add(jsonObject);
                taskExecutor.execute(()->handleRecoverAndAuxilary());
            }
        }
        if(reportAlarmList.size() != 0){
            saveCurAlarmList(enterServerCode, reportAlarmList, alarmJsonList);
            //实时告警不分表
            redisUtils.mset(pendingAlarmMap);
        }

        return ;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/21 9:39
     * 功能描述:告警产生
     * 1，判断实时告警表和历史告警表是否存在该告警
     * --如果存在，作为重复告警不予处理
     * --如果不存在，继续其他流程
     */
    private boolean existAlarm(Alarm alarm){
        boolean exist ;
        //20191111先从map中判定
        List<Alarm> alarms = handleCurAlarmList(alarm, Contant.TWO);
        if(null != alarms){
            logger.info("map中告警已存在:{}", alarm);
            return true;
        }
        //20191107从redis中判断告警是否存在;key:
        exist = redisUtils.hasKey(alarm.getKey());
        if (exist) {
            logger.info("redis中告警已存在:{}", alarm);
            return exist;
        }
        //设置默认告警等级和颜色
        String alarmLevelName = EnumLevelName.getNameByLevel(alarm.getLevel());
        alarm.setTargetLevel(alarm.getLevel());
        alarm.setTargetLevelName(alarmLevelName);
        alarm.setColor(Contant.COLOR_BLACK);
        alarm.setState(Contant.PENDING);
        return exist;
    }

    /**
     * @auther: liudd
     * @date: 2019/11/11 17:01
     * 功能描述:处理实时告警
     */
    private void saveCurAlarmList(String enterServerCode, List<Alarm> reportAlarmList, List<JSONObject> alarmJsonList){
        Map<String, List<OperateEntity>> enterServeOperaListMap = reportOperateConfig.getEnterServeOperaListMap();
        List<OperateEntity> operateEntityList = enterServeOperaListMap.get(enterServerCode);
        if(null != operateEntityList){
            String reportAlarmListJson = JSONObject.toJSONString(reportAlarmList);
            for(OperateEntity operateEntity : operateEntityList){
                String serverVerson = operateEntity.getServerVerson();
                String operaCode = operateEntity.getOperaCode();
                try {
                    //所有其他模块，都返回告警列表json字符串
                    MsgResult msgResult = mqttSender.sendToMqttSyn(serverVerson, operaCode, reportAlarmListJson);
                    //打印请求相关信息
                    logger.info("---report : msg:{}, operate:{}, result:{}", JSONObject.toJSON(alarmJsonList), operateEntity, msgResult);
                    int stateCode = msgResult.getStateCode();
                    if(1 == stateCode){
                        reportAlarmListJson = msgResult.getMsg();
                    }
                }catch (Exception e){
                    //打印调用失败消息
                    logger.info("---report: remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSON(alarmJsonList), operateEntity, e.getMessage());
                    continue;
                }
            }
        }
    }


    /**
     * @auther: liudd
     * @date: 2019/10/21 11:27
     * 功能描述:消除告警或者修改告警属性
     */
    private void  handleRecoverAndAuxilary() {
        if(recoverAndAuxilaryAlarmQueue.size() == 0){
            return ;
        }
        JSONObject jsonObject = recoverAndAuxilaryAlarmQueue.poll();
        String flag = jsonObject.getString("flag");
        if(Contant.ZERO.equals(flag)) {
            Alarm alarm = JSONObject.parseObject(jsonObject.toJSONString(), Alarm.class);
            String enterServerCode = jsonObject.getString("enterServerCode");
            String enterpriseCode = alarm.getEnterpriseCode();
            String serverCode = alarm.getServerCode();
            alarm.initKey();

            String redisKey = pendingAlarm+Contant.COLON+alarm.getKey();
            JSONObject redisJson = (JSONObject)redisUtils.get(redisKey);
            if(null == redisJson){
                //redis待处理列表中没有该告警，说明该告警已经消除
                return ;
            }
            boolean result ;
            //判断内存中是否存在
            List<Alarm> mapAlarms = handleCurAlarmList(alarm, Contant.TWO);
            if(null != mapAlarms){
                Alarm mapAlarm = mapAlarms.get(0);
                mapAlarm.setTrecover(new Date());
                mapAlarm.setState(Contant.RESOLVE);
                logger.info("resolve alarm in map, key:{}, alarm:{}", alarm.getKey(), alarm);
                result = true;
            }else if(Contant.ONE.equals(redisJson.getString("flag"))) {
                result = alarmDao.resolveByKey(alarm.getKey(), Contant.RESOLVE, new Date(), currentAlarmTable);
            }else{//从历史表中删除
                result = alarmDao.resolveByKey(alarm.getKey(), Contant.RESOLVE, new Date(),
                        enterpriseCode + Contant.UNDERLINE + serverCode + Contant.UNDERLINE + historyAlarmTable);
            }
            if(result){
                //liuddtodo 调用告警消除发送推送
                redisUtils.del(redisKey);
                alarm.setTrecover(new Date());
                alarm.setTreport(redisJson.getDate("treport"));
                alarm.setTargetLevel(redisJson.getInteger("targetLevel"));
                Map<String, List<OperateEntity>> enterServeOperaListMap = resloverOperateConfig.getEnterServeOperaListMap();
                List<OperateEntity> operateEntityList = enterServeOperaListMap.get(enterServerCode);
//                System.out.println("operateEntityList:" + operateEntityList);
                String resolveAlarmListJson = JSONObject.toJSONString(Arrays.asList(alarm));
                if(null != operateEntityList){
                    for(OperateEntity operateEntity : operateEntityList){
                        String serverVerson = operateEntity.getServerVerson();
                        String operaCode = operateEntity.getOperaCode();
                        try {
                            //所有其他模块，都返回告警列表json字符串
                            MsgResult msgResult = mqttSender.sendToMqttSyn(serverVerson, operaCode, resolveAlarmListJson);
                            logger.info("***resolve : msg:{}, operate:{}, result:{}", JSONObject.toJSON(resolveAlarmListJson), operateEntity, msgResult);
                            resolveAlarmListJson = msgResult.getMsg();
                        }catch (Exception e){
                            //打印调用失败消息
                            logger.info("***resolve: remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSON(resolveAlarmListJson), operateEntity, e.getMessage());
                            continue;
                        }
                    }
                }
            }
        }else{
            handleAuxilary(jsonObject);
        }
        handleRecoverAndAuxilary();
    }
    private void handleAuxilary(JSONObject jsonObject){
        //name, value,level,flag, deviceId,deviceType,deviceModel,signalId,serial
        String enterpriseCode = jsonObject.getString(Contant.ENTERPRISECODE);
        String serverCode = jsonObject.getString(Contant.SERVERCODE);
        Auxilary auxilary = auxilaryDao.getByEnterServerCode(enterpriseCode, serverCode);
        if(null != auxilary){
            String deviceType = jsonObject.getString(Contant.DEVICETYPE);
            String deviceModel = jsonObject.getString(Contant.DEVICEMODEL);
            String deviceId = jsonObject.getString(Contant.DEVICEID);
            String signalId = jsonObject.getString(Contant.SIGNALID);
            String serial = jsonObject.getString(Contant.SERIAL);
            Set<String> keys = jsonObject.keySet();
            if(null != keys && keys.size()>0) {
                List<String> proStrList = auxilary.getProStrList();
                Map<String, String> updateMap = new HashMap<>();
                for(String key : keys){
                    if(proStrList.contains(key)){
                        updateMap.put(key, jsonObject.getString(key));
                    }
                }
                if(updateMap.size()>0){
                    boolean result = alarmDao.updateAuxilary(deviceType, deviceModel, deviceId, signalId,
                            serial, updateMap, currentAlarmTable);
                    if(!result){
                        alarmDao.updateAuxilary(deviceType, deviceModel, deviceId, signalId,
                                serial, updateMap, enterpriseCode + Contant.UNDERLINE + serverCode + Contant.UNDERLINE + historyAlarmTable);
                    }
                }
            }

        }
        logger.info("修改告警属性: {}", jsonObject.toJSONString());
    }
}
