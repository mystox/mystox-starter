package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备资产 相关数据查询类
 * Created by Eric on 2020/2/12.
 */
@Repository
public class DeviceMongo {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 获取单个站点下所有设备
     */
    public List<DeviceEntity> findAllDevicesBySiteCode(String uniqueCode, String siteCode){
        Criteria criteria = new Criteria();
        if (siteCode != null){
            criteria.and("siteCode").is(siteCode);
        }

        return mongoTemplate.find(new Query(criteria), DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * 根据查询条件，获取设备列表
     */
    public List<DeviceEntity> findDeviceList(String uniqueCode, DeviceQuery deviceQuery){
        // TODO: 2020/3/9
        return new ArrayList<>();
    }

    /**
     * 统计设备数量
     */
    public Integer countDeviceShortCode(String uniqueCode, String preCode) {
        Criteria criteria = Criteria.where("code").regex("^" + preCode + ".*?");
        Query query = new Query(criteria);
        return (int)mongoTemplate.count(query, DeviceEntity.class,
                uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * 通过设备编码查找设备
     */
    public DeviceEntity findDeviceByShortCode(String uniqueCode, String deviceCode) {
        return mongoTemplate.findOne(
                new Query(Criteria.where("code").is(deviceCode)),
                DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:获取设备实体类表，不包含名字，无语远程调用
     */
    public List<DeviceEntity> listEntity(String uniqueCode, DeviceQuery deviceQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, deviceQuery);
        Query query = Query.query(criteria);
        query.skip( (deviceQuery.getCurrentPage()-1) * deviceQuery.getPageSize() ).limit(deviceQuery.getPageSize());
        return mongoTemplate.find(query, DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }

    /**
     * @auther: liudd
     * @date: 2020/2/28 9:48
     * 功能描述:统计设备列表数量
     */
    public int countEntity(String uniqueCode, DeviceQuery deviceQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, deviceQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, uniqueCode + CollectionSuffix.DEVICE);
    }

    Criteria baseCriteria(Criteria criteria, DeviceQuery deviceQuery){
        List<String> siteCodeList = deviceQuery.getSiteCodes();
        if(null != siteCodeList){
            criteria.and("siteCode").in(siteCodeList);
        }
        String operationState = deviceQuery.getOperationState();
        if(!StringUtil.isNUll(operationState)){
            criteria.and("operationState").is(operationState);
        }
        String deviceTypeCode = deviceQuery.getDeviceTypeCode();
        if(!StringUtil.isNUll(deviceTypeCode)){
            criteria.and("typeCode").is(deviceTypeCode);
        }
        return criteria;
    }

    /**
     * @param uniqueCode
     * @param siteCodeList
     * @auther: liudd
     * @date: 2020/3/6 9:36
     * 功能描述:根据站点编码列表获取设备列表，不需要设备名称，无需远程调用
     */
    public List<DeviceEntity> getBySiteCodeList(String uniqueCode, List<String> siteCodeList) {
        Criteria criteria = Criteria.where("siteCode").in(siteCodeList);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }
}
