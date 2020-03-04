package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.OperaCodeConstant;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.mqtt.entity.CIResponseEntity;
import com.kongtrolink.framework.scloud.mqtt.query.BasicDeviceQuery;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 资产管理-设备资产管理 接口实现类
 * Created by Eric on 2020/2/12.
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    MqttOpera mqttOpera;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServiceImpl.class);

    /**
     * 获取设备列表
     */
    @Override
    public List<DeviceModel> findDeviceList(String uniqueCode, DeviceQuery deviceQuery) {
        List<DeviceModel> list = new ArrayList<>();
        BasicDeviceQuery basicDeviceQuery = new BasicDeviceQuery();
        // TODO: 2020/2/28 拼凑向【资管】获取设备基本信息请求参数

        // TODO: 2020/2/28 从【资管】获取设备基本信息
        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_CI, JSON.toJSONString(basicDeviceQuery));
        int stateCode = msgResult.getStateCode();
        if (stateCode == 1){
            LOGGER.info("【设备管理】，从【资管】获取设备基本信息成功");
            CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);

        }else {
            LOGGER.error("【设备管理】,从【资管】获取设备基本信息失败");
        }

        return list;
    }


    /**
     * @param deviceQuery
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:列表
     */
    @Override
    public List<DeviceEntity> list(DeviceQuery deviceQuery) {
        return null;
    }

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:统计设备列表数量
     */
    @Override
    public int count(DeviceQuery deviceQuery) {
        return 0;
    }

    /**
     * @param deviceEntityList
     * @auther: liudd
     * @date: 2020/2/26 16:35
     * 功能描述:列表转换成编码列表
     */
    @Override
    public List<String> list2CodeList(List<DeviceEntity> deviceEntityList) {
        if(null == deviceEntityList){
            return null;
        }
        List<String> deviceCodeList = new ArrayList<>();
        for(DeviceEntity deviceEntity : deviceEntityList){
            deviceCodeList.add(deviceEntity.getCode());
        }
        return deviceCodeList;
    }

    /**
     * @param uniqueCode
     * @param code
     * @auther: liudd
     * @date: 2020/3/3 10:49
     * 功能描述:根据设备编码获取单个设备
     */
    @Override
    public DeviceModel getByCode(String uniqueCode, String code) {
        return null;
    }

    /**
     * @auther: liudd
     * @date: 2020/3/3 10:49
     * 功能描述:根据设备编码获取设备列表
     */
    @Override
    public List<DeviceModel> getByCodeList(String uniqueCode, List<String> deviceCodeList) {
        return null;
    }
}
