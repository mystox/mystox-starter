package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
/**
 * 数据监控 - 实时数据
 * @author Mag
 */
public interface RealTimeDataService {
    /**
     * 实时数据-获取设备列表
     */
    ListResult<DeviceModel> getDeviceList(String uniqueCode, DeviceQuery query);
}
