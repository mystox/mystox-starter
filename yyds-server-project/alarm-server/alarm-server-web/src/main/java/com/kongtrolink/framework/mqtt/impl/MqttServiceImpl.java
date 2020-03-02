package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.core.constant.Const;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.enttiy.Auxilary;
import com.kongtrolink.framework.exception.ParameterException;
import com.kongtrolink.framework.mqtt.MqttService;
import com.kongtrolink.framework.query.AlarmQuery;
import com.kongtrolink.framework.service.AlarmService;
import com.kongtrolink.framework.service.AuxilaryService;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        List<DBObject> dbObjectList = alarmService.list(alarmQuery);
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
        List<String> idList = alarmQuery.getIdList();
        List<Date> treportList = alarmQuery.getTreportList();
        if(null == idList || idList.size() == 0){
            throw new ParameterException("告警id为空");
        }
        if(null == treportList || treportList.size() == 0){
            throw new ParameterException("上报时间为空");
        }
        if(idList.size() != treportList.size()){
            throw new ParameterException("告警id和告警上报时间数量一致");
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
        JsonResult jsonResult = new JsonResult();
        AlarmQuery alarmQuery = JSONObject.parseObject(jsonStr, AlarmQuery.class);
        try{
            checkUpdatePara(alarmQuery);
        }catch (ParameterException e){
            jsonResult.setSuccess(false);
            jsonResult.setInfo(e.getMessage());
            return JSONObject.toJSONString(jsonResult);
        }
        int succCount = 0;
        int failCount = 0;
        List<String> idList = alarmQuery.getIdList();
        List<Date> treportList = alarmQuery.getTreportList();
        boolean check = false;
        for(int i=0; i<idList.size(); i++){
            String id = idList.get(i);
            Date treport = treportList.get(i);
            alarmQuery.setId(id);
            alarmQuery.setTreport(treport);
            if(Const.ALARM_OPERATE_CHECK.equals(alarmQuery.getOperate())) {
                check = alarmService.check(alarmQuery);
            }else if(Const.ALARM_OPERATE_RESOLVE.equals(alarmQuery.getOperate())){
                check = alarmService.resolve(alarmQuery);
            }
            if(check){
                succCount ++;
            }else{
                failCount ++;
            }
        }
        jsonResult.setData("共" + idList.size() + "个告警，成功" + succCount + "个， 失败" + failCount+"个");
        jsonResult.setSuccess(true);
        if(succCount == 0){
            jsonResult.setSuccess(false);
        }
        return JSONObject.toJSONString(jsonResult);
    }
}
