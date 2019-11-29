package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.dao.MsgTemplateDao;
import com.kongtrolink.framework.enttiy.MsgTemplate;
import com.kongtrolink.framework.query.MsgTemplateQuery;
import com.kongtrolink.framework.runner.ApplicationInitRunner;
import com.kongtrolink.framework.service.MsgTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/23 14:34
 * @Description:
 */
@Service
public class MsgTemplateServiceImpl implements MsgTemplateService{

    @Value("${sender.serverVersion:ALARM-SERVER-SENDER_1.0.0}")
    private String serverVersion;
    @Value("${sender.operaCode:handleSender}")
    private String operaCode;

    @Value("${sms.url}")
    private String sms_url;
    @Value("${sms.alarm.reportTemplateId}")
    private String msgReportCode;
    @Value("${sms.alarm.recoverTemplateId}")
    private String msgRecoverCode;

    @Value("${email.url}")
    private String email_url;
    @Value("${email.alarm.reportTemplateId}")
    private String emailReportCode;
    @Value("${email.alarm.recoverTemplateId}")
    private String emailResolveCode;

    @Value("${jpush.alarm.reportTemplateId}")
    private String appReportCode;
    @Value("${jpush.alarm.recoverTemplateId}")
    private String appResolveCode;

    private static final Logger logger = LoggerFactory.getLogger(MsgTemplateServiceImpl.class);
    @Autowired
    MsgTemplateDao msgTemplateDao;
    @Override
    public boolean save(MsgTemplate msgTemplate) {
        return msgTemplateDao.save(msgTemplate);
    }

    @Override
    public boolean delete(String msgTemplateId) {
        return msgTemplateDao.delete(msgTemplateId);
    }

    @Override
    public boolean update(MsgTemplate msgTemplate) {
        return msgTemplateDao.update(msgTemplate);
    }

    @Override
    public MsgTemplate get(String msgTemplateId) {
        return msgTemplateDao.get(msgTemplateId);
    }

    @Override
    public List<MsgTemplate> list(MsgTemplateQuery msgTemplateQuery) {
        return msgTemplateDao.list(msgTemplateQuery);
    }

    @Override
    public int count(MsgTemplateQuery msgTemplateQuery) {
        return msgTemplateDao.count(msgTemplateQuery);
    }

    @Override
    public MsgTemplate getByName(String enterpriseCode, String serverCode, String name) {
        return msgTemplateDao.getByName(enterpriseCode, serverCode, name);
    }

    /**
     * @param type
     * @auther: liudd
     * @date: 2019/10/29 10:48
     * 功能描述:获取默认模板
     */
    @Override
    public MsgTemplate getSystemTemplate(String type) {
        return msgTemplateDao.getSystemTemplate(type);
    }

    public void initMsgTemplate(){
        Date curDate = new Date();
        MsgTemplate emailTemplate = msgTemplateDao.getSystemTemplate(Contant.TEMPLATE_EMAIL);
        if(null == emailTemplate){
            logger.info("系统默认邮件模板不存在，准备初始化...");
            emailTemplate = new MsgTemplate();
            emailTemplate.setName("默认邮件模板");
            emailTemplate.setTemplateType("系统");
            emailTemplate.setType("邮件");
            emailTemplate.setServerVerson(serverVersion);
            emailTemplate.setOperaCode(operaCode);
            emailTemplate.setUrl(email_url);
            emailTemplate.setReportCode(emailReportCode);
            emailTemplate.setResolveCode(emailResolveCode);
            emailTemplate.setUpdateTime(curDate);
            msgTemplateDao.save(emailTemplate);
        }
        logger.info("默认邮件模板：" + emailTemplate.toString());
        MsgTemplate msgTemplate = msgTemplateDao.getSystemTemplate(Contant.TEMPLATE_MSG);
        if(null == msgTemplate){
            logger.info("系统默认短信模板不存在，准备初始化...");
            msgTemplate = new MsgTemplate();
            msgTemplate.setName("默认短信模板");
            msgTemplate.setTemplateType("系统");
            msgTemplate.setType("短信");
            msgTemplate.setServerVerson(serverVersion);
            msgTemplate.setOperaCode(operaCode);
            msgTemplate.setUrl(sms_url);
            msgTemplate.setReportCode(msgReportCode);
            msgTemplate.setResolveCode(msgRecoverCode);
            msgTemplate.setUpdateTime(curDate);
            msgTemplateDao.save(msgTemplate);
        }
        logger.info("默认短信模板：" + emailTemplate.toString());

        MsgTemplate appTemplate = msgTemplateDao.getSystemTemplate(Contant.TEMPLATE_APP);
        if(null == appTemplate){
            logger.info("系统默认APP模板不存在，准备初始化...");
            appTemplate = new MsgTemplate();
            appTemplate.setName("默认APP模板");
            appTemplate.setTemplateType("系统");
            appTemplate.setType("APP");
            appTemplate.setServerVerson(serverVersion);
            appTemplate.setOperaCode(operaCode);
            appTemplate.setReportCode(appReportCode);
            appTemplate.setResolveCode(appResolveCode);
            appTemplate.setUpdateTime(curDate);
            msgTemplateDao.save(appTemplate);
        }
        logger.info("默认APP模板：" + appTemplate.toString());
    }
}
