package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.SetPointAckMessage;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.SetThresholdAckMessage;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.FsuDeviceEntity;
import com.kongtrolink.framework.scloud.entity.model.SignalModel;
import com.kongtrolink.framework.scloud.entity.realtime.SignalDiInfo;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SignalDiInfoQuery;
import com.kongtrolink.framework.scloud.query.SignalQuery;

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
     * 根据 设备code 获取 该设备所属的FSU
     * @param uniqueCode 企业编码
     * @param query 查询
     * @return FSU信息
     */
    DeviceEntity getFsuInfoByDeviceCode(String uniqueCode,DeviceQuery query);

    /**
     * 实时数据-获取改设备的实时数据
     */
    SignalModel getData(String uniqueCode, SignalQuery signalQuery,String userId);

    /**
     * 单个信号点的实时数据
     */
    Object getDataDetail(String uniqueCode, SignalQuery signalQuery);
    /**
     * 设置值
     * @param signalQuery 参数
     * @return 返回
     */
    SetPointAckMessage setPoint(SignalQuery signalQuery);

    /**
     * 设置阈值
     * @param signalQuery 参数
     * @return 返回
     */
    SetThresholdAckMessage setThreshold(SignalQuery signalQuery);
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
