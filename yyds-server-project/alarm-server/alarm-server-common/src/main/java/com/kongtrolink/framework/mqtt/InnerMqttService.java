package com.kongtrolink.framework.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.mqtt.mqttEntity.DeviceTypeMqttResult;
import com.kongtrolink.framework.mqtt.mqttEntity.DeviceTypeResult;
import com.kongtrolink.framework.mqtt.mqttEntity.ServerEntity;
import com.kongtrolink.framework.mqtt.mqttEntity.UniqueServiceEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 15:04
 * @Description:
 */
@Service
public class InnerMqttService {

    public static JSON getUniqueService(){
        ServerEntity serverEntity = new ServerEntity("智慧用电", "zhyd");
        UniqueServiceEntity uniqueServiceEntity = new UniqueServiceEntity("梅泰诺", "meitainuo");
        uniqueServiceEntity.setServerCodes(Arrays.asList(serverEntity));

        List<UniqueServiceEntity> uniqueServiceEntityList = new ArrayList<>();
        uniqueServiceEntityList.add(uniqueServiceEntity);
        JSON json = (JSON)JSONObject.toJSON(uniqueServiceEntityList);
        return json;
    }

    public static JSON getDeviceType(String uniqueCode, String service){
        DeviceTypeResult deviceTypeResult = new DeviceTypeResult(uniqueCode, service, "kgdy", "kgdyARCM001");
        DeviceTypeResult result2 = new DeviceTypeResult(uniqueCode, service, "ywtcq", "ywtcq001");
        List<DeviceTypeResult> resultList = new ArrayList<>();
        resultList.add(deviceTypeResult);
        resultList.add(result2);
        DeviceTypeMqttResult mqttResult = new DeviceTypeMqttResult();
        mqttResult.setEnterpriseCode(uniqueCode);
        mqttResult.setService(service);
        mqttResult.setDeviceTypeResultList(resultList);
        return (JSON) JSONObject.toJSON(mqttResult);
    }

    public static void main(String[] args){
        JSON uniqueService = getUniqueService();
        System.out.println(uniqueService.toJSONString());
//
//        JSON deviceType = getDeviceType("meitainuo", "zhyd");
//        System.out.println(deviceType);
    }
}
