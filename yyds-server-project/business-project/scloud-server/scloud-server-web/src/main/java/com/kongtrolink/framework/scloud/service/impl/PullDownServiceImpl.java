package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.PullDownDao;
import com.kongtrolink.framework.scloud.dao.RealTimeDataDao;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.PullDownQuery;
import com.kongtrolink.framework.scloud.service.PullDownService;
import com.kongtrolink.framework.scloud.service.RealTimeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PullDownServiceImpl implements PullDownService {
    @Autowired
    PullDownDao pullDownDao;
    @Autowired
    RealTimeDataDao realTimeDataDao;
    /**
     * 设备类型 下拉框
     *
     * @param uniqueCode 企业编码
     * @return 设备类型列表
     */
    @Override
    public List<DeviceType> getDeviceTypeList(String uniqueCode) {
        return pullDownDao.getDeviceTypeList(uniqueCode);
    }

    /**
     * 根据设备类型 获取该设备下的信号点列表
     *
     * @param uniqueCode 企业编码
     * @param query deviceType 设备类型
     * @param query type       遥测等类型
     * @return 信号点列表
     */
    @Override
    public List<SignalType> getSignalTypeList(String uniqueCode, PullDownQuery query) {
        return pullDownDao.getSignalTypeList(uniqueCode, query.getDeviceType(), query.getType());
    }

}
