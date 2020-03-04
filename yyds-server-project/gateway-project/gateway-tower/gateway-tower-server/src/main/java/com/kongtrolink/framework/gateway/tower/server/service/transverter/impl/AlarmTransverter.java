package com.kongtrolink.framework.gateway.tower.server.service.transverter.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.OperaCode;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.gateway.iaiot.core.alarm.AlarmReport;
import com.kongtrolink.framework.gateway.iaiot.core.alarm.AlarmReportInfo;
import com.kongtrolink.framework.gateway.tower.core.constant.RedisKey;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.SendAlarm;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.TAlarm;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.XmlList;
import com.kongtrolink.framework.gateway.tower.core.util.GatewayTowerUtil;
import com.kongtrolink.framework.gateway.tower.core.util.MessageUtil;
import com.kongtrolink.framework.gateway.tower.core.util.RedisKeyUtil;
import com.kongtrolink.framework.gateway.tower.server.entity.DeviceConfigEntity;
import com.kongtrolink.framework.gateway.tower.server.entity.Transverter;
import com.kongtrolink.framework.gateway.tower.server.service.DeviceTypeConfig;
import com.kongtrolink.framework.gateway.tower.server.service.transverter.TransverterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by mystoxlol on 2019/10/16, 15:27.
 * company: kongtrolink
 * description: 告警转换器
 * update record:
 */
@Service
public class AlarmTransverter extends TransverterHandler {

    private static final Logger logger = LoggerFactory.getLogger(AlarmTransverter.class);

    @Value("${gateway.alarmReport.version:V1.0.0}")
    private String alarmServerVersion;
    @Autowired
    private DeviceTypeConfig deviceTypeConfig;
    @Autowired
    RedisUtils redisUtils;

    public void transferExecute(String payload){
        try{
            SendAlarm sendAlarm = (SendAlarm) MessageUtil.stringToMessage(payload, SendAlarm.class);
            XmlList alarmListList = sendAlarm.getValue().getAlarmListList().get(0);
            List<TAlarm> tAlarmLists = alarmListList.gettAlarmList();
            if(tAlarmLists==null || tAlarmLists.size() ==0){
                logger.info("无告警实体判断 payLoad:{} ",payload);
                return;
            }
            List<AlarmReportInfo> alarmInfoList = new ArrayList<>();
            for(TAlarm pushAlarmInfo:tAlarmLists){
                String deviceSn = pushAlarmInfo.getFsuId()+"_"+pushAlarmInfo.getDeviceId();
                AlarmReportInfo alarmInfo = new AlarmReportInfo();
                String desc = pushAlarmInfo.getAlarmDesc();
                alarmInfo.setName(desc);
                try{
                    //格式   温度(001号)过高告警(100℃)
                    String[] descList = desc.split("\\(");
                    String value = descList[descList.length-1];
                    alarmInfo.setValue(value.replace(")",""));
                    alarmInfo.setName(descList[0]);
                }catch (Exception e){

                }
                alarmInfo.setLevel(String.valueOf(pushAlarmInfo.getAlarmLevel()));
                //告警标志（0：结束，1：开始
                int flag = 1;
                if("结束".equals(pushAlarmInfo.getAlarmFlag())||"END".equals(pushAlarmInfo.getAlarmFlag())){
                    flag = 0;
                }
                alarmInfo.setFlag(flag);
                alarmInfo.setDeviceId(deviceSn);
                String deviceType = GatewayTowerUtil.getDeviceTypeFromId(pushAlarmInfo.getDeviceId());
                DeviceConfigEntity deviceConfigEntity = deviceTypeConfig.getAssentDeviceType(deviceType);
                if(deviceConfigEntity!=null){
                    deviceType = deviceConfigEntity.getAssentType();
                }
                alarmInfo.setDeviceType(deviceType);
                alarmInfo.setDeviceModel(deviceType);
                alarmInfo.setSignalId(pushAlarmInfo.getId());
                alarmInfo.setSerial(String.valueOf(pushAlarmInfo.getSerialNo()));
                alarmInfoList.add(alarmInfo);
            }
            sendMessage(alarmInfoList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void offlineAlarmEnd(String sn){
        String uniqueCode = getEnterpriseCode();
        Object offlineAlarmSerialNo = redisUtils.hget(RedisKeyUtil.getRedisKey(uniqueCode,RedisKey.FSU_ALARM_INFO),sn);
        if(offlineAlarmSerialNo !=null){
            //删除离线告警
            redisUtils.hdel(RedisKeyUtil.getRedisKey(uniqueCode,RedisKey.FSU_ALARM_INFO),sn);
            List<AlarmReportInfo> alarmInfoList = new ArrayList<>();
            AlarmReportInfo alarmInfo = new AlarmReportInfo();
            alarmInfo.setName(getFsuOffAlarmName());
            alarmInfo.setValue("1");
            alarmInfo.setLevel(getFsuOffAlarmLevel());
            alarmInfo.setFlag(0);
            alarmInfo.setDeviceId(sn);
            String deviceType = getFsuType();
            DeviceConfigEntity deviceConfigEntity = deviceTypeConfig.getAssentDeviceType(deviceType);
            if(deviceConfigEntity!=null){
                deviceType = deviceConfigEntity.getAssentType();
            }
            alarmInfo.setDeviceType(deviceType);
            alarmInfo.setDeviceModel(deviceType);
            alarmInfo.setSignalId("");
            alarmInfo.setSerial(String.valueOf(offlineAlarmSerialNo));
            alarmInfoList.add(alarmInfo);
            sendMessage(alarmInfoList);
        }
    }


    public void offlineAlarmStart(String sn){
        String uniqueCode = getEnterpriseCode();
        logger.error("收到fsuId：{} 离线告警   .... ",sn);
        Object offlineAlarmSerialNo = redisUtils.hget(RedisKeyUtil.getRedisKey(uniqueCode,RedisKey.FSU_ALARM_INFO),sn);
        if(offlineAlarmSerialNo !=null){
            logger.error("离线告警已存在");
            return;
        }
        long serial = System.currentTimeMillis();
        List<AlarmReportInfo> alarmInfoList = new ArrayList<>();
        AlarmReportInfo alarmInfo = new AlarmReportInfo();
        alarmInfo.setName(getFsuOffAlarmName());
        alarmInfo.setValue("1");
        alarmInfo.setLevel(getFsuOffAlarmLevel());
        alarmInfo.setFlag(1);//1开始 0 结束
        alarmInfo.setDeviceId(sn);
        String deviceType = getFsuType();
        DeviceConfigEntity deviceConfigEntity = deviceTypeConfig.getAssentDeviceType(deviceType);
        if(deviceConfigEntity!=null){
            deviceType = deviceConfigEntity.getAssentType();
        }
        alarmInfo.setDeviceType(deviceType);
        alarmInfo.setDeviceModel(deviceType);
        alarmInfo.setSignalId("");
        alarmInfo.setSerial(String.valueOf(serial));
        alarmInfoList.add(alarmInfo);
        //保存在redis中
        redisUtils.hset(RedisKeyUtil.getRedisKey(uniqueCode,RedisKey.FSU_ALARM_INFO),sn,String.valueOf(serial));
        //推送给告警模块
        sendMessage(alarmInfoList);

    }

    private void sendMessage(List<AlarmReportInfo> alarmInfoList){
        AlarmReport report = new AlarmReport();
        report.setEnterpriseCode(getEnterpriseCode());
        report.setServerCode(getBusinessCode());
        report.setAlarms(alarmInfoList);
        logger.debug("上报告警的 数据: {} " ,JSONObject.toJSONString(report));
        String jsonResult = JSONObject.toJSONString(report);
        reportMsg(MqttUtils.preconditionServerCode(ServerName.ALARM_SERVER_CONTROLLER,alarmServerVersion),
                OperaCode.ALARM_REPORT,jsonResult);
    }
}
