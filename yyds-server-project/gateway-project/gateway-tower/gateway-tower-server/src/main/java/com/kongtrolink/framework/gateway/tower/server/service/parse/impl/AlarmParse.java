package com.kongtrolink.framework.gateway.tower.server.service.parse.impl;


import com.chinatowercom.scservice.InvokeResponse;
import com.kongtrolink.framework.gateway.tower.core.entity.base.MessageResp;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.SendAlarmAck;
import com.kongtrolink.framework.gateway.tower.server.service.parse.ParseHandler;
import com.kongtrolink.framework.gateway.tower.server.service.transverter.impl.AlarmTransverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by mystoxlol on 2019/10/16, 15:28.
 * company: kongtrolink
 * description: 注册协议转换器
 * update record:
 * 注册 - 平台注册返回结果 -> 资管上报/告警消除
 */
@Service
public class AlarmParse extends ParseHandler {

    @Autowired
    private AlarmTransverter alarmTransverter;

    @Override
    public InvokeResponse execute(String payload) {
        try{
            alarmTransverter.transferExecute(payload);
            SendAlarmAck sendAlarmAck = new SendAlarmAck();
            sendAlarmAck.setResult(1);
            MessageResp respMessage = new MessageResp(sendAlarmAck);
            return baseInvokeResponse(respMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
