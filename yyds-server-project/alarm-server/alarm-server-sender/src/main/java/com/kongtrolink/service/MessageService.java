package com.kongtrolink.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.dao.InformMsgDao;
import com.kongtrolink.framework.enttiy.InformMsg;
import com.kongtrolink.message.ReqSingleMessage;
import com.kongtrolink.message.RespMessage;
import com.kongtrolink.message.SmsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    InformMsgDao informMsgDao;
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
        boolean result = false;
        try{
            RespMessage msg = SmsUtil.sendMessage(reqSingleMessage, informMsg.getUrl());
            LOGGER.info("SMS Message sent. Msg: {}, result:{}", JSONObject.toJSONString(reqSingleMessage), JSONObject.toJSONString(msg));
            if(msg.getStatusCode() == 200){
                result = true;
            }else{
                throw new Exception(String.valueOf(msg.getStatusCode())+","+ msg.getMessage() + ";" + msg.getInfo());
            }
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.info("发送告警短信失败,AlarmId: {}, phones: {}, isReport: {}", informMsg.getAlarmName(),
                    informMsg.getInformAccount(), informMsg.getAlarmStateType());
        }
        String resultStr = result ? Contant.OPE_SEND + Contant.RESULT_SUC : Contant.OPE_SEND + Contant.RESULT_FAIL;
        informMsg.setResult(resultStr);
        informMsgDao.save(informMsg);
    }

    private ReqSingleMessage buildMessage(InformMsg informMsg) {
        ReqSingleMessage reqSingleMessage = new ReqSingleMessage(sms_user, sms_key);
        reqSingleMessage.setPhone(informMsg.getInformAccount());
        String tempCode = informMsg.getTempCode();
        reqSingleMessage.setTemplateId(Integer.parseInt(tempCode));
        JSONObject vars = new JSONObject();
        vars.put("%tier%", informMsg.getAddressName());
        //20191114当前服务没有站点信息
//        vars.put("%site%", "站点名称");
        vars.put("%alarm%", informMsg.getAlarmName());
        reqSingleMessage.setVars(vars);
        reqSingleMessage.setSignature(SmsUtil.createSignature(reqSingleMessage));
        return reqSingleMessage;
    }
//
//    public void sendMessage(ReqSingleMessage reqSingleMessage) throws Exception {
//        try {
//            RespMessage msg = SmsUtil.sendMessage(reqSingleMessage);
//            LOGGER.info("SMS Message sent. Msg: {}", JSONObject.toJSONString(reqSingleMessage));
//            if (msg.getStatusCode() != 200) {
//                throw new Exception(String.valueOf(msg.getStatusCode())+","+ msg.getMessage() + ";" + msg.getInfo());
//            }
//        } catch (IllegalAccessException | UnsupportedEncodingException e) {
//            LOGGER.error(e.getMessage());
//        }
//    }
}
