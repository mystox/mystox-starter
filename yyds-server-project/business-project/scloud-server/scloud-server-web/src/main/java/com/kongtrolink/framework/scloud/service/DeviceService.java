package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;

import java.util.List;

/**
 * 资产管理-设备资产管理 接口类
 * Created by Eric on 2020/2/12.
 */
public interface DeviceService {

    /**
     * 获取设备列表
     */
    List<DeviceModel> findDeviceList(String uniqueCode, DeviceQuery deviceQuery);

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:获取设备列表
     */
    List<DeviceEntity> list(DeviceQuery deviceQuery);

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:统计设备列表数量
     */
    int count(DeviceQuery deviceQuery);

    /**
     * @auther: liudd
     * @date: 2020/2/26 16:35
     * 功能描述:列表转换成编码列表
     */
    List<String> list2CodeList(List<DeviceEntity> deviceEntityList);

    /**
     * @auther: liudd
     * @date: 2020/3/3 10:49
     * 功能描述:根据设备编码获取单个设备
     */
    DeviceModel getByCode(String uniqueCode, String code);

    /**
     * @auther: liudd
     * @date: 2020/3/3 10:49
     * 功能描述:根据设备编码获取设备列表
     */
    List<DeviceModel> getByCodeList(String uniqueCode, List<String> deviceCodeList);
}
