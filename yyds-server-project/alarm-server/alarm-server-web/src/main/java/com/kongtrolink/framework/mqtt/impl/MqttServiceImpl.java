package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.core.constant.Const;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.enttiy.Auxilary;
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
        JSONObject jsonObject = (JSONObject)JSONObject.parse(jsonStr);
        JsonResult jsonResult = new JsonResult();
        AlarmQuery alarmQuery = new AlarmQuery();
        initPara(alarmQuery, jsonObject);
        String type = alarmQuery.getType();
        if(StringUtil.isNUll(type)){
            jsonResult.setSuccess(false);
            jsonResult.setData("告警类型为空");
            return JSONObject.toJSONString(jsonResult);
        }
        String enterpriseCode = alarmQuery.getEnterpriseCode();
        if(StringUtil.isNUll(enterpriseCode)){
            jsonResult.setSuccess(false);
            jsonResult.setData("企业编码为空");
            return JSONObject.toJSONString(jsonResult);
        }
        String serverCode = alarmQuery.getServerCode();
        if(StringUtil.isNUll(serverCode)){
            jsonResult.setSuccess(false);
            jsonResult.setData("服务编码为空");
            return JSONObject.toJSONString(jsonResult);
        }
        List<DBObject> dbObjectList = alarmService.list(alarmQuery);
        Auxilary auxilary = auxilaryService.getByEnterServerCode(enterpriseCode, serverCode);
        jsonResult.setOtherInfo(auxilary);
        jsonResult.setSuccess(true);
        jsonResult.setData(dbObjectList);
        jsonResult.setCount(dbObjectList.size());
        return JSONObject.toJSONString(jsonResult);
    }

    /**
     * @auther: liudd
     * @date: 2020/2/28 10:50
     * 功能描述:填充远程接口调用的参数
     */
    private void initPara(AlarmQuery alarmQuery, JSONObject jsonObject){
        //list相关参数
        alarmQuery.setCurrentPage(jsonObject.getInteger(Const.ALARM_LIST_CURRENTPAGE));
        alarmQuery.setPageSize(jsonObject.getInteger(Const.ALARM_LIST_PAGESIZE));
        alarmQuery.setType(jsonObject.getString(Const.ALARM_LIST_TYPE));
        alarmQuery.setId(jsonObject.getString(Const.ALARM_LIST_ID));
        alarmQuery.setEnterpriseCode(jsonObject.getString(Const.ALARM_LIST_ENTERPRISECODE));
        alarmQuery.setServerCode(jsonObject.getString(Const.ALARM_LIST_SERVERCODE));
        alarmQuery.setName(jsonObject.getString(Const.ALARM_LIST_NAME));
        alarmQuery.setTargetLevelName(jsonObject.getString(Const.ALARM_LIST_TARGETLEVELNAME));
        alarmQuery.setState(jsonObject.getString(Const.ALARM_LIST_STATE));
        String isCheck = jsonObject.getString(Const.ALARM_LIST_CHECKSTATE);
        if(!StringUtil.isNUll(isCheck)){
            alarmQuery.setCheck(Boolean.parseBoolean(isCheck));
        }
        alarmQuery.setDeviceType(jsonObject.getString(Const.ALARM_LIST_DEVICETYPE));
        alarmQuery.setDeviceModel(jsonObject.getString(Const.ALARM_LIST_DEVICEMODEL));
        Long startBeginTime = jsonObject.getLong(Const.ALARM_LIST_STARTBEGINTIME);
        if(null != startBeginTime){
            alarmQuery.setStartBeginTime(new Date(startBeginTime));
        }
        Long startEndTime = jsonObject.getLong(Const.ALARM_LIST_STARTENDTIME);
        if(null != startEndTime){
            alarmQuery.setStartEndTime(new Date(startEndTime));
        }

        //远程确认相关参数
        alarmQuery.setOperate(jsonObject.getString(Const.ALARM_OPERATE));
        Long checkTime = jsonObject.getLong(Const.ALARM_OPERATE_TIME);
        if(null != checkTime){
            alarmQuery.setOperateTime(new Date(checkTime));
        }
        alarmQuery.setOperateUserId(jsonObject.getString(Const.ALARM_OPERATE_USER_ID));
        alarmQuery.setOperateUsername(jsonObject.getString(Const.ALARM_OPERATE_USERNAME));
        alarmQuery.setOperateDesc(jsonObject.getString(Const.ALARM_OPERATE_DESC));
        Long treport = jsonObject.getLong(Const.ALARM_LIST_TREPORT);
        if(null != treport){
            alarmQuery.setTreport(new Date(treport));
        }
    }

    /**
     * @param jsonStr
     * @auther: liudd
     * @date: 2020/3/2 13:25
     * 功能描述:远程告警确认
     */
    @Override
    public String alarmRemoteCheck(String jsonStr) {
        logger.info(jsonStr);
        //填充查询条件
        JSONObject jsonObject = (JSONObject)JSONObject.parse(jsonStr);
        JsonResult jsonResult = new JsonResult();
        AlarmQuery alarmQuery = new AlarmQuery();
        initPara(alarmQuery, jsonObject);
        String type = alarmQuery.getType();
        if(StringUtil.isNUll(type)){
            jsonResult.setSuccess(false);
            jsonResult.setData("告警类型为空");
            return JSONObject.toJSONString(jsonResult);
        }
        String enterpriseCode = alarmQuery.getEnterpriseCode();
        if(StringUtil.isNUll(enterpriseCode)){
            jsonResult.setSuccess(false);
            jsonResult.setData("企业编码为空");
            return JSONObject.toJSONString(jsonResult);
        }
        String serverCode = alarmQuery.getServerCode();
        if(StringUtil.isNUll(serverCode)){
            jsonResult.setSuccess(false);
            jsonResult.setData("服务编码为空");
            return JSONObject.toJSONString(jsonResult);
        }
        boolean check = alarmService.check(alarmQuery);
        if(check){
            jsonResult.setSuccess(true);
            jsonResult.setData(Const.ALARM_OPERATE_CHECK + Const.RESULT_SUCC);
        }else{
            jsonResult.setSuccess(false);
            jsonResult.setData(Const.ALARM_OPERATE_CHECK + Const.RESULT_FAIL);
        }
        return JSONObject.toJSONString(jsonResult);
    }

    /**
     * @param jsonStr
     * @auther: liudd
     * @date: 2020/3/2 16:13
     * 功能描述:远程告警消除
     */
    @Override
    public String alarmRemoteResolve(String jsonStr) {
        logger.info(jsonStr);
        //填充查询条件
        AlarmQuery alarmQuery = JSONObject.parseObject(jsonStr, AlarmQuery.class);
        JsonResult jsonResult = new JsonResult();
        String type = alarmQuery.getType();
        if(StringUtil.isNUll(type)){
            jsonResult.setSuccess(false);
            jsonResult.setData("告警类型为空");
            return JSONObject.toJSONString(jsonResult);
        }
        String enterpriseCode = alarmQuery.getEnterpriseCode();
        if(StringUtil.isNUll(enterpriseCode)){
            jsonResult.setSuccess(false);
            jsonResult.setData("企业编码为空");
            return JSONObject.toJSONString(jsonResult);
        }
        String serverCode = alarmQuery.getServerCode();
        if(StringUtil.isNUll(serverCode)){
            jsonResult.setSuccess(false);
            jsonResult.setData("服务编码为空");
            return JSONObject.toJSONString(jsonResult);
        }
        List<String> idList = alarmQuery.getIdList();
        List<Date> treportList = alarmQuery.getTreportList();
        for(int i=0; i<idList.size(); i++){
            String id = idList.get(i);
            Date date = treportList.get(i);
            alarmQuery.setId(id);
            alarmQuery.setTreport(date);
            boolean resolve = alarmService.resolve(alarmQuery);
        }
        return new JSONObject().toJSONString();
    }
}
