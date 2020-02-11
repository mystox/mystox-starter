package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.constant.RedisKey;
import com.kongtrolink.framework.scloud.constant.StationType;
import com.kongtrolink.framework.scloud.dao.RealTimeDataDao;
import com.kongtrolink.framework.scloud.entity.Device;
import com.kongtrolink.framework.scloud.entity.Site;
import com.kongtrolink.framework.scloud.entity.realtime.SignalDiInfo;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.realtime.SignalDiInfoKo;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SignalDiInfoQuery;
import com.kongtrolink.framework.scloud.service.RealTimeDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据监控 - 实时数据
 * @author Mag
 */
@Service
public class RealTimeDataServiceImpl implements RealTimeDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeDataServiceImpl.class);

    @Autowired
    RealTimeDataDao realTimeDataDao;
    @Autowired
    RedisUtils redisUtils;
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
    /**
     * 根据查询 某一个遥测信号值列表
     *
     * @param uniqueCode 企业唯一吗
     * @param query      查询参数
     * @return 信号值列表
     */
    @Override
    public List<SignalDiInfo> getSignalDiInfo(String uniqueCode, SignalDiInfoQuery query) {
        List<SignalDiInfo> list = new ArrayList<>();
        //根据区域数查询获取站点ID列表和局站类型 封装在SignalDiInfoKo对象中
        SignalDiInfoKo ko = getDeviceList(uniqueCode,query);
        Map<String, Site> siteMap =ko.getSiteMap();//局站类型Map
        List<String> siteIds = ko.getSiteIds();//站点ID列表
        String signalCntbId = query.getSignalCode(); //遥测信号点ID
        List<Device> deviceList = realTimeDataDao.findDeviceDiList(uniqueCode,siteIds,query);
        for(Device device:deviceList){
            SignalDiInfo info = new SignalDiInfo();
            int siteId = device.getSiteId();
            String deviceCode = device.getCode();
            String fsuCode = device.getFsuCode();
            if(siteMap.containsKey(String.valueOf(siteId))){
                Site site = siteMap.get(siteId);
                info.setTier(site.getTierString());
                info.setSiteCode(site.getCode());
                info.setSiteName(site.getName());
                info.setSiteType(site.getCompany());
            }
            info.setDeviceName(device.getName());
            info.setDeviceCode(device.getCode());
            String redisKey = fsuCode+"#"+deviceCode;
            Object value = redisUtils.hget(RedisKey.DEVICE_REAL_DATA,redisKey);
            try{
                Map<String,Object> redisValue = JSONObject.parseObject(String.valueOf(value));
                if(redisValue!=null && redisValue.containsKey(signalCntbId)){
                    info.setValue(String.valueOf(redisValue.get(signalCntbId)));
                }
            }catch (Exception e){
                LOGGER.error("获取redis数据异常 {} ,{} ",RedisKey.DEVICE_REAL_DATA,redisKey);
            }
            list.add(info);
        }

        return list;
    }
    /**
     * 根据查询 某一个遥测信号值列表 - 取得总数
     *
     * @param uniqueCode 企业唯一吗
     * @param query      查询参数
     * @return 信号值列表
     */
    @Override
    public int getSignalDiInfoNum(String uniqueCode, SignalDiInfoQuery query) {
        SignalDiInfoKo ko = getDeviceList(uniqueCode,query);
        List<String> siteIds = ko.getSiteIds();
        return realTimeDataDao.findDeviceDiCount(uniqueCode,siteIds,query);
    }

    /**
     * 根据 区域数 返回需要查询的站点ID列表

     */
    private SignalDiInfoKo getDeviceList(String uniqueCode, SignalDiInfoQuery query){
        Map<String,Site> siteMap = new HashMap<>();
        List<String> siteIds = new ArrayList<>();
        List<Site> siteList = realTimeDataDao.findSite(uniqueCode,query);
        for(Site site:siteList){
            if(!siteMap.containsKey(site.getId())) {
                siteMap.put(String.valueOf(site.getId()), site);
            }
            site.setCompany(StationType.toValue(site.getStationType()));//暂存这个局站类型
            siteIds.add(String.valueOf(site.getId()));
        }
        SignalDiInfoKo ko = new SignalDiInfoKo();
        ko.setSiteIds(siteIds);
        ko.setSiteMap(siteMap);
        return ko;
    }

}
