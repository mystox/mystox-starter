package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.SignalMongo;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.service.SignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 信号点接口实现类
 * Created by Eric on 2020/2/10.
 */
@Service
public class SignalServiceImpl implements SignalService{

    @Autowired
    SignalMongo signalMongo;

    @Override
    public void modifySignalType(String uniqueCode, List<DeviceType> deviceTypes) {
        signalMongo.modifyTypeList(uniqueCode, deviceTypes);
        // TODO: 2020/2/11 发送更新信号映射表MQTT消息

    }
}
