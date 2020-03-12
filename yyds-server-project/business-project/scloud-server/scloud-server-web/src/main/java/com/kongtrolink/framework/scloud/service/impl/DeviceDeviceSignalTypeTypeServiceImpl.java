package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.DeviceSignalTypeMongo;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.DeviceTypeExport;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.service.DeviceSignalTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 信号点接口实现类
 * Created by Eric on 2020/2/10.
 */
@Service
public class DeviceDeviceSignalTypeTypeServiceImpl implements DeviceSignalTypeService {

    @Autowired
    DeviceSignalTypeMongo deviceSignalTypeMongo;

    /**
     * 修改或保存信号类型映射表
     */
    @Override
    public void modifySignalType(String uniqueCode, List<DeviceType> deviceTypes) {
        deviceSignalTypeMongo.modifyTypeList(uniqueCode, deviceTypes);
        // TODO: 2020/2/11 发送更新信号映射表MQTT消息

    }

    /**
     * 查询信号类型映射表
     */
    @Override
    public List<DeviceType> querySignalType(String uniqueCode) {
        return deviceSignalTypeMongo.findSignalTypeList(uniqueCode);
    }

    /**
     * 导出信号类型映射表
     */
    @Override
    public List<DeviceTypeExport> getDeviceTypeExport(String uniqueCode) {
        List<DeviceTypeExport> value = new ArrayList<>();
        List<DeviceType> typeList = deviceSignalTypeMongo.findSignalTypeList(uniqueCode);
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

    /**
     * @param uniqueCode
     * @auther: liudd
     * @date: 2020/3/3 11:06
     * 功能描述:根据设备类型编码获取deviceType
     */
    @Override
    public DeviceType getByCode(String uniqueCode, String typeCode) {
        return deviceSignalTypeMongo.getByCode(uniqueCode, typeCode);
    }

    /**
     * @param uniqueCode
     * @param codeList
     * @param cntbIdList
     * @auther: liudd
     * @date: 2020/3/3 16:23
     * 功能描述:根据设备类型和cntbid列表，获取signalType列表
     */
    @Override
    public List<SignalType> getByCodeListCntbIdList(String uniqueCode, List<String> codeList, List<String> cntbIdList) {
        return deviceSignalTypeMongo.getByCodeListCntbIdList(uniqueCode, codeList, cntbIdList);
    }
}
