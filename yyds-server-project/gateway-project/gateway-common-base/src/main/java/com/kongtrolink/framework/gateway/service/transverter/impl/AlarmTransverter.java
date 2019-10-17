package com.kongtrolink.framework.gateway.service.transverter.impl;

import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.OperaCode;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.service.transverter.TransverterHandler;
import com.kongtrolink.framework.stereotype.Transverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/10/16, 15:27.
 * company: kongtrolink
 * description: 告警转换器
 * update record:
 */
@Service
@Transverter(name = "alarm")
public class AlarmTransverter extends TransverterHandler {


    @Value("gateway.alarmReport.version:1.0.0")
    private String alarmServerVersion;


    @Override
    protected void transferExecute(ParseProtocol parseProtocol) {
        String payLoad = parseProtocol.getPayload();//
        //TODO

        String jsonResult = "";
        reportMsg(MqttUtils.preconditionServerCode(ServerName.ALARM_SERVER,alarmServerVersion),
                OperaCode.ALARM_REPORT,jsonResult);
    }
}
