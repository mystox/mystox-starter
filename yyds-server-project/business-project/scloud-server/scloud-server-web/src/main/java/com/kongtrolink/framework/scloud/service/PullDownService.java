package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.PullDownQuery;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 下拉选择框
 */
public interface PullDownService {
    /**
     * 设备类型 下拉框
     * @param uniqueCode 企业编码
     * @return 设备类型列表
     */
    List<DeviceType> getDeviceTypeList(String uniqueCode);

    /**
     * 根据设备类型 获取该设备下的信号点列表
     * @param uniqueCode 企业编码
     * @param query deviceType 设备类型
     * @param query type 遥测等类型
     * @return 信号点列表
     */
    List<SignalType> getSignalTypeList(String uniqueCode, PullDownQuery query);

}