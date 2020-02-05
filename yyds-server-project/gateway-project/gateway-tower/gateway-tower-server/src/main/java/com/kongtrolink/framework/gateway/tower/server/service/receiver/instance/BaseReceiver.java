package com.kongtrolink.framework.gateway.tower.server.service.receiver.instance;


import com.chinatowercom.scservice.InvokeResponse;
import com.kongtrolink.framework.gateway.tower.core.entity.base.CntbPktTypeTable;
import com.kongtrolink.framework.gateway.tower.core.entity.base.MessageResp;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.SendAlarm;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.SendAlarmAck;
import com.kongtrolink.framework.gateway.tower.core.util.MessageUtil;
import com.kongtrolink.framework.gateway.tower.server.service.parse.ParseService;
import com.kongtrolink.framework.gateway.tower.server.service.parse.impl.AlarmParse;
import com.kongtrolink.framework.gateway.tower.server.service.parse.impl.LoginParse;
import com.kongtrolink.framework.gateway.tower.server.service.receiver.ReceiveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.annotation.Resource;

/**
 * Created by mystoxlol on 2019/10/16, 9:40
 * company: kongtrolink
 * description:
 * update record:
 */
@Service("baseReceiver")
public class BaseReceiver extends ReceiveHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseReceiver.class);
    @Resource(name="loginParse")
    LoginParse loginParse;
    @Resource(name="alarmParse")
    AlarmParse alarmParse;
    @Autowired
    ThreadPoolTaskExecutor mqttExecutor;
    /**
     * 收到消息 处理类
     * Auto generated method signature
     *
     */
    @Override
    public com.chinatowercom.scservice.InvokeResponse invoke(com.chinatowercom.scservice.Invoke invoke0) {
//        LOGGER.info("*********************FSU上报 start****************************************");
//        LOGGER.info("[SCService Web Server]  receive request...");
        try {
            Document reqDocument = MessageUtil.stringToDocument(invoke0.getXmlData().getString());
            String pkType = reqDocument.getElementsByTagName("Name").item(0).getTextContent();
            LOGGER.debug("[SCService Web Server]  PK_Type.Name: " + pkType);
            Node reqInfoNode = reqDocument.getElementsByTagName("Info").item(0);
            String payload = MessageUtil.infoNodeToString(reqInfoNode);
            LOGGER.info("[SCService Web Server]  PK_Type: " + pkType + " >> fsuCode : \n" + payload);
            if(CntbPktTypeTable.LOGIN.equals(pkType)){
                return loginParse.execute(payload);
            }else if(CntbPktTypeTable.SEND_ALARM.equals(pkType)){
                return alarmParse.execute(payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }


}
