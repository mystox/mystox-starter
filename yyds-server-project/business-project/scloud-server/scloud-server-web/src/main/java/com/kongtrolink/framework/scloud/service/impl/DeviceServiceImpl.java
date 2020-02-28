package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
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
     * @param deviceQuery
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:列表
     */
    @Override
    public List<DeviceEntity> list(DeviceQuery deviceQuery) {
        return null;
    }

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
}
