package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.WorkConstants;
import com.kongtrolink.framework.scloud.entity.*;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private String table_cur_alarm_business = "_current_alarm_business";

    public List<WorkAlarmConfig> getAllWorkAlarmConfig(String uniqueCode, Date curDate) {
        Criteria criteria = Criteria.where("sendWorkTime").lte(curDate);
        criteria.and("sendWorkType").is(WorkConstants.SEND_TYPE_AUTO);
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
        criteria.and("status").in(stateList);
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

    public void deleteWorkAlarmConfigById(String workAlarmConfigId) {
        Criteria criteria = Criteria.where("_id").is(workAlarmConfigId);
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, table_work_alarm_config);
    }

    /**
     * @param uniqueCode
     * @param alarmBusiness
     * @auther: liudd
     * @date: 2020/4/8 19:05
     * 功能描述:添加告警业务信息.如果告警消除时，该告警还未派单，则删除告警工单配置信息。杜绝派单时告警已消除情况
     */
    public void addAlarmBusiness(String uniqueCode, AlarmBusiness alarmBusiness) {
        mongoTemplate.save(alarmBusiness, uniqueCode + table_cur_alarm_business);
    }
}
