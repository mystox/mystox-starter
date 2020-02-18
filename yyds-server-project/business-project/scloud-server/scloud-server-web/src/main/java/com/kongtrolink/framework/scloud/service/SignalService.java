package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.DeviceType;

import java.util.List;

/**
 * 信号点接口类
 * Created by Eric on 2020/2/10.
 */
public interface SignalService {

    void modifySignalType(String uniqueCode, List<DeviceType> deviceTypes);
}
