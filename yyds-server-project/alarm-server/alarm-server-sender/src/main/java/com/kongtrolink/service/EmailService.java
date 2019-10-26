package com.kongtrolink.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.email.ApiMailInfo;
import com.kongtrolink.email.SendMail;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.dao.InformMsgDao;
import com.kongtrolink.framework.enttiy.InformMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

/**
 * @Auther: liudd
 * @Date: 2019/10/26 09:03
 * @Description:发送邮件综合service
 */
@Service
public class EmailService {

    @Autowired
    InformMsgDao informMsgDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    @Value("${email.api_user}")
    private String email_api_user;
    @Value("${email.api_key}")
    private String email_api_key;
    @Value("${email.from_address}")
    private String email_from_address;
    @Value("${email.from_name}")
    private String email_from_name;

    public void sendEmail(InformMsg informMsg) {
        //告警上报/告警消除
        String typeName = informMsg.getAlarmStateType();
        String email = informMsg.getInformAccount();
        String enterpriseServer = informMsg.getEnterpriseName() + informMsg.getServerName();
        String xsmtpapi = getXsmtpapi(informMsg);
        ApiMailInfo apiMailInfo = new ApiMailInfo(email_api_user, email_api_key, email_from_address, email_from_name);
        apiMailInfo.setSubject(enterpriseServer  + typeName + Contant.INFORM);
        apiMailInfo.setXsmtpapi(xsmtpapi);
        String url = informMsg.getUrl();
        if(!StringUtil.isNUll(url)){
            url = "http://api.sendcloud.net/apiv2/mail/sendtemplate";
        }
        apiMailInfo.setUrl(url);
        String tempCode = informMsg.getTempCode();
        apiMailInfo.setTemplateInvokeName(tempCode);
//        if(Contant.ALARM_STATE_REPORT.equals(typeName)){
//            apiMailInfo.setTemplateInvokeName(EMAIL_ALARM_REPORT_TEMPLATE_ID);
//        }else{
//            apiMailInfo.setTemplateInvokeName(EMAIL_ALARM_RECOVER_TEMPLATE_ID);
//        }
        apiMailInfo.setFrom_name(enterpriseServer);
        Boolean result = false;
        LOGGER.info(apiMailInfo.toString());
        try {
            result = SendMail.sendByJavaWebApi(apiMailInfo);
            LOGGER.info("email sending result {}", result);
        } catch (IOException ex) {
            LOGGER.info("发送告警邮件失败 ,AlarmId: {}, email: {}, isReport: {}", informMsg.getAlarmName(), email, typeName);
        }
        informMsg.setResult(result);
        informMsgDao.save(informMsg);
    }

    private String getXsmtpapi(InformMsg informMsg) {

        JSONArray sendTo = new JSONArray();
        sendTo.add(informMsg.getInformAccount());

        JSONArray tier = new JSONArray();
        tier.add(informMsg.getAddressName());

//        JSONArray site = new JSONArray();
//        String siteNaem = enterprise.getName();
//        if(null != alarm.getSite()){
//            siteNaem = alarm.getSite().getName();
//        }
//        site.add(siteNaem);

        JSONArray device = new JSONArray();
        device.add(informMsg.getDeviceName());

        JSONArray alarmName = new JSONArray();
        alarmName.add(informMsg.getAlarmName());

        JSONObject sub = new JSONObject();
        sub.put("%tier%", tier);
//        sub.put("%site%", site);
        sub.put("%device%", device);
        sub.put("%alarmName%", alarmName);

        JSONObject ret = new JSONObject();
        ret.put("to", sendTo);
        ret.put("sub", sub);
        return ret.toString();
    }
}
