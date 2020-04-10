package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.core.constant.Const;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.Auxilary;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.exception.ParameterException;
import com.kongtrolink.framework.mqtt.MqttService;
import com.kongtrolink.framework.query.AlarmQuery;
import com.kongtrolink.framework.query.AuxilaryQuery;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.AlarmService;
import com.kongtrolink.framework.service.AuxilaryService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/2/28 10:05
 * @Description:
 */
@Service
public class MqttServiceImpl implements MqttService{

    @Autowired
    AlarmService alarmService;
    @Autowired
    AuxilaryService auxilaryService;
    @Autowired
    EnterpriseLevelService enterpriseLevelService;

    private static final Logger logger = LoggerFactory.getLogger(MqttServiceImpl.class);

    /**
     * @param jsonStr
     * @auther: liudd
     * @date: 2020/2/28 10:04
     * 功能描述:告警列表，需要判定所属表
     */
    @Override
    public String alarmRemoteList(String jsonStr) {
        logger.info(jsonStr);
        //填充查询条件
        JsonResult jsonResult = new JsonResult();
        AlarmQuery alarmQuery = JSONObject.parseObject(jsonStr, AlarmQuery.class);
            try{
            checkPara(alarmQuery);
        }catch (ParameterException e){
            jsonResult.setSuccess(false);
            jsonResult.setInfo(e.getMessage());
            return JSONObject.toJSONString(jsonResult);
        }
        List<Alarm> dbObjectList = alarmService.list(alarmQuery);
        Auxilary auxilary = auxilaryService.getByEnterServerCode(alarmQuery.getEnterpriseCode(), alarmQuery.getServerCode());
        jsonResult.setOtherInfo(auxilary);
        jsonResult.setSuccess(true);
        jsonResult.setData(dbObjectList);
        jsonResult.setCount(dbObjectList.size());
        return JSONObject.toJSONString(jsonResult);
    }

    /**
     * @auther: liudd
     * @date: 2020/2/28 10:50
     * 功能描述:判定远程调用基本参数
     */
    private void checkPara(AlarmQuery alarmQuery)throws ParameterException{
        if(StringUtil.isNUll(alarmQuery.getEnterpriseCode())){
            throw new ParameterException("企业编码为空");
        }
        if(StringUtil.isNUll(alarmQuery.getServerCode())){
            throw new ParameterException("服务编码为空");
        }
        if(StringUtil.isNUll(alarmQuery.getType())){
            throw new ParameterException("告警类型为空");
        }
    }

    private void checkUpdatePara(AlarmQuery alarmQuery)throws ParameterException{
        checkPara(alarmQuery);
        //判定告警id，上报时间
        List<String> keyList = alarmQuery.getKeyList();
        List<Date> treportList = alarmQuery.getTreportList();
        if(null == keyList || keyList.size() == 0){
            throw new ParameterException("告警key为空");
        }
        if(null == treportList || treportList.size() == 0){
            throw new ParameterException("上报时间为空");
        }
        if(keyList.size() != treportList.size()){
            throw new ParameterException("告警key和告警上报时间数量一致");
        }
        if(StringUtil.isNUll(alarmQuery.getOperate())){
            throw new ParameterException("操作类型为空");
        }
    }

    /**
     * @param jsonStr
     * @auther: liudd
     * @date: 2020/3/2 13:25
     * 功能描述:远程告警确认
     */
    @Override
    public String alarmRemoteOperate(String jsonStr) {
        logger.info(jsonStr);
        //填充查询条件
        JSONObject jsonObject = new JSONObject();
        AlarmQuery alarmQuery = JSONObject.parseObject(jsonStr, AlarmQuery.class);
        try{
            checkUpdatePara(alarmQuery);
        }catch (ParameterException e){
            jsonObject.put("success", false);
            jsonObject.put("info", e.getMessage());
            return jsonObject.toJSONString();
        }
        int succCount = 0;
        int failCount = 0;
        List<String> keyList = alarmQuery.getKeyList();
        List<Date> treportList = alarmQuery.getTreportList();
        List<String> succKeyList = new ArrayList<>();
        List<String> failKeyList = new ArrayList<>();
        boolean check = false;
        for(int i=0; i<keyList.size(); i++){
            alarmQuery.setKey(keyList.get(i));
            alarmQuery.setTreport(treportList.get(i));
            if(Const.ALARM_OPERATE_RESOLVE.equals(alarmQuery.getOperate())){
                alarmQuery.setTrecover(alarmQuery.getOperateTime());
                check = alarmService.resolveByKey(alarmQuery);
            }
            if(check){
                succKeyList.add(keyList.get(i));
            }else{
                failCount ++;
                failKeyList.add(keyList.get(i));
            }
        }
        jsonObject.put("success", true);
        jsonObject.put("succKeyList", succKeyList);
        jsonObject.put("failKeyList", failKeyList);
        return jsonObject.toJSONString();
    }

    @Override
    public String levelRemoteLastUse(String jsonStr) {
        logger.info(jsonStr);
        EnterpriseLevelQuery enterpriseLevelQuery = JSONObject.parseObject(jsonStr, EnterpriseLevelQuery.class);
        List<EnterpriseLevel> lastUse = enterpriseLevelService.getLastUse(enterpriseLevelQuery.getEnterpriseCode(), enterpriseLevelQuery.getServerCode());
        return JSONObject.toJSON(lastUse).toString();
    }

    @Override
    public String auxilaryRemoteGet(String jsonStr) {
        logger.info(jsonStr);
        JSONObject jsonObject = new JSONObject();
        AuxilaryQuery auxilaryQuery = JSONObject.parseObject(jsonStr, AuxilaryQuery.class);
        Auxilary auxilary = auxilaryService.getByEnterServerCode(auxilaryQuery.getEnterpriseCode(), auxilaryQuery.getServerCode());
        jsonObject.put("success",true);
        jsonObject.put("data", auxilary);
        if(null == auxilary){
            jsonObject.put("success",true);
            jsonObject.put("info", "无数据");
        }
        return jsonStr.toString();
    }

    @Override
    public String auxilaryRemoteDel(String jsonStr) {
        logger.info(jsonStr);
        AuxilaryQuery auxilaryQuery = JSONObject.parseObject(jsonStr, AuxilaryQuery.class);
        JsonResult delete = auxilaryService.delete(auxilaryQuery);
        return JSONObject.toJSON(delete).toString();
    }
}
