package com.kongtrolink.gateway.nb.cmcc.iot.service;


import cmcciot.nbapi.entity.Device;
import cmcciot.nbapi.entity.Execute;
import cmcciot.nbapi.entity.Resources;
import cmcciot.nbapi.online.CreateDeviceOpe;
import cmcciot.nbapi.online.ExecuteOpe;
import cmcciot.nbapi.online.ResourcesOpe;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.gateway.nb.cmcc.entity.base.BaseAck;
import com.kongtrolink.gateway.nb.cmcc.iot.config.NbIotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * NB命令的各种接口
 * by Mag on 2018/11/23.
 */
@Service
public class NbIotService {
    @Resource
    NbIotConfig nbIotConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(NbIotService.class);

    /**
     * 分类	IMEI	IMSI	            chip	    备注
     * 正式设备	15位数字	不超过15位数字，不为空	无	     正式设备无需chip值
     * 测试设备	4位数字	不超过15位数字，不为空	取值1-6	 测试设备必须填写chip值
     */
    public BaseAck addDevice(String title, String imei, String imsi) {
        // Create device
        CreateDeviceOpe deviceOpe = new CreateDeviceOpe(nbIotConfig.getMasterApiKey());
        Device device = new Device(title, imei, imsi);
        BaseAck baseAck = deviceOpe.operation(device, JSON.toJSONString(device));
        LOGGER.info(baseAck.toString());
        return baseAck;
    }

    /**
     * ﻿即时命令-命令下发
     *
     * @param command      命令值
     * @param imei         nbiot设备的身份码
     * @param objId        设备的object id，根据终端设备sdk确定
     * @param objInstId    nbiot设备object下具体的一个实例id,根据终端设备sdk确定
     * @param executeResId nbiot设备的资源id,根据终端设备sdk确定
     * @return 结果
     */
    public BaseAck command(String command, String imei, Integer objId, Integer objInstId, Integer executeResId) {
        // Execute
        ExecuteOpe executeOpe = new ExecuteOpe(nbIotConfig.getMasterApiKey());
        Execute execute = new Execute(imei, objId, objInstId, executeResId);
        //下发命令内容，JSON格式
        Map<String, String> body = new HashMap<>();
        body.put("args", command);
        BaseAck baseAck = executeOpe.operation(execute, JSONObject.toJSONString(body));
        LOGGER.info(baseAck.toString()+"context:--------"+command);
        return baseAck;
    }


    /**
     * ﻿即时命令-命令下发
     *
     * @param command      命令值
     * @param imei         nbiot设备的身份码
     * @param objId        设备的object id，根据终端设备sdk确定
     * @param objInstId    nbiot设备object下具体的一个实例id,根据终端设备sdk确定
     * @param executeResId nbiot设备的资源id,根据终端设备sdk确定
     * @return 结果
     */
    public BaseAck command(byte[] command, String imei, Integer objId, Integer objInstId, Integer executeResId) {
        // Execute
        ExecuteOpe executeOpe = new ExecuteOpe(nbIotConfig.getMasterApiKey());
        Execute execute = new Execute(imei, objId, objInstId, executeResId);
        //下发命令内容，byte格式
        BaseAck baseAck = executeOpe.operation(execute, command);
        LOGGER.info(baseAck.toString());
        return baseAck;
    }


    public BaseAck getResources(String imei) {
        ResourcesOpe resourcesOpe = new ResourcesOpe(nbIotConfig.getMasterApiKey());
        Resources resources = new Resources(imei);
        BaseAck baseAck = resourcesOpe.operation(resources, "");
        LOGGER.info(baseAck.toString());
        return baseAck;
    }


}
