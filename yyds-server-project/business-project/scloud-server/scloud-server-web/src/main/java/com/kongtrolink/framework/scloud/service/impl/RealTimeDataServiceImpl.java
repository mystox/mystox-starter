package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.dao.RealTimeDataDao;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.service.RealTimeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 数据监控 - 实时数据
 * @author Mag
 */
@Service
public class RealTimeDataServiceImpl implements RealTimeDataService {
    @Autowired
    RealTimeDataDao realTimeDataDao;
    /**
     * 实时数据-获取设备列表
     *
     */
    @Override
    public ListResult<DeviceModel> getDeviceList(String uniqueCode, DeviceQuery query) {
        List<DeviceModel> list = realTimeDataDao.getDeviceList(uniqueCode,query);
        if(list!=null && list.size()>0){
            Map<String,Integer> deviceTypeMap = realTimeDataDao.queryDeviceType(uniqueCode);
            //根据设备类型取得该设备类型有多少信号点
            for(DeviceModel model:list){
                String deviceType = model.getTypeCode();
                if(deviceTypeMap.containsKey(deviceType)){
                    model.setCountSignal(deviceTypeMap.get(deviceType));
                }
            }
        }
        int count = realTimeDataDao.getDeviceCount(uniqueCode, query);
        ListResult<DeviceModel> value = new ListResult(list,count);
        return value;
    }
}
