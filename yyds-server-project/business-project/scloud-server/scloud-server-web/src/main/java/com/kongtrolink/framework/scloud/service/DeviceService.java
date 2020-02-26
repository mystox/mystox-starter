package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.DeviceEntity;

import java.util.List;

/**
 * 资产管理-设备资产管理 接口类
 * Created by Eric on 2020/2/12.
 */
public interface DeviceService {

    /**
     * @auther: liudd
     * @date: 2020/2/26 16:31
     * 功能描述:根据站点id列表获取设备列表
     */
    List<DeviceEntity> listBySiteIdList(List<String> siteIdList);

    /**
     * @auther: liudd
     * @date: 2020/2/26 16:33
     * 功能描述:设备列表转换成设备ID列表
     */
    List<String> list2IdList(List<DeviceEntity> deviceEntityList);

    /**
     * @auther: liudd
     * @date: 2020/2/26 16:35
     * 功能描述:列表转换成编码列表
     */
    List<String> list2CodeList(List<DeviceEntity> deviceEntityList);
}
