package com.kongtrolink.service;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.dao.InformMsgDao;
import com.kongtrolink.framework.dao.InformRuleDao;
import com.kongtrolink.framework.dao.InformRuleUserDao;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.InformMsg;
import com.kongtrolink.framework.enttiy.InformRule;
import com.kongtrolink.framework.enttiy.InformRuleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


/**
 * @Auther: liudd
 * @Date: 2019/10/22 10:11
 * @Description:获取对应的告警通知规则，并填充投递规则信息
 */
@Service
public class CreateInformMsgService {

    @Autowired
    InformMsgDao informMsgDao;
    @Autowired
    InformRuleDao informRuleDao;
    @Autowired
    InformRuleUserDao ruleUserDao;
    @Resource(name = "sendExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    CreateDeviceInfoService deliverService;

    private List<Alarm> informAlarmList = new ArrayList<>();

    public synchronized void handleInformAlarmList(List<Alarm> alarmList, String type){
        if(Contant.ZERO.equals(type)){
            informAlarmList.clear();
        }else if(Contant.ONE.equals(type)){
            if(null != alarmList){
                informAlarmList.addAll(alarmList);
                taskExecutor.execute(()->handle());
            }
        }
    }

    public synchronized List<Alarm> beforeHandle(){
        if(informAlarmList.size() == 0){
            return null;
        }
        List<Alarm> hanldeAlarmList = new ArrayList<>();
        hanldeAlarmList.addAll(informAlarmList);
        informAlarmList.clear();
        return hanldeAlarmList;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/22 10:25
     * 功能描述:不用定时任务试试
     */
    public void handle(){
        List<Alarm> alarmList = beforeHandle();
        if(null == alarmList || alarmList.size() == 0){
            return ;
        }
        Date date = new Date();
        List<InformMsg> informMsgList = new ArrayList<>();
        for(Alarm alarm : alarmList){
            List<InformMsg> msgInformMsgList = createMsg(alarm, Contant.INFORM_TYPE_MSG, date);
            informMsgList.addAll(msgInformMsgList);
            List<InformMsg> emailInformMsgList = createMsg(alarm, Contant.INFORM_TYPE_EMAL, date);
            informMsgList.addAll(emailInformMsgList);
            List<InformMsg> appEmailMsgList = createMsg(alarm, Contant.INFORM_TYPE_APP, date);
            informMsgList.addAll(appEmailMsgList);
        }
        if(informMsgList.size() > 0){
            deliverService.handleInformMsgList(informMsgList, Contant.ONE);
        }
    }

    private List<InformMsg> createMsg(Alarm alarm, String type, Date date){
        List<InformMsg> msgList = new ArrayList<>();
        String enterpriseCode = alarm.getEnterpriseCode();
        String serverCode = alarm.getServerCode();
        Integer targetLevel = alarm.getTargetLevel();
        Date treport = alarm.getTreport();
        List<InformRule> informRuleList = null;
        //获取匹配的短信通知
        try {
            informRuleList = informRuleDao.matchInform(enterpriseCode, serverCode, targetLevel, treport, type);
            if (null == informRuleList || informRuleList.size() == 0) {
                return msgList;
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
        List<String> ruleIdList = inform2IdList(informRuleList);
        //获取对应的用户id
        List<InformRuleUser> ruleUserList = ruleUserDao.getByRuleIdList(ruleIdList);
        //分别得到两个以informRule ID为键的map
        Map<String, List<InformRuleUser>> ruleId_ruleUserListMap = ruleUser2InformIdUserListMap(ruleUserList);
        Map<String, InformRule> informId_entityMap = inform2IdEntityMap(informRuleList);
        for(String ruleId : informId_entityMap.keySet()){
            InformRule informRule = informId_entityMap.get(ruleId);
            List<InformRuleUser> userList = ruleId_ruleUserListMap.get(ruleId);
            for(InformRuleUser ruleUser : userList){
                InformMsg msg = new InformMsg();
                msg.initAlarmInfo(alarm, informRule, ruleUser, type, date);
                msgList.add(msg);
            }
        }
        return msgList;
    }

    private List<String> inform2IdList(List<InformRule> informRuleList){
        List<String> idList = new ArrayList<>();
        if(null != informRuleList){
            for (InformRule informRule : informRuleList){
                idList.add(informRule.get_id());
            }
        }
        return idList;
    }

    private Map<String, InformRule> inform2IdEntityMap(List<InformRule> informRuleList){
        Map<String, InformRule> map = new HashMap<>();
        if(null != informRuleList){
            for(InformRule informRule : informRuleList){
                map.put(informRule.get_id(), informRule);
            }
        }
        return map;
    }

    private Map<String, List<InformRuleUser>> ruleUser2InformIdUserListMap(List<InformRuleUser> informRuleUserList){
        Map<String, List<InformRuleUser>> map = new HashMap<>();
        if(null != informRuleUserList){
            for(InformRuleUser informRuleUser : informRuleUserList){
                String informRuleId = informRuleUser.getInformRule().getStrId();
                List<InformRuleUser> ruleUserList = map.get(informRuleId);
                if(null == ruleUserList){
                    ruleUserList = new ArrayList<>();
                }
                ruleUserList.add(informRuleUser);
                map.put(informRuleId, ruleUserList);
            }
        }
        return map;
    }
}
