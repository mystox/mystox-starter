package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.dao.InformRuleDao;
import com.kongtrolink.framework.enttiy.InformRule;
import com.kongtrolink.framework.enttiy.MsgTemplate;
import com.kongtrolink.framework.query.InformRuleQuery;
import com.kongtrolink.framework.service.InformRuleService;
import com.kongtrolink.framework.service.MsgTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 09:42
 * @Description:
 */
@Service
public class InformRuleServiceImpl implements InformRuleService {

    @Autowired
    InformRuleDao ruleDao;
    @Autowired
    MsgTemplateService msgTemplateService;

    private static final Logger logger = LoggerFactory.getLogger(InformRuleServiceImpl.class);

    @Override
    public boolean save(InformRule deliver) {
        return ruleDao.save(deliver);
    }

    @Override
    public boolean delete(String deliverId) {
        return ruleDao.delete(deliverId);
    }

    @Override
    public boolean update(InformRule deliver) {
        return ruleDao.update(deliver);
    }

    @Override
    public InformRule get(String ruleId) {
        return ruleDao.get(ruleId);
    }

    @Override
    public List<InformRule> list(InformRuleQuery deliverQuery) {
        return ruleDao.list(deliverQuery);
    }

    @Override
    public int count(InformRuleQuery deliverQuery) {
        return ruleDao.count(deliverQuery);
    }

    @Override
    public InformRule getOne(InformRuleQuery deliverQuery) {
        return ruleDao.getOne(deliverQuery);
    }

    @Override
    public InformRule getByName(String name) {
        return ruleDao.getByName(name);
    }

    @Override
    public boolean updateStatus(String ruleId, String status) {
        return ruleDao.updateStatus(ruleId, status);
    }

    @Override
    public List<InformRule> getByTemplateIdAndType(String templateId, String type) {
        return ruleDao.getByTemplateIdAndType(templateId, type);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/29 9:59
     * 功能描述:火球系统默认告警投递规则
     */
    @Override
    public InformRule getSystemRule() {
        return ruleDao.getSystemRule();
    }

    /**
     * @auther: liudd
     * @date: 2019/10/29 9:59
     * 功能描述:初始化默认告警投递规则
     */
    @Override
    public void initInformRule() {
        InformRule systemRule = getSystemRule();
        if(null == systemRule){
            logger.info("系统默认投递规则不存在，准备初始化");
            systemRule = new InformRule();
            systemRule.setName("默认告警投递规则");
            systemRule.setRuleType(Contant.SYSTEM);
            systemRule.setContent(Arrays.asList("短信", "邮件"));
            systemRule.setDescribe("系统默认告警投递规则");


            systemRule.setMsgEnable("true");
            systemRule.setMsgBeginTime("00:00:00");
            systemRule.setMsgEndTime("23:59:59");
            systemRule.setMsgBeginTimeInt(0);
            systemRule.setMsgEndTimeInt(235959);
            systemRule.setMsgDayList(Arrays.asList(0,1,2,3,4,5,6));
            systemRule.setMsgLevelList(Arrays.asList(1));
            systemRule.setRepeat(1);
            MsgTemplate msgTemplate = msgTemplateService.getSystemTemplate(Contant.TEMPLATE_MSG);
            if(null != msgTemplate){
                systemRule.setMsgTemplate(new FacadeView(msgTemplate.get_id(), msgTemplate.getName()));
                systemRule.initTemplate(msgTemplate);
            }

            systemRule.setEmailEnable("true");
            systemRule.setEmailBeginTime("00:00:00");
            systemRule.setEmailEndTime("23:59:59");
            systemRule.setEmailBeginTimeInt(0);
            systemRule.setEmailEndTimeInt(235959);
            systemRule.setEmailDayList(Arrays.asList(0,1,2,3,4,5,6));
            systemRule.setEmailLevelList(Arrays.asList(1));
            MsgTemplate emailTemplate = msgTemplateService.getSystemTemplate(Contant.TEMPLATE_EMAIL);
            if(null != emailTemplate){
                systemRule.setEmailTemplate(new FacadeView(emailTemplate.get_id(), emailTemplate.getName()));
                systemRule.initTemplate(emailTemplate);
            }
            save(systemRule);
        }
        logger.info("系统默认告警投递规则：{}", systemRule.toString());
    }
}
