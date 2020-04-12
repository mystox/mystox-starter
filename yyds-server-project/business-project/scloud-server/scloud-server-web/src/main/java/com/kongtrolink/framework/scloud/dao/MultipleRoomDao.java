package com.kongtrolink.framework.scloud.dao;


import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.multRoom.RoomDeviceType;
import com.kongtrolink.framework.scloud.entity.multRoom.RoomSignalType;
import com.kongtrolink.framework.scloud.entity.multRoom.RoomSignalTypeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 综合机房
 * by Mag on 6/21/2018.
 */
@Repository
public class MultipleRoomDao {

    @Autowired
    MongoTemplate mongoTemplate;
    /**
     * 查询站点信息
     */
    public SiteEntity findSite(String uniqueCode, int siteId) {
        return mongoTemplate.findOne(
                new Query(Criteria.where("id").is(siteId)),
                SiteEntity.class, uniqueCode + CollectionSuffix.SITE);
    }
    /**
     * 获取 基本信息 中的 监控点设备数 和监控点数
     * @param uniqueCode 企业编码
     * @param siteId     站点ID
     * @return RoomSiteInfo
     */
    public int getSiteInfoNum(String uniqueCode, int siteId) {
        Query query = new Query(Criteria.where("siteId").is(siteId));
        return (int)mongoTemplate.count(query,uniqueCode+ CollectionSuffix.DEVICE);
    }

    /**
     * 获取 基本信息 中的 监控点设备数 和监控点数
     * @param uniqueCode 企业编码
     * @param siteId     站点ID
     * @return RoomSiteInfo
     */
    public List<DeviceEntity> getDeviceList(String uniqueCode, int siteId) {
        Query query = new Query(Criteria.where("siteId").is(siteId));
        return mongoTemplate.find(query,DeviceEntity.class,uniqueCode+ CollectionSuffix.DEVICE);
    }

    /**
     * 保存自定义信号点
     *
     * @param uniqueCode 企业编码
     * @param config 信号点信息
     */
    public void addShowSignalConfig(String uniqueCode, RoomSignalTypeConfig config) {
         mongoTemplate.save(config,uniqueCode+ CollectionSuffix.MULTIPLE_ROOM_SIGNAL_CONFIG);
    }

    public  void delShowSignalConfig(String uniqueCode,int deviceId){
        Criteria criteria = Criteria.where("deviceId").is(deviceId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query,uniqueCode+CollectionSuffix.MULTIPLE_ROOM_SIGNAL_CONFIG);
    }
    /**
     * 根据设备查询信号点配置
     *
     * @param uniqueCode 企业编码
     * @param deviceId 信号点信息
     */
    public RoomSignalTypeConfig queryRoomSignalTypeConfig(String uniqueCode, int deviceId) {
        Criteria criteria = Criteria.where("deviceId").is(deviceId);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query,RoomSignalTypeConfig.class,uniqueCode+CollectionSuffix.MULTIPLE_ROOM_SIGNAL_CONFIG);
    }

    /**
     * 根据企业编码-设备类型 获取 要展现的信号点数据
     * @param uniqueCode 企业编码
     * @param deviceType 设备类型
     * @return 信号点类型
     */
    public RoomDeviceType getShowSignal(String uniqueCode, String deviceType){
        Criteria criteria = Criteria.where("uniqueCode").is(uniqueCode).and("code").is(deviceType);
        return mongoTemplate.findOne(new Query(criteria), RoomDeviceType.class, CollectionSuffix.MULTIPLE_ROOM_SIGNAL);
    }

    /**
     * 查询设备
     */
    public DeviceEntity findDeviceById(String uniqueCode, int deviceId) {
        Criteria criteria = Criteria.where("id").is(deviceId);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }

    public List<RoomDeviceType> queryRoomDeviceType(String uniqueCode) {
        Query query = new Query();
        List<RoomDeviceType> list = mongoTemplate.find(query,RoomDeviceType.class,uniqueCode + CollectionSuffix.SIGNAL_TYPE);
        return  list;
    }

    public void saveRoomDeviceType(RoomDeviceType roomDeviceType) {
        mongoTemplate.save(roomDeviceType,CollectionSuffix.MULTIPLE_ROOM_SIGNAL);
    }

    public  int countRoomDeviceType(String uniqueCode){
        Criteria criteria = Criteria.where("uniqueCode").is(uniqueCode);
        return (int)mongoTemplate.count(new Query(criteria), RoomDeviceType.class, CollectionSuffix.MULTIPLE_ROOM_SIGNAL);
    }

    public  void removeRoomDeviceType(String uniqueCode){
        Criteria criteria = Criteria.where("uniqueCode").is(uniqueCode);
        mongoTemplate.remove(new Query(criteria), CollectionSuffix.MULTIPLE_ROOM_SIGNAL);
    }


    public Map<String, Integer> deviceAlarmCountMap(String uniqueCode, String siteCode){
        Map<String, Integer> map = new HashMap<>();
        Criteria criteria = Criteria.where("siteCode").is(siteCode).and("state").is("待处理").and("shield").ne(true);
        Query query = Query.query(criteria);
        List<AlarmBusiness> alarmList = mongoTemplate.find(query, AlarmBusiness.class, uniqueCode + CollectionSuffix.CUR_ALARM_BUSINESS);
        for(AlarmBusiness alarm : alarmList){
            String deviceCode = alarm.getDeviceCode();
            int num = 0;
            if(map.containsKey(deviceCode)){
                num = map.get(deviceCode);
            }
            num = num + 1 ;
            map.put(deviceCode,num);
        }
        return map;
    }
    /**
     * 功能描述:根据设备类型编码集合获取RoomDeviceType列表
     */
    public List<RoomDeviceType> getByDeviceTypeList(String uniqueCode, List<String> deviceTypeCodeList){
        Criteria criteria = Criteria.where("uniqueCode").is(uniqueCode).and("code").in(deviceTypeCodeList);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, RoomDeviceType.class, CollectionSuffix.MULTIPLE_ROOM_SIGNAL);
    }
    /**
     * @auther: liudd
     * @date: 2018/7/27 10:54
     * 功能描述:设备类型集合转换成设备编码类型，信号类型map集合
     */
    public Map<String, List<RoomSignalType>> deviceTypeList2CodeSignalMap(List<RoomDeviceType> roomDeviceTypeList){
        Map<String, List<RoomSignalType>> map = new HashMap<>();
        if(null != roomDeviceTypeList){
            for(RoomDeviceType roomDeviceType : roomDeviceTypeList){
                map.put(roomDeviceType.getCode(), roomDeviceType.getSignalTypeList());
            }
        }
        return map;
    }
}
