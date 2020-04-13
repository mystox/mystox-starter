package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.WorkConstants;
import com.kongtrolink.framework.scloud.entity.*;
import com.mongodb.WriteResult;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 16:55
 * @Description:
 */
@Repository
public class JobWorkDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table_work_alarm_config = "_work_alarm_config";
    private String table_work_config = "_work_config";
    private String table_work = "_work";
    private String tabel_work_record = "_work_record";

    public List<WorkAlarmConfig> getAllWorkAlarmConfig(String uniqueCode, Date curDate) {
        Criteria criteria = Criteria.where("sendTime").lte(curDate);
        criteria.and("sendType").is(WorkConstants.SEND_TYPE_AUTO);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, WorkAlarmConfig.class, uniqueCode + table_work_alarm_config);
    }

    public WorkConfig getWorkConfigById(String uniqueCode, String workConfigId){
        return mongoTemplate.findById(workConfigId, WorkConfig.class, uniqueCode + table_work_config);
    }

    /**
     * @param uniqueCode
     * @param deviceCode
     * @auther: liudd
     * @date: 2020/4/8 17:12
     * 功能描述:获取该设备未回单的告警工单
     */
    public Work getNoOverWorkByDeviceCode(String uniqueCode, String deviceCode) {
        Criteria criteria = Criteria.where("device.strId").is(deviceCode);
        List<String> stateList = new ArrayList<>();
        stateList.add(WorkConstants.STATE_RECEIVE);
        stateList.add(WorkConstants.STATE_HANDLER);
        criteria.and("state").in(stateList);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Work.class, uniqueCode + table_work);
    }

    public void addWork(String uniqueCode, Work work) {
        mongoTemplate.save(work, uniqueCode + table_work);
    }

    boolean deleteWork(String uniqueCode, String workId){
        Criteria criteria = Criteria.where("_id").is(workId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, uniqueCode + table_work);
        return remove.getN()>0 ? true : false;
    }

    public boolean updateWork(String uniqueCode, Work work) {
        if(deleteWork(uniqueCode, work.getId())){
            addWork(uniqueCode, work);
            return true;
        }
        return false;
    }

    public void addWorkRecord(String uniqueCode, WorkRecord workRecord) {
        mongoTemplate.save(workRecord, uniqueCode + tabel_work_record);
    }

    public void deleteWorkAlarmConfigById(String uniqueCode, String workAlarmConfigId) {
        Criteria criteria = Criteria.where("_id").is(workAlarmConfigId);
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, uniqueCode + table_work_alarm_config);
    }

    /**
     * @param uniqueCode
     * @param alarmBusiness
     * @auther: liudd
     * @date: 2020/4/11 15:03
     * 功能描述:修改告警的工单编码
     */
    public boolean updateAlarmWorkCode(String uniqueCode, AlarmBusiness alarmBusiness) {
        Criteria criteria = Criteria.where("key").is(alarmBusiness.getKey());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("workCode", alarmBusiness.getWorkCode());
        WriteResult result = mongoTemplate.updateFirst(query, update, uniqueCode + alarmBusiness);
        return result.getN()>0 ? true : false;
    }
}
