package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.core.constant.Const;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
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
    public String remoteList(String jsonStr) {
        System.out.println("+++++++++++++++");
        logger.info(jsonStr);
        JSONObject resultJson = new JSONObject();
        resultJson.put(Const.RESULT_CODE, "1");
        //填充查询条件
        JSONObject jsonObject = (JSONObject)JSONObject.parse(jsonStr);
        AlarmQuery alarmQuery = new AlarmQuery();
        initPara(alarmQuery, jsonObject);
        String type = alarmQuery.getType();
        if(StringUtil.isNUll(type)){
            resultJson.put(Const.RESULT_CODE, "0");
            resultJson.put(Const.RESULT_INFO, "告警类型为空");
            return resultJson.toJSONString();
        }
        String enterpriseCode = alarmQuery.getEnterpriseCode();
        if(StringUtil.isNUll(enterpriseCode)){
            resultJson.put(Const.RESULT_CODE, "0");
            resultJson.put(Const.RESULT_INFO, "企业编码为空");
            return resultJson.toJSONString();
        }
        String serverCode = alarmQuery.getServerCode();
        if(StringUtil.isNUll(serverCode)){
            resultJson.put(Const.RESULT_CODE, "0");
            resultJson.put(Const.RESULT_INFO, "服务编码为空");
            return resultJson.toJSONString();
        }

        List<DBObject> dbObjectList = alarmService.list(alarmQuery);
        Auxilary auxilary = auxilaryService.getByEnterServerCode(enterpriseCode, serverCode);
        JsonResult jsonResult = new JsonResult(dbObjectList);
        jsonResult.setOtherInfo(auxilary);
        resultJson.put(Const.DATA, jsonResult);
        return resultJson.toJSONString();
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
        alarmQuery.setId(jsonObject.getString(Const.ALARM_LIST_ID));
        alarmQuery.setEnterpriseCode(jsonObject.getString(Const.ALARM_LIST_ENTERPRISECODE));
        alarmQuery.setServerCode(jsonObject.getString(Const.ALARM_LIST_SERVERCODE));
        alarmQuery.setName(jsonObject.getString(Const.ALARM_LIST_NAME));
        alarmQuery.setLevel(jsonObject.getInteger(Const.ALARM_LIST_LEVEL));
        alarmQuery.setTargetLevel(jsonObject.getInteger(Const.ALARM_LIST_TARGETLEVEL));
        alarmQuery.setTargetLevelName(jsonObject.getString(Const.ALARM_LIST_TARGETLEVELNAME));
        alarmQuery.setState(jsonObject.getString(Const.ALARM_LIST_STATE));
        alarmQuery.setCheck(jsonObject.getBoolean(Const.ALARM_LIST_CHECKSTATE));
        alarmQuery.setDeviceType(jsonObject.getString(Const.ALARM_LIST_DEVICETYPE));
        alarmQuery.setDeviceModel(jsonObject.getString(Const.ALARM_LIST_DEVICEMODEL));
        alarmQuery.setTreport(jsonObject.getDate(Const.ALARM_LIST_TREPORT));
        alarmQuery.setStartBeginTime(jsonObject.getDate(Const.ALARM_LIST_BEGINTIME));
        alarmQuery.setStartEndTime(jsonObject.getDate(Const.ALARM_LIST_ENDTIME));

        alarmQuery.setOperate(jsonObject.getString(Const.ALARM_OPERATE));
        alarmQuery.setOperateTime(jsonObject.getDate(Const.ALARM_OPERATE_TIME));
        alarmQuery.setOperateUserId(jsonObject.getString(Const.ALARM_OPERATE_USER_ID));
        alarmQuery.setOperateUsername(jsonObject.getString(Const.ALARM_OPERATE_USERNAME));
    }

    /**
     * @param jsonStr
     * @auther: liudd
     * @date: 2020/2/28 10:04
     * 功能描述:修改告警属性，包括告警消除，关注，取消关注等
     */
    @Override
    public String update(String jsonStr) {
        logger.info(jsonStr);
        JSONObject resultJson = new JSONObject();
        JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonStr);
        //填充查询条件
        AlarmQuery alarmQuery = new AlarmQuery();
        initPara(alarmQuery, jsonObject);
        return resultJson.toJSONString();
    }
}
