package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 资产管理-设备资产管理 接口实现类
 * Created by Eric on 2020/2/12.
 */
public class DeviceServiceImpl implements DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServiceImpl.class);

    /**
     * @param siteIdList
     * @auther: liudd
     * @date: 2020/2/26 16:31
     * 功能描述:根据站点id列表获取设备列表
     */
    @Override
    public List<DeviceEntity> listBySiteIdList(List<String> siteIdList) {
        return null;
    }

    /**
     * @param deviceEntityList
     * @auther: liudd
     * @date: 2020/2/26 16:33
     * 功能描述:设备列表转换成设备ID列表
     */
    @Override
    public List<String> list2IdList(List<DeviceEntity> deviceEntityList) {
        if(null == deviceEntityList){
            return null;
        }
        List<String> deviceIdList = new ArrayList<>();
        for(DeviceEntity deviceEntity : deviceEntityList){
            deviceIdList.add(deviceEntity.getId() + "");
        }
        return deviceIdList;
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
}
