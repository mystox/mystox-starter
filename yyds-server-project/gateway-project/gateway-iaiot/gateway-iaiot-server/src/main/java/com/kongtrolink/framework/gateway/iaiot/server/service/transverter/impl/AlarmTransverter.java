package com.kongtrolink.framework.gateway.iaiot.server.service.transverter.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.OperaCode;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.gateway.iaiot.server.entity.DeviceConfigEntity;
import com.kongtrolink.framework.gateway.iaiot.server.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.iaiot.server.entity.Transverter;
import com.kongtrolink.framework.gateway.iaiot.server.service.transverter.TransverterHandler;
import com.kongtrolink.framework.gateway.iaiot.server.service.DeviceTypeConfig;
import com.kongtrolink.framework.gateway.iaiot.core.alarm.AlarmReport;
import com.kongtrolink.framework.gateway.iaiot.core.alarm.AlarmReportInfo;
import com.kongtrolink.framework.gateway.iaiot.core.rec.PushAlarm;
import com.kongtrolink.framework.gateway.iaiot.core.rec.info.PushAlarmInfo;
import com.kongtrolink.framework.gateway.iaiot.core.rec.info.PushAlarmList;
import com.kongtrolink.framework.gateway.iaiot.core.rec.info.PushDeviceAssetDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private String tallyTime = "";//计数
    private int tallyNum = 0;//计数
    private int total = 0;
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
                DeviceConfigEntity deviceConfigEntity = deviceTypeConfig.getAssentDeviceType(deviceInfo.getType());
                if(deviceConfigEntity!=null){
                    alarmInfo.setDeviceType(deviceConfigEntity.getAssentType());
                }
                alarmInfo.setDeviceModel(deviceInfo.getModel());
                alarmInfo.setSignalId(pushAlarmInfo.getId());
                alarmInfo.setSerial(String.valueOf(pushAlarmInfo.getSerialNo()));
                alarmInfoList.add(alarmInfo);
            }
            AlarmReport report = new AlarmReport();
            report.setEnterpriseCode(getEnterpriseCode());
            report.setServerCode(getBusinessCode());
            report.setAlarms(alarmInfoList);
            logger.debug("上报告警的 数据: {} " ,JSONObject.toJSONString(report));
            String jsonResult = JSONObject.toJSONString(report);
            reportMsg(MqttUtils.preconditionServerCode(ServerName.ALARM_SERVER_CONTROLLER,alarmServerVersion),
                    OperaCode.ALARM_REPORT,jsonResult);
            //统计计数
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            if(!time.equals(tallyTime)){
                logger.debug("----------------->  统计告警数量时间: {}  数量: {} 合计:{} " ,tallyTime,tallyNum+1,total);
                tallyTime = time;
                tallyNum = 1;
            }else{
                tallyNum += 1;
            }
            total += 1;

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
