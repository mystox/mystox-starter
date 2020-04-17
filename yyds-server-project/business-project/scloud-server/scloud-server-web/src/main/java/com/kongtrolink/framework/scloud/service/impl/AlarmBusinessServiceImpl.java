package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.dao.AlarmBusinessDao;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.AlarmBusinessQuery;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.AlarmBusinessService;
import com.kongtrolink.framework.scloud.service.FilterRuleService;
import com.kongtrolink.framework.scloud.service.SiteService;
import com.kongtrolink.framework.scloud.util.DateUtil;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Autowired
    SiteService siteService;

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
        FilterRule filterRule = filterRuleService.getUserInUse(uniqueCode, alarmQuery.getOperatorId());
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
                if(null != sourSiteCodeList) {
                    sourSiteCodeList.retainAll(siteCodeList);
                    alarmQuery.setSiteCodeList(sourSiteCodeList);
                }else{
                    alarmQuery.setSiteCodeList(siteCodeList);
                }
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

    /**
     * @auther: liudd
     * @date: 2020/4/9 17:31
     * 功能描述:告警消除
     */
    @Override
    public List<Statistics> countLevel(String uniqueCode, String table, AlarmBusinessQuery businessQuery) {
        return businessDao.countLevel(uniqueCode, table, businessQuery);
    }

    /**
     * @param uniqueCode
     * @param businessQuery
     * @auther: liudd
     * @date: 2020/4/14 15:59
     * 功能描述:告警频发站点统计
     */
    @Override
    public List<Statistics> alarmSiteTopHistory(String uniqueCode, AlarmBusinessQuery businessQuery) {
        List<Statistics> alarmSiteStatisticsList = businessDao.siteDateCount(uniqueCode, CollectionSuffix.HIS_ALARM_BUSINESS, businessQuery);
        //存储站点编码--统计实体列表map
        Map<String, List<Statistics>> siteCodeStatisticsListMap = new HashMap<>();
        for(Statistics alarmSiteStatistics : alarmSiteStatisticsList){
            String siteCode = alarmSiteStatistics.getCode();
            List<Statistics> siteCodeStatisticsList = siteCodeStatisticsListMap.get(siteCode);
            if(null == siteCodeStatisticsList){
                siteCodeStatisticsList = new ArrayList<>();
            }
            siteCodeStatisticsList.add(alarmSiteStatistics);
            siteCodeStatisticsListMap.put(siteCode, siteCodeStatisticsList);
        }

        Map<String, Statistics> siteCodeStatisticsMap = new HashMap<>();
        Date startBeginTime = businessQuery.getStartBeginTime();
        Date startEndTime = businessQuery.getStartEndTime();
        for(long i= startBeginTime.getTime(); i< startEndTime.getTime();){
            String timeStr = DateUtil.getInstance().format(startBeginTime, "yyyy-MM-dd");
            for(String key : siteCodeStatisticsListMap.keySet()){
                List<Statistics> siteCodeStatisticsList = siteCodeStatisticsListMap.get(key);
                Statistics alarmSiteStatistics = siteCodeStatisticsMap.get(key);
                if(alarmSiteStatistics == null){
                    alarmSiteStatistics = new Statistics();
                    alarmSiteStatistics.setCode(siteCodeStatisticsList.get(0).getCode());
                    alarmSiteStatistics.setProperties(new ArrayList<>());
                    alarmSiteStatistics.setValues(new ArrayList<>());
                    alarmSiteStatistics.getProperties().add("站点名称");
                    alarmSiteStatistics.getValues().add("null");
                    alarmSiteStatistics.getProperties().add("告警总数");
                    alarmSiteStatistics.getValues().add("0");
                }
                int count = 0;
                Iterator<Statistics> iterator = siteCodeStatisticsList.iterator();
                while(iterator.hasNext()){
                    Statistics next = iterator.next();
                    if(timeStr.equals(next.getName())){
                        count = next.getCount();
                        iterator.remove();
                    }
                }
                alarmSiteStatistics.getProperties().add(timeStr);
                alarmSiteStatistics.getValues().add(count+"");
                alarmSiteStatistics.setCount(alarmSiteStatistics.getCount() + count);
                alarmSiteStatistics.getValues().set(1, alarmSiteStatistics.getCount()+"");
                siteCodeStatisticsMap.put(key, alarmSiteStatistics);
            }
            i= i+(24*60*60*1000);
            startBeginTime = new Date(i);
        }
        List<Statistics> list = new ArrayList<>();
        list.addAll(siteCodeStatisticsMap.values());
        List<String> siteCodeList = new ArrayList<>();
        siteCodeList.addAll(siteCodeStatisticsMap.keySet());
        SiteQuery siteQuery = new SiteQuery();
        siteQuery.setServerCode(businessQuery.getServerCode());
        siteQuery.setSiteCodes(siteCodeList);
        List<SiteModel> siteList = siteService.findSiteList(uniqueCode, siteQuery);
        for(SiteModel siteModel : siteList){
            String code = siteModel.getCode();
            Statistics alarmSiteStatistics = siteCodeStatisticsMap.get(code);
            if(null != alarmSiteStatistics){
                alarmSiteStatistics.getValues().set(0, siteModel.getName());
            }
            siteCodeStatisticsMap.remove(code);
        }
        boolean connFailt = siteCodeStatisticsMap.size()==list.size()? true : false;
        String connFailtInfo = "资管通讯失败";
        for(String key : siteCodeStatisticsMap.keySet()){
            Statistics alarmSiteStatistics = siteCodeStatisticsMap.get(key);
            alarmSiteStatistics.getValues().set(0, "资管不存在该设备");
            if(connFailt) {
                alarmSiteStatistics.getValues().set(0, connFailtInfo);
            }
        }
        return list;
    }

    /**
     * @param uniqueCode
     * @param businessQuery
     * @auther: liudd
     * @date: 2020/4/15 11:19
     * 功能描述:告警分布
     */
    @Override
    public List<Statistics> getAlarmDistributeList(String uniqueCode, AlarmBusinessQuery businessQuery) {
        List<Statistics> alarmDistributeList = businessDao.getAlarmDistributeList(uniqueCode, businessQuery);
        String tierCodePrefix = businessQuery.getTierCodePrefix();
        if(!StringUtil.isNUll(tierCodePrefix) && tierCodePrefix.length() == 6){
            //从资管获取站点名称
            List<String> siteCodeList = new ArrayList<>();
            Map<String, Statistics> siteCodeStatisticsMap = new HashMap<>();
            for(Statistics statistics : alarmDistributeList){
                siteCodeList.add(statistics.getCode());
                siteCodeStatisticsMap.put(statistics.getCode(), statistics);
            }
            SiteQuery siteQuery = new SiteQuery();
            siteQuery.setServerCode(businessQuery.getServerCode());
            siteQuery.setSiteCodes(siteCodeList);
            List<SiteModel> siteList = siteService.findSiteList(uniqueCode, siteQuery);
            for(SiteModel siteModel : siteList){
                String code = siteModel.getCode();
                Statistics alarmSiteStatistics = siteCodeStatisticsMap.get(code);
                if(null != alarmSiteStatistics){
                    alarmSiteStatistics.setName(siteModel.getName());
                }
                siteCodeStatisticsMap.remove(code);
            }
            boolean connFailt = siteCodeStatisticsMap.size()==alarmDistributeList.size()? true : false;
            String connFailtInfo = "资管通讯失败";
            for(String key : siteCodeStatisticsMap.keySet()){
                Statistics alarmSiteStatistics = siteCodeStatisticsMap.get(key);
                alarmSiteStatistics.setName("资管不存在该设备");
                if(connFailt) {
                    alarmSiteStatistics.setName(connFailtInfo);
                }
            }
        }
        return alarmDistributeList;
    }
}
