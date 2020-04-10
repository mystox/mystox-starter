package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.query.AuxilaryQuery;
import com.kongtrolink.framework.scloud.service.AuxilaryService;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Auther: liudd
 * @Date: 2020/4/3 09:31
 * @Description:
 */
@Service
public class AuxilaryServiceImpl implements AuxilaryService{

    @Value("${alarmModule.auxilaryRemoteGet:auxilaryRemoteGet}")
    private String auxilaryRemoteGet;
    @Value("${alarmModule.auxilaryRemoteDel:auxilaryRemoteDel}")
    private String auxilaryRemoteDel;
    @Autowired
    MqttOpera mqttOpera;

    @Override
    public JSONObject getByEnterServerCode(AuxilaryQuery auxilaryQuery) {
        JSONObject jsonObject = new JSONObject();
        try {
            MsgResult opera = mqttOpera.opera(auxilaryRemoteGet, JSONObject.toJSONString(auxilaryQuery));
            String msg = opera.getMsg();
            jsonObject.put("success", true);
            jsonObject.put("data", msg);
        }catch (Exception e){
            jsonObject.put("success", false);
            jsonObject.put("info", "远程调用失败");
            jsonObject.put("otherInfo", e.getMessage());
        }
        return jsonObject;
    }

    @Override
    public JSONObject delete(AuxilaryQuery auxilaryQuery) {
        JSONObject jsonObject = new JSONObject();
        try {
            MsgResult opera = mqttOpera.opera(auxilaryRemoteDel, JSONObject.toJSONString(auxilaryRemoteDel));
            String msg = opera.getMsg();
            jsonObject.put("success", true);
            jsonObject.put("data", msg);
        }catch (Exception e){
            jsonObject.put("success", false);
            jsonObject.put("info", "远程调用失败");
            jsonObject.put("otherInfo", e.getMessage());
        }
        return jsonObject;
    }
}
