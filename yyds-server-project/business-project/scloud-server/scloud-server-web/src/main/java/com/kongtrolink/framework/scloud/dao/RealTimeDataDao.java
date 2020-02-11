package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
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



    private Criteria getDeviceCriteria(DeviceQuery deviceQuery){
        String siteId = deviceQuery.getSiteId();
        String fsuId = deviceQuery.getFsuId();
        String systemName = deviceQuery.getSystemName();
        String name = deviceQuery.getDeviceName();
        String code = deviceQuery.getDeviceCode();
        String type = deviceQuery.getDeviceType();
        String typeCode = deviceQuery.getDeviceTypeCode();
        // 默认条件（无实际作用）
        Criteria criteria = Criteria.where("BASE_CONDITION").exists(false);
        if (siteId != null) {
            criteria = criteria.and("siteId").is(siteId);
        }
        if (fsuId != null) {
            criteria = criteria.and("fsuId").is(fsuId);
        }
        if (systemName != null) {
            criteria.and("systemName").is(systemName);
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
}
