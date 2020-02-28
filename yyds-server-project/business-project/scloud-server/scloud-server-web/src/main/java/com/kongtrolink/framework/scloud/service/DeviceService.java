package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.query.DeviceQuery;

import java.util.List;

/**
 * 资产管理-设备资产管理 接口类
 * Created by Eric on 2020/2/12.
 */
public interface DeviceService {

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:列表
     */
    List<DeviceEntity> list(DeviceQuery deviceQuery);

    int count(DeviceQuery deviceQuery);

    /**
     * @auther: liudd
     * @date: 2020/2/26 16:35
     * 功能描述:列表转换成编码列表
     */
    List<String> list2CodeList(List<DeviceEntity> deviceEntityList);
}
