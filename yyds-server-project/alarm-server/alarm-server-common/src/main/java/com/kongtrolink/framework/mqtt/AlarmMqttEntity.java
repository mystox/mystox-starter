package com.kongtrolink.framework.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.enttiy.Alarm;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/21 09:10
 * @Description:告警上报或者告警消除MQTT实体
 */
public class AlarmMqttEntity {
    private String enterpriseCode;
    private String serverCode;
    private List<JSONObject> alarms;

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public List<JSONObject> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<JSONObject> alarms) {
        this.alarms = alarms;
    }
}
