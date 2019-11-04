package com.kongtrolink.framework.gateway.service.transverter.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.OperaCode;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.service.DeviceTypeConfig;
import com.kongtrolink.framework.gateway.entity.Transverter;
import com.kongtrolink.framework.gateway.service.transverter.TransverterHandler;
import com.kongtrolink.framework.gateway.tower.entity.alarm.AlarmReport;
import com.kongtrolink.framework.gateway.tower.entity.alarm.AlarmReportInfo;
import com.kongtrolink.framework.gateway.tower.entity.rec.PushAlarm;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.PushAlarmInfo;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.PushAlarmList;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.PushDeviceAssetDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mystoxlol on 2019/10/16, 15:27.
 * company: kongtrolink
 * description: 告警转换器
 * update record:
 */
@Transverter("alarmTransverter")
public class AlarmTransverter extends TransverterHandler {

    private static final Logger logger = LoggerFactory.getLogger(AlarmTransverter.class);

    @Value("${gateway.alarmReport.version:V1.0.0}")
    private String alarmServerVersion;
    @Autowired
    private DeviceTypeConfig deviceTypeConfig;
    @Autowired
    RedisUtils redisUtils;

    @Override
    protected void transferExecute(ParseProtocol parseProtocol) {
        try{
            String payLoad = parseProtocol.getPayload();//
            String sn = parseProtocol.getSn();
            PushAlarm alarmReport = JSONObject.parseObject(payLoad,PushAlarm.class);
            PushAlarmList pushAlarmList = alarmReport.getPayload();
            if(pushAlarmList==null || pushAlarmList.getAlarms()==null || pushAlarmList.getAlarms().size()==0){
                logger.info("无告警实体判断 payLoad:{} ",payLoad);
                return;
            }
            List<AlarmReportInfo> alarmInfoList = new ArrayList<>();
            for(PushAlarmInfo pushAlarmInfo:pushAlarmList.getAlarms()){
                String deviceSn = sn+"_"+pushAlarmInfo.getDeviceId();
                AlarmReportInfo alarmInfo = new AlarmReportInfo();
                alarmInfo.setName(pushAlarmInfo.getDesc());
                alarmInfo.setValue(String.valueOf(pushAlarmInfo.getEventValue()));
                alarmInfo.setLevel(String.valueOf(pushAlarmInfo.getLevel()));
                alarmInfo.setFlag(pushAlarmInfo.getFlag());
                alarmInfo.setDeviceId(deviceSn);
                //根据设备ID获取设备type
                PushDeviceAssetDevice deviceInfo = deviceTypeConfig.getDeviceInfo(deviceSn);
                if(deviceInfo==null){
                    logger.info("无法根据 sn:{} 在redis中查询到相关设备信息 !!!!",deviceSn);
                    continue;
                }
                alarmInfo.setDeviceType(deviceTypeConfig.getAssentDeviceType(deviceInfo.getType()));
                alarmInfo.setDeviceModel(deviceInfo.getModel());
                alarmInfo.setSignalId(pushAlarmInfo.getId());
                alarmInfo.setSerial(String.valueOf(pushAlarmInfo.getSerialNo()));
                alarmInfoList.add(alarmInfo);
            }
            AlarmReport report = new AlarmReport();
            report.setEnterpriseCode(getEnterpriseCode());
            report.setServerCode(getBusinessCode());
            report.setAlarms(alarmInfoList);
            logger.debug("上报告警的 数据: \n");
            logger.debug(JSONObject.toJSONString(report));
            logger.debug("\n");
            String jsonResult = JSONObject.toJSONString(report);
            reportMsg(MqttUtils.preconditionServerCode(ServerName.ALARM_SERVER,alarmServerVersion),
                    OperaCode.ALARM_REPORT,jsonResult);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
