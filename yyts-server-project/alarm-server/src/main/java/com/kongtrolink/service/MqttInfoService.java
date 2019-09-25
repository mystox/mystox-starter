package com.kongtrolink.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 15:56
 * @Description:第三方获取信息service
 */
@Service
public class MqttInfoService {

    /**
     * @auther: liudd
     * @date: 2019/9/20 15:58
     * 功能描述:获取所有企业uniqueCode
     */
    public JSONObject getUniqueCodeList(){

        return null;
    }

    /**
     * @auther: liudd
     * @date: 2019/9/20 15:58
     * 功能描述:根据企业编码，获取企业下所有服务
     */
    public JSONObject getServiceList(String uniqueCode){
        return null;
    }

    /**
     * @auther: liudd
     * @date: 2019/9/20 15:59
     * 功能描述:根据企业和服务，获取所有设备名称和型号
     */
    public JSONObject getDeviceTypeList(String uniqueCode, String service){

        return null;
    }
}
