package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.constant.ScloudBusinessOperate;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.*;
import com.kongtrolink.framework.scloud.constant.RedisKey;
import com.kongtrolink.framework.scloud.constant.StationType;
import com.kongtrolink.framework.scloud.dao.RealTimeDataDao;
import com.kongtrolink.framework.scloud.entity.Device;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.entity.Site;
import com.kongtrolink.framework.scloud.entity.model.SignalModel;
import com.kongtrolink.framework.scloud.entity.realtime.SignalDiInfo;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.realtime.SignalDiInfoKo;
import com.kongtrolink.framework.scloud.entity.realtime.SignalInfoEntity;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SignalDiInfoQuery;
import com.kongtrolink.framework.scloud.query.SignalQuery;
import com.kongtrolink.framework.scloud.service.RealTimeDataService;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Autowired
    MqttOpera mqttOpera;
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
     * 实时数据-获取改设备的实时数据
     *
     * @param uniqueCode 企业编码
     */
    @Override
    public SignalModel getData(String uniqueCode,SignalQuery signalQuery) {
        try{
            String devType = signalQuery.getDeviceType();//设备类型
            DeviceType deviceType = realTimeDataDao.queryDeviceType(uniqueCode,devType);
            if(deviceType==null || deviceType.getSignalTypeList()==null || deviceType.getSignalTypeList().size()==0){
                return new SignalModel();
            }
            GetDataMessage getDataMessage = getGetDataMessage(signalQuery,deviceType.getSignalTypeList());
            //下发到网关获取实时数据
            MsgResult result = mqttOpera.opera(ScloudBusinessOperate.GET_DATA,JSONObject.toJSONString(getDataMessage));
            String ack = result.getMsg();//消息返回内容
            GetDataAckMessage getDataAckMessage = JSONObject.parseObject(ack,GetDataAckMessage.class);
            SignalModel signalModel = getSignalModel(signalQuery.getFsuCode(),signalQuery.getType(),getDataAckMessage,deviceType.getSignalTypeList());
            return signalModel;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取实时数据
     * 整理返回下前段展现 并存放到redis中
     */
    private SignalModel getSignalModel(String fsuCode,String type,GetDataAckMessage getDataAckMessage,List<SignalType> signalTypeList){
        SignalModel signalModel = new SignalModel();
        signalModel.setReportTime(new Date().getTime());
        Map<String,List<SignalInfoEntity>> infoList = new HashMap<>();//前端返回值
        if(type!=null){
            List<SignalInfoEntity> typeInfoList = new ArrayList<>();
            infoList.put(type,typeInfoList);
        }
        Map<String,Object> valueMap = null ;//存放信号点数据 key:cntbId value:值
        if(getDataAckMessage!=null
                && getDataAckMessage.getPayload()!=null
                && getDataAckMessage.getPayload().getDeviceIds() !=null){
            for(DeviceIdInfo deviceIdInfo:getDataAckMessage.getPayload().getDeviceIds()){
                List<SignalIdInfo> ids = deviceIdInfo.getIds();
                String redisKey = fsuCode+"#"+deviceIdInfo.getDeviceId();
                Object value = redisUtils.hget(RedisKey.DEVICE_REAL_DATA,redisKey);
                try{
                    if(value==null){
                        valueMap = new HashMap<>();
                    }else{
                        valueMap = JSONObject.parseObject(String.valueOf(value));
                    }
                    if(ids !=null){
                        for(SignalIdInfo signalIdInfo:ids){
                            valueMap.put(signalIdInfo.getId(),signalIdInfo.getValue());
                        }
                    }
                    //更新redis里面的值
                    redisUtils.hset(RedisKey.DEVICE_REAL_DATA,redisKey,valueMap);
                }catch (Exception e){
                    LOGGER.error("获取redis数据异常 {} ,{} ",RedisKey.DEVICE_REAL_DATA,redisKey);
                }
                for(SignalType signalType:signalTypeList){
                    if(type!=null && !type.equals(signalType.getType())){
                        //判断查询的是指定类型的信号点类型
                        continue;
                    }
                    Object realData = valueMap.get(signalType.getCntbId());
                    SignalInfoEntity signalInfoEntity = new SignalInfoEntity();
                    signalInfoEntity.init(signalType,realData);
                    if(infoList.containsKey(type)){
                        List<SignalInfoEntity> typeInfoList = infoList.get(type);
                        typeInfoList.add(signalInfoEntity);
                        infoList.put(type,typeInfoList);
                    }else{
                        List<SignalInfoEntity> typeInfoList = new ArrayList<>();
                        typeInfoList.add(signalInfoEntity);
                        infoList.put(type,typeInfoList);
                    }
                }
                signalModel.setInfoList(infoList);
            }
        }
        return signalModel;
    }
    /**
     * 获取实时数据
     * 整理返回下发网关的传递参数
     */
    private GetDataMessage getGetDataMessage(SignalQuery signalQuery,List<SignalType> signalTypeList){
        GetDataMessage getDataMessage = new GetDataMessage();
        DeviceIdEntity payload = new DeviceIdEntity();
        List<DeviceIdInfo> deviceIds = new ArrayList<>();
        String type = signalQuery.getType();
        DeviceIdInfo deviceIdInfo = new DeviceIdInfo(signalQuery.getDeviceCode());
        if(type !=null && !"".equals(type)){
            List<SignalIdInfo> ids = new ArrayList<>();
            for(SignalType signalType:signalTypeList){
                if(type.equals(signalType.getType())){
                    SignalIdInfo signalIdInfo = new SignalIdInfo();
                    signalIdInfo.setId(signalType.getCntbId());
                    ids.add(signalIdInfo);
                }
            }
            deviceIdInfo.setIds(ids);
        }
        deviceIds.add(deviceIdInfo);
        payload.setDeviceIds(deviceIds);
        getDataMessage.setFsuId(signalQuery.getFsuCode());
        getDataMessage.setPayload(payload);
        return getDataMessage;
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
