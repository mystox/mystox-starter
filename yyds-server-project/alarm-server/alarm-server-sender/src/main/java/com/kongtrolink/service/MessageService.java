package com.kongtrolink.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.enttiy.InformMsg;
import com.kongtrolink.message.ReqSingleMessage;
import com.kongtrolink.message.RespMessage;
import com.kongtrolink.message.SmsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/26 10:00
 * @Description:
 */
@Service
public class MessageService {

    @Value("${sms.enable}")
    String SMS_ENABLE;
    @Value("${sms.sms_user}")
    private String sms_user;
    @Value("${sms.sms_key}")
    private String sms_key;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    /**
     * @auther: liudd
     * @date: 2019/10/26 10:46
     * 功能描述:发送告警短信
     */
    public void doSendMessage(InformMsg informMsg){

        ReqSingleMessage reqSingleMessage = buildMessage(informMsg);
        LOGGER.info("AlarmId: {}, phones: {}", informMsg.getAlarmName(), informMsg.getInformAccount());
        try{
            System.out.println(reqSingleMessage);
            sendMessage(reqSingleMessage);
        }catch (Exception e){
            LOGGER.info("发送告警短信失败,AlarmId: {}, phones: {}, isReport: {}", informMsg.getAlarmName(),
                    informMsg.getInformAccount(), informMsg.getAlarmStateType());
        }
    }

    private ReqSingleMessage buildMessage(InformMsg informMsg) {
        ReqSingleMessage reqSingleMessage = new ReqSingleMessage(sms_user, sms_key);
        reqSingleMessage.setPhone(informMsg.getInformAccount());
        String tempCode = informMsg.getTempCode();
        reqSingleMessage.setTemplateId(Integer.valueOf(tempCode));
        JSONObject vars = new JSONObject();
        vars.put("%tier%", informMsg.getAddressName());
//        vars.put("%site%", enterprise.getName());
//        vars.put("%device%",informMsg.getDeviceName());
        vars.put("%alarm%", informMsg.getAlarmName());
        reqSingleMessage.setVars(vars);
        reqSingleMessage.setSignature(SmsUtil.createSignature(reqSingleMessage));
        return reqSingleMessage;
    }

    public void sendMessage(ReqSingleMessage reqSingleMessage) throws Exception {
        // 根据配置开启关闭短信提醒功能
        if(Boolean.parseBoolean(SMS_ENABLE) == false){
            LOGGER.info("sms.enable 短信功能关闭");
            return;
        }
        try {
            RespMessage msg = SmsUtil.sendMessage(reqSingleMessage);
            LOGGER.info("SMS Message sent. Msg: {}", JSONObject.toJSONString(reqSingleMessage));
            if (msg.getStatusCode() != 200) {
                throw new Exception(String.valueOf(msg.getStatusCode())+","+ msg.getMessage() + ";" + msg.getInfo());
            }
        } catch (IllegalAccessException | UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
            return;
        }
    }
}
