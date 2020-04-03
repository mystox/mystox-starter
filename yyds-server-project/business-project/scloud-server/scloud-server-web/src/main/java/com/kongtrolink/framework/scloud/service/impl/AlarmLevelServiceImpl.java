package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.query.AlarmLevelQuery;
import com.kongtrolink.framework.scloud.service.AlarmLevelService;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Auther: liudd
 * @Date: 2020/4/3 08:31
 * @Description:
 */
@Service
public class AlarmLevelServiceImpl implements AlarmLevelService{

    @Value("${alarmModule.level.getLastUse:levelRemoteLastUse}")
    private String levelRemoteLastUse;
    @Value("${asset.getCIModel:getCIModel}")
    private String getCIModel;
    @Autowired
    MqttOpera mqttOpera;

    @Override
    public JSONObject getLastUse(AlarmLevelQuery alarmLevelQuery) {
       JSONObject jsonObject = new JSONObject();
        try {
            MsgResult msgResult = mqttOpera.opera(levelRemoteLastUse, JSONObject.toJSONString(alarmLevelQuery));
            jsonObject.put("success", true);
            jsonObject.put("data", msgResult.getMsg());
        }catch (Exception e){
            jsonObject.put("success", false);
            jsonObject.put("info", "远程调用失败");
            jsonObject.put("otherInfo", e.getMessage());
        }
        return jsonObject;
    }

    @Override
    public JSONObject getDeviceTypeList(AlarmLevelQuery alarmLevelQuery) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseCode", alarmLevelQuery.getEnterpriseCode());
        jsonObject.put("serverCode", alarmLevelQuery.getServerCode());
        MsgResult msgResult = mqttOpera.opera(getCIModel, jsonObject.toJSONString());
        String msg = msgResult.getMsg();
        jsonObject.put("success", true);
        jsonObject.put("data", msg);
        return jsonObject;
    }
}
