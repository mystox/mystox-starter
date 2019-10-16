package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.mqtt.DeviceTypeMqttResult;
import com.kongtrolink.framework.mqtt.DeviceTypeResult;
import com.kongtrolink.framework.mqtt.ServerEntity;
import com.kongtrolink.framework.mqtt.EnterpriseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/16 09:27
 * @Description:
 */
@Service
public class MqttService {

    /**
     * @Auther: liudd
     * @Date: 2019/9/11 15:04
     * @Description:
     */

    public JSON getEnterpriseMsgAll(){
        ServerEntity serverEntity = new ServerEntity("zhyd", "智慧用电");
        EnterpriseEntity uniqueServiceEntity = new EnterpriseEntity("梅泰诺", "meitainuo");
        uniqueServiceEntity.setServerCodes(Arrays.asList(serverEntity));

        List<EnterpriseEntity> uniqueServiceEntityList = new ArrayList<>();
        uniqueServiceEntityList.add(uniqueServiceEntity);
        JSON json = (JSON) JSONObject.toJSON(uniqueServiceEntityList);
        return json;
    }

    public JSON getDeviceTypeList(String enterpriseCode, String serverCode){
        DeviceTypeResult deviceTypeResult = new DeviceTypeResult(enterpriseCode, serverCode, "kgdy", "kgdyARCM001");
        DeviceTypeResult result2 = new DeviceTypeResult(enterpriseCode, serverCode, "ywtcq", "ywtcq001");
        List<DeviceTypeResult> resultList = new ArrayList<>();
        resultList.add(deviceTypeResult);
        resultList.add(result2);
        DeviceTypeMqttResult mqttResult = new DeviceTypeMqttResult();
        mqttResult.setEnterpriseCode(enterpriseCode);
        mqttResult.setService(serverCode);
        mqttResult.setDeviceTypeResultList(resultList);
        return (JSON) JSONObject.toJSON(mqttResult);
    }

    public static void main(String[] args){
        MqttService mqttService = new MqttService();
        JSON uniqueService = mqttService.getEnterpriseMsgAll();
        System.out.println(uniqueService.toJSONString());

        JSON deviceType = mqttService.getDeviceTypeList("meitainuo", "zhyd");
        System.out.println(deviceType);
    }

}
