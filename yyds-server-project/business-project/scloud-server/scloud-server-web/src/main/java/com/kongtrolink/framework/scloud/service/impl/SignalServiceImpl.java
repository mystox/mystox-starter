package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.SignalMongo;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.DeviceTypeExport;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.service.SignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 信号点接口实现类
 * Created by Eric on 2020/2/10.
 */
@Service
public class SignalServiceImpl implements SignalService{

    @Autowired
    SignalMongo signalMongo;

    /**
     * 修改或保存信号类型映射表
     */
    @Override
    public void modifySignalType(String uniqueCode, List<DeviceType> deviceTypes) {
        signalMongo.modifyTypeList(uniqueCode, deviceTypes);
        // TODO: 2020/2/11 发送更新信号映射表MQTT消息

    }

    /**
     * 查询信号类型映射表
     */
    @Override
    public List<DeviceType> querySignalType(String uniqueCode) {
        return signalMongo.findSignalTypeList(uniqueCode);
    }

    /**
     * 导出信号类型映射表
     */
    @Override
    public List<DeviceTypeExport> getDeviceTypeExport(String uniqueCode) {
        List<DeviceTypeExport> value = new ArrayList<>();
        List<DeviceType> typeList = signalMongo.findSignalTypeList(uniqueCode);
        if (typeList == null || typeList.size() == 0){
            return value;
        }
        for (DeviceType deviceType : typeList){
            String code = deviceType.getCode();
            String type = deviceType.getTypeName();
            DeviceTypeExport export = new DeviceTypeExport(code,type,null);
            value.add(export);
            List<SignalType> signalTypes = deviceType.getSignalTypeList();
            if(signalTypes!=null){
                for(int i=0;i<signalTypes.size();i++){
                    SignalType signalType = signalTypes.get(i);
                    DeviceTypeExport exportSignal = new DeviceTypeExport(null,null,signalType);
                    value.add(exportSignal);
                }
            }
        }
        return value;
    }
}
