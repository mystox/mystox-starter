package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.entity.realtime.SignalDiInfo;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SignalDiInfoQuery;

import java.util.List;

/**
 * 数据监控 - 实时数据
 * @author Mag
 */
public interface RealTimeDataService {
    /**
     * 实时数据-获取设备列表
     */
    ListResult<DeviceModel> getDeviceList(String uniqueCode, DeviceQuery query);
    /**
     * 根据查询 某一个遥测信号值列表 -分页
     * @param uniqueCode 企业唯一吗
     * @param query 查询参数
     * @return 信号值列表
     */
    List<SignalDiInfo> getSignalDiInfo(String uniqueCode, SignalDiInfoQuery query);

    /**
     * 根据查询 某一个遥测信号值列表 - 取得总数
     * @param uniqueCode 企业唯一吗
     * @param query 查询参数
     * @return 信号值列表
     */
    int getSignalDiInfoNum(String uniqueCode, SignalDiInfoQuery query);
}
