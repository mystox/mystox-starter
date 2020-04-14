package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.dao.AlarmBusinessDao;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.query.AlarmBusinessQuery;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.service.AlarmBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 18:43
 * @Description:
 */
@Service
public class AlarmBusinessServiceImpl implements AlarmBusinessService{

    @Autowired
    AlarmBusinessDao businessDao;

    @Override
    public void add(String uniqueCode, String table, AlarmBusiness business) {
        businessDao.add(uniqueCode, table, business);
    }

    @Override
    public boolean add(String uniqueCode, String table, List<AlarmBusiness> businessList) {
        return businessDao.add(uniqueCode, table, businessList);
    }

    @Override
    public List<AlarmBusiness> listByKeyList(String uniqueCode, String table, List<String> keyList) {
        return businessDao.listByKeyList(uniqueCode, table, keyList);
    }

    @Override
    public boolean deleteByKeyList(String uniqueCode, String table, List<String> keyList) {
        return businessDao.deleteByKeyList(uniqueCode, table, keyList);
    }

    /**
     * @param unuqieCode
     * @param table
     * @param alarmBusiness
     * @auther: liudd
     * @date: 2020/4/9 11:15
     * 功能描述:根据key消除告警
     */
    @Override
    public boolean resolveByKey(String unuqieCode, String table, AlarmBusiness alarmBusiness) {
        return businessDao.resolveByKey(unuqieCode, table, alarmBusiness);
    }

    @Override
    public List<AlarmBusiness> list(String uniqueCode, AlarmBusinessQuery alarmBusinessQuery) {
        String table = CollectionSuffix.CUR_ALARM_BUSINESS;
        if(BaseConstant.ALARM_TYPE_HISTORY.equals(alarmBusinessQuery.getType())){
            table = CollectionSuffix.HIS_ALARM_BUSINESS;
        }
        return businessDao.list(uniqueCode, table, alarmBusinessQuery);
    }

    @Override
    public int count(String uniqueCode, AlarmBusinessQuery alarmBusinessQuery) {
        String table = CollectionSuffix.CUR_ALARM_BUSINESS;
        if(BaseConstant.ALARM_TYPE_HISTORY.equals(alarmBusinessQuery.getType())){
            table = CollectionSuffix.HIS_ALARM_BUSINESS;
        }
        return businessDao.count(uniqueCode, table, alarmBusinessQuery);
    }

    /**
     * @param uniqueCode
     * @param businessQuery
     * @auther: liudd
     * @date: 2020/4/9 16:26
     * 功能描述:告警确认
     */
    @Override
    public JSONObject check(String uniqueCode, String table, AlarmBusinessQuery businessQuery) {
        int result = businessDao.check(uniqueCode, table, businessQuery);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", businessQuery.getOperate()+"操作,成功"+result +"个");
        jsonObject.put("succ", true);
        return jsonObject;
    }

    @Override
    public JSONObject unCheck(String uniqueCode, String table, AlarmBusinessQuery businessQuery) {
        int result = businessDao.unCheck(uniqueCode, table, businessQuery);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", businessQuery.getOperate()+"操作,成功"+result +"个");
        jsonObject.put("succ", true);
        return jsonObject;
    }

    /**
     * @param uniqueCode
     * @param table
     * @param businessQuery
     * @auther: liudd
     * @date: 2020/4/9 17:31
     * 功能描述:告警消除
     */
    @Override
    public boolean resolve(String uniqueCode, String table, AlarmBusinessQuery businessQuery) {
        return businessDao.resolve(uniqueCode, table, businessQuery);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/9 18:37
     * 功能描述:接触告警消除，用于手动消除告警，远程失败后本地回滚
     */
    @Override
    public boolean unResolveByKeys(String uniqueCode, String table, List<String> keyList) {
        return businessDao.unResolveByKeys(uniqueCode, table, keyList);
    }
}
