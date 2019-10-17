package com.kongtrolink.framework.gateway.service.transverter.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.OperaCode;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.service.transverter.TransverterHandler;
import com.kongtrolink.framework.gateway.tower.entity.alarm.AlarmReport;
import com.kongtrolink.framework.gateway.tower.entity.alarm.AlarmReportInfo;
import com.kongtrolink.framework.gateway.tower.entity.rec.PushAlarm;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.PushAlarmInfo;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.PushAlarmList;
import com.kongtrolink.framework.stereotype.Transverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Transverter(name = "alarm")
public class AlarmTransverter extends TransverterHandler {

    private static final Logger logger = LoggerFactory.getLogger(AlarmTransverter.class);

    @Value("gateway.alarmReport.version:1.0.0")
    private String alarmServerVersion;


    @Override
    protected void transferExecute(ParseProtocol parseProtocol) {
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
            AlarmReportInfo alarmInfo = new AlarmReportInfo();

        }
        AlarmReport report = new AlarmReport();
        report.setEnterpriseCode(getEnterpriseCode());
        report.setServerCode(getBusinessCode());
        report.setAlarms(alarmInfoList);
        String jsonResult = JSONObject.toJSONString(report);
        reportMsg(MqttUtils.preconditionServerCode(ServerName.ALARM_SERVER,alarmServerVersion),
                OperaCode.ALARM_REPORT,jsonResult);

    }
}
