package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.dao.AlarmBusinessDao;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.FilterRule;
import com.kongtrolink.framework.scloud.query.AlarmBusinessQuery;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.service.AlarmBusinessService;
import com.kongtrolink.framework.scloud.service.FilterRuleService;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    @Autowired
    FilterRuleService filterRuleService;

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
        combineFilter(uniqueCode, alarmBusinessQuery);
        String table = CollectionSuffix.CUR_ALARM_BUSINESS;
        if(BaseConstant.ALARM_TYPE_HISTORY.equals(alarmBusinessQuery.getType())){
            table = CollectionSuffix.HIS_ALARM_BUSINESS;
        }
        return businessDao.list(uniqueCode, table, alarmBusinessQuery);
    }

    @Override
    public int count(String uniqueCode, AlarmBusinessQuery alarmBusinessQuery) {
        combineFilter(uniqueCode, alarmBusinessQuery);
        String table = CollectionSuffix.CUR_ALARM_BUSINESS;
        if(BaseConstant.ALARM_TYPE_HISTORY.equals(alarmBusinessQuery.getType())){
            table = CollectionSuffix.HIS_ALARM_BUSINESS;
        }
        return businessDao.count(uniqueCode, table, alarmBusinessQuery);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/14 10:38
     * 功能描述:合并告警过滤条件
     */
    //liuddtodo 需要先根据前端选取站点层级和用户权限，获取用户站点编码列表,再获取用户管辖范围设备编码列表，现假定前端将用户所管辖站点全部传递给后台
    private void combineFilter(String uniqueCode, AlarmBusinessQuery alarmQuery){
        //累加告警过滤功能
        FilterRule filterRule = filterRuleService.getUserInUse(uniqueCode, alarmQuery.getOperateUserId());
        if(null != filterRule){
            //比较时间
            Date startBeginTime = filterRule.getStartBeginTime();
            if(null != startBeginTime){
                Date sourStartBeginTime = alarmQuery.getStartBeginTime();
                if(null == sourStartBeginTime){
                    alarmQuery.setStartBeginTime(startBeginTime);
                }else{
                    if(startBeginTime.getTime() > sourStartBeginTime.getTime()){
                        alarmQuery.setStartBeginTime(startBeginTime);
                    }
                }
            }
            Date startEndTime = filterRule.getStartEndTime();
            if(null != startEndTime){
                Date sourEndTime = alarmQuery.getStartEndTime();
                if(null == sourEndTime){
                    alarmQuery.setStartEndTime(startEndTime);
                }else{
                    if(startEndTime.getTime() < sourEndTime.getTime()){
                        alarmQuery.setStartEndTime(startEndTime);
                    }
                }
            }
            Date clearBeginTime = filterRule.getClearBeginTime();
            if(null != clearBeginTime){
                Date sourBeginTime = alarmQuery.getClearBeginTime();
                if(null == sourBeginTime){
                    alarmQuery.setClearBeginTime(clearBeginTime);
                }else{
                    if(clearBeginTime.getTime() > sourBeginTime.getTime()){
                        alarmQuery.setClearBeginTime(clearBeginTime);
                    }
                }
            }
            Date clearEndTime = filterRule.getClearEndTime();
            if(null != clearEndTime){
                Date sourEndTime = alarmQuery.getClearEndTime();
                if(null == sourEndTime){
                    alarmQuery.setClearEndTime(clearEndTime);
                }else{
                    if(clearEndTime.getTime() < sourEndTime.getTime()){
                        alarmQuery.setClearEndTime(clearEndTime);
                    }
                }
            }
            //告警等级
            List<Integer> alarmLevelList = filterRule.getAlarmLevelList();
            if(null != alarmLevelList){
                List<Integer> levelList = alarmQuery.getLevelList();
                if(null != levelList){
                    alarmLevelList.retainAll(levelList);
                }
                alarmQuery.setLevelList(alarmLevelList);
//                Integer level = alarmQuery.getLevel();
//                if(null == level){
//                    alarmQuery.setLevelList(alarmLevelList);
//                }else{
//                    if(alarmLevelList.contains(level)){
//
//                    }else{
//                        alarmQuery.setLevel(level + 52014);
//                    }
//                }

            }
            //告警名称
            String alarmName = filterRule.getAlarmName();
            if(!StringUtil.isNUll(alarmName)){  //告警过滤规则中有告警名称
                String name = alarmQuery.getName();
                if(StringUtil.contant(name, alarmName)){

                }else if(StringUtil.contant(alarmName, name)){
                    alarmQuery.setName(alarmName);
                }else{
                    alarmQuery.setName(name + "特殊字符@特殊字符" + alarmName); //  如果告警列表与告警过滤的告警名称排斥，必然无法获取到任何告警数据
                }
            }
            //比对设备类型
            List<String> deviceTypeList = filterRule.getDeviceTypeList();
            if(null != deviceTypeList){
                String deviceType = alarmQuery.getDeviceType();
                if(StringUtil.isNUll(deviceType)){
                    alarmQuery.setDeviceTypeList(deviceTypeList);
                }else{
                    if(!deviceTypeList.contains(deviceType)){
                        alarmQuery.setDeviceType(deviceType + "特殊类型@特殊类型");
                    }
                }
            }
            //根据告警过滤规则，获取站点编码列表，与告警列表传递的站点列表取交集
            if(filterRule.isBaseSite()){
                List<String> siteCodeList  = filterRule.getSiteCodeList();
                List<String> sourSiteCodeList = alarmQuery.getSiteCodeList();
                sourSiteCodeList.retainAll(siteCodeList);
                alarmQuery.setSiteCodeList(sourSiteCodeList);
            }else{
                //基于区域，根据区域编码获取所有站点
                alarmQuery.setTierCodeList(filterRule.getTierCodeList());
            }
        }
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
