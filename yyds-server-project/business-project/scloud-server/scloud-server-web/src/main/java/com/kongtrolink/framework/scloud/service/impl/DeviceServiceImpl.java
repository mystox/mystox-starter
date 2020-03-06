package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.OperaCodeConstant;
import com.kongtrolink.framework.scloud.dao.DeviceMongo;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.mqtt.entity.CIResponseEntity;
import com.kongtrolink.framework.scloud.mqtt.query.BasicDeviceQuery;
import com.kongtrolink.framework.scloud.service.AssetCIService;
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
    DeviceMongo deviceMongo;
    @Autowired
    MqttOpera mqttOpera;
    @Autowired
    AssetCIService assetCIService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServiceImpl.class);

    /**
     * 获取单个站点下设备列表
     */
    @Override
    public List<DeviceModel> findDeviceList(String uniqueCode, DeviceQuery deviceQuery) {
        List<DeviceModel> list = new ArrayList<>();
        String siteCode = deviceQuery.getSiteCode();

        //获取（业务平台端）单个站点下所有设备
        List<DeviceEntity> devices = deviceMongo.findAllDevicesBySiteCode(uniqueCode, siteCode);
        if (devices != null && devices.size() > 0) {
            List<String> deviceCodes = new ArrayList<>();   //设备编码
            for (DeviceEntity device : devices){
                deviceCodes.add(device.getCode());
            }
            deviceQuery.setDeviceCodes(deviceCodes);

            //从【资管】获取设备(基本信息)列表
            MsgResult msgResult = assetCIService.getAssetDeviceList(uniqueCode, deviceQuery);
            int stateCode = msgResult.getStateCode();
            if (stateCode == 1) {
                LOGGER.info("【设备管理】，从【资管】获取设备基本信息成功");
                CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);

            } else {
                LOGGER.error("【设备管理】,从【资管】获取设备基本信息失败");
            }
        }

        return list;
    }


    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:获取设备实体类表，不包含名字，无语远程调用
     */
    @Override
    public List<DeviceEntity> listEntity(String uniqueCode, DeviceQuery deviceQuery) {
        return deviceMongo.listEntity(uniqueCode, deviceQuery);
    }

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:统计设备列表数量
     */
    @Override
    public int countEntity(String uniqueCode, DeviceQuery deviceQuery) {
        return 0;
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

    @Override
    public List<String> entityList2CodeList(List<DeviceEntity> deviceEntityList) {
        if(null == deviceEntityList){
            return null;
        }
        List<String> deviceCodeList = new ArrayList<>();
        for(DeviceEntity deviceEntity : deviceEntityList){
            deviceCodeList.add(deviceEntity.getCode());
        }
        return deviceCodeList;
    }
}
