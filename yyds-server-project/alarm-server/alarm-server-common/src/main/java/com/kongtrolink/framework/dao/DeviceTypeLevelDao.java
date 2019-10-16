package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.query.DeviceTypeLevelQuery;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 16:44
 * @Description:
 */
@Repository
public class DeviceTypeLevelDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = MongTable.DEVICETYPE_LEVEL;

    public void add(DeviceTypeLevel deviceTypeLevel) {
        mongoTemplate.save(deviceTypeLevel, table);
    }

    public boolean delete(String deviceTypeLevelId) {
        Criteria criteria = Criteria.where("_id").is(deviceTypeLevelId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN()>0 ? true : false;
    }

    public boolean update(DeviceTypeLevel deviceTypeLevel) {
        Criteria criteria = Criteria.where("_id").is(deviceTypeLevel.getId());
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        boolean result = remove.getN() > 0 ? true : false;
        if(result){
            mongoTemplate.save(deviceTypeLevel, table);
        }
        return result;
    }

    /**
     * @param deviceTypeLevelId
     * @auther: liudd
     * @date: 2019/9/26 13:47
     * 功能描述:根据id获取
     */
    public DeviceTypeLevel get(String deviceTypeLevelId) {
        Criteria criteria = Criteria.where("_id").is(deviceTypeLevelId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, DeviceTypeLevel.class, table);
    }

    public List<DeviceTypeLevel> list(DeviceTypeLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        Query query = Query.query(criteria);
        int currentPage = levelQuery.getCurrentPage();
        int pageSize = levelQuery.getPageSize();
        query.skip( (currentPage-1)*pageSize ).limit(pageSize);
        return mongoTemplate.find(query, DeviceTypeLevel.class, table);
    }

    public int count(DeviceTypeLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, levelQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, table);
    }

    Criteria baseCriteria(Criteria criteria, DeviceTypeLevelQuery levelQuery){
        String id = levelQuery.getId();
        if(!StringUtil.isNUll(id)){
            criteria.and("_id").is(id);
        }
        String enterpriseCode = levelQuery.getEnterpriseCode();
        if(!StringUtil.isNUll(enterpriseCode)){
            criteria.and("enterpriseCode").is(enterpriseCode);
        }
        String serverCode = levelQuery.getServerCode();
        if(!StringUtil.isNUll(serverCode)){
            criteria.and("serverCode").is(serverCode);
        }
        String deviceType = levelQuery.getDeviceType();
        if(!StringUtil.isNUll(deviceType)){
            criteria.and("deviceType").is(deviceType);
        }
        String deviceModel = levelQuery.getDeviceModel();
        if(!StringUtil.isNUll(deviceModel)){
            criteria.and("deviceModel").is(deviceModel);
        }
        String level = levelQuery.getLevel();
        if(!StringUtil.isNUll(level)){
            criteria.and("levels").is(level);
        }
        return criteria;
    }

    public DeviceTypeLevel getOne(DeviceTypeLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, levelQuery);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, DeviceTypeLevel.class, table);
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @auther: liudd
     * @date: 2019/10/16 16:13
     * 功能描述:根据企业信息获取所有设备等级
     */
    public List<DeviceTypeLevel> listByEnterpriseInfo(String enterpriseCode, String serverCode) {
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, DeviceTypeLevel.class, table);
    }
}
