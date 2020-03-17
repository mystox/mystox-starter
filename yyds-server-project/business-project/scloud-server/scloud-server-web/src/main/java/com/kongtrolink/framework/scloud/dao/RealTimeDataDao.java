package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.FsuDeviceEntity;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SignalDiInfoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据监控 - 实时数据
 * @author Mag
 */
@Repository
public class RealTimeDataDao{

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 实时数据-获取设备列表 -分页信息
     */
    public List<DeviceModel> getDeviceList(String uniqueCode, DeviceQuery deviceQuery) {
        int currentPage = deviceQuery.getCurrentPage();
        int pageSize = deviceQuery.getPageSize();
        Criteria criteria = getDeviceCriteria(deviceQuery);
        Query query = new Query(criteria);
        // 根据设备类型升序排序
        query.with(new Sort(Sort.Direction.ASC, "code"));
        query.skip((currentPage - 1) * pageSize).limit(pageSize);
        return mongoTemplate.find(query, DeviceModel.class, uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * 实时数据-获取设备列表 -分页个数
     */
    public int getDeviceCount(String uniqueCode, DeviceQuery deviceQuery){
        Criteria criteria = getDeviceCriteria(deviceQuery);
        Query query = new Query(criteria);
        return (int)mongoTemplate.count(query, uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * 根据设备类型取得该设备类型有多少信号点
     */
    public Map<String,Integer> queryDeviceType(String uniqueCode){
        List<DeviceType> list = mongoTemplate.findAll(DeviceType.class, uniqueCode + CollectionSuffix.SIGNAL_TYPE);
        Map<String,Integer> map = new HashMap<>();
        if(list!=null){
            for(DeviceType deviceType:list){
                if(deviceType.getSignalTypeList()==null){
                    map.put(deviceType.getCode(),0);
                }else{
                    map.put(deviceType.getCode(),deviceType.getSignalTypeList().size());
                }
            }
        }
        return map;
    }
    public int getSignalCount(String uniqueCode,String deviceType){
        DeviceType typeInfo = queryDeviceType(uniqueCode,deviceType);
        if(typeInfo==null||typeInfo.getSignalTypeList()==null){
            return 0;
        }
        return typeInfo.getSignalTypeList().size();
    }
    /**
     * 根据设备类型取得该设备类型下的信号点信息
     */
    public DeviceType queryDeviceType(String uniqueCode,String deviceType){
        // 默认条件（无实际作用）
        Criteria criteria = Criteria.where("code").is(deviceType);
        Query query = new Query(criteria);
        return  mongoTemplate.findOne(query,DeviceType.class, uniqueCode + CollectionSuffix.SIGNAL_TYPE);
    }


    /**
     * 查询 条件 获取站点
     * @param uniqueCode 企业编码
     * @param param 查询条件
     * @return list
     */
    public List<SiteEntity> findSite(String uniqueCode, DeviceQuery param) {
        List<String> tierCodes = param.getTierCodes();// 区域codes
        List<Integer> siteIds = param.getSiteIds();
        Criteria criteria = new Criteria();
        if (tierCodes != null && tierCodes.size()>0) {
            if (siteIds != null && siteIds.size()>0) {
                criteria.orOperator(Criteria.where("tierCode").in(tierCodes),Criteria.where("id").in(siteIds));
            }else{
                criteria.and("tierCode").in(tierCodes);
            }
        }
        Query query = new Query(criteria);
        List<SiteEntity> siteEntityList = mongoTemplate.find(query, SiteEntity.class, uniqueCode + CollectionSuffix.SITE);
        return siteEntityList;
    }

    /**
     * 查询 条件
     * @param uniqueCode 企业编码
     * @param param 站点列表
     * @return list
     */
    public List<DeviceEntity> findDeviceDiList(String uniqueCode, List<Integer> siteIds, DeviceQuery param) {
        Criteria criteria = getDeviceDiCriteria(siteIds, param);
        int currentPage = param.getCurrentPage();
        int pageSize = param.getPageSize();
//        DBObject dbObject = new BasicDBObject();
//        DBObject fieldObject = new BasicDBObject();
//        fieldObject.put("_id", true);
//        fieldObject.put("name", true);
//        fieldObject.put("code", true);
//        Query query = new BasicQuery(dbObject, fieldObject);
//        query.addCriteria(criteria);
        Query query = new Query(criteria);
        query.skip((currentPage - 1) * pageSize).limit(pageSize);
        return mongoTemplate.find(query, DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }


    public int findDeviceDiCount(String uniqueCode,List<Integer> siteIds, DeviceQuery param) {
        Criteria criteria = getDeviceDiCriteria(siteIds, param);
        Query query = new Query(criteria);
        return (int)mongoTemplate.count(query, uniqueCode + CollectionSuffix.DEVICE);
    }

    private Criteria getDeviceDiCriteria(List<Integer> siteIds, DeviceQuery param){
        Criteria criteria = Criteria.where("siteId").in(siteIds).and("typeCode").is(param.getDeviceTypeCode());
        String deviceName = param.getDeviceName();//设备名称
        String deviceCode = param.getDeviceCode();//设备编码
        if(deviceName!=null && !"".equals(deviceName)){
            criteria.and("name").regex(".*?" + deviceName + ".*?");
        }
        if(deviceCode!=null && !"".equals(deviceCode)){
            criteria.and("code").regex(".*?" + deviceCode + ".*?");
        }
        return criteria;
    }

    private Criteria getDeviceCriteria(DeviceQuery deviceQuery){
        List<Integer> siteIds = deviceQuery.getSiteIds();
        String name = deviceQuery.getDeviceName();
        String code = deviceQuery.getDeviceCode();
        String type = deviceQuery.getDeviceType();
        String typeCode = deviceQuery.getDeviceTypeCode();
        // 默认条件（无实际作用）
        Criteria criteria = new Criteria();
        if (siteIds != null) {
            criteria = criteria.and("siteId").is(siteIds);
        }
        if (name != null) {
            criteria.and("name").regex(".*?" + name + ".*?");
        }
        if (code != null) {
            criteria.and("code").regex(".*?" + code + ".*?");
        }
        if (typeCode != null) {
            criteria.and("typeCode").is(typeCode);
        } else {
            if (type != null) {
                criteria.and("type").is(type);
            }
        }
        return criteria;
    }

    /**
     * 根据 设备code 获取 该设备所属的FSU
     *
     * @param uniqueCode 企业编码
     * @param deviceCode 设备code
     * @return FSU信息
     */
    public FsuDeviceEntity getFsuInfoByDeviceCode(String uniqueCode, String deviceCode) {
        Criteria criteria = Criteria.where("deviceCode").is(deviceCode);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query,FsuDeviceEntity.class,uniqueCode + CollectionSuffix.FSU_DEVICE);
    }

    /**
     * 根据 FSU CODE 获取 该设备信息
     * @param uniqueCode 企业编码
     * @param code FSU code
     * @return FSU信息
     */
    public DeviceEntity getFsuInfo(String uniqueCode, String code) {
        Criteria criteria = Criteria.where("code").is(code);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query,DeviceEntity.class,uniqueCode + CollectionSuffix.DEVICE);
    }
}
