package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.DeviceTypeExport;

import java.util.List;

/**
 * 信号点接口类
 * Created by Eric on 2020/2/10.
 */
public interface SignalService {

    /**
     * 修改或保存信号类型映射表
     */
    void modifySignalType(String uniqueCode, List<DeviceType> deviceTypes);

    /**
     * 查询信号类型映射表
     */
    List<DeviceType> querySignalType(String uniqueCode);

    /**
     * 导出信号类型映射表
     */
    List<DeviceTypeExport> getDeviceTypeExport(String uniqueCode);

    /**
     * @auther: liudd
     * @date: 2020/3/3 11:06
     * 功能描述:根据设备类型编码获取deviceType
     */
    DeviceType getByCode(String uniqueCode, String typeCode);
}
