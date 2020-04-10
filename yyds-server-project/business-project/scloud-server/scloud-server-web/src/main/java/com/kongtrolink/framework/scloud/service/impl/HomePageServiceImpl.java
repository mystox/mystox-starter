package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.HomePageMongo;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.home.*;
import com.kongtrolink.framework.scloud.service.HomePageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Mag
 **/
@Service
public class HomePageServiceImpl implements HomePageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePageServiceImpl.class);
    @Autowired
    private HomePageMongo homePageMongo;
    /**
     * 首页 - 站点数量(交维态/所有)
     *
     * @param uniqueCode 企业编码
     * @param userId     站点ID
     * @param homeQuery       区域信息
     * @return 返回
     */
    @Override
    public HomeFsuNumber getHomeFsuNumber(String uniqueCode, String userId, HomeQuery homeQuery) {
        //统计交维态的站点总数
        HomeFsuNumber homeFsuNumber = homePageMongo.getHomeFsuNumber(uniqueCode,userId,homeQuery);
        List<HomeSiteAlarmMap> list = getHomeSiteList(uniqueCode,userId,homeQuery);
        int total = list.size();
        homeFsuNumber.setTotal(total);
        return homeFsuNumber;
    }

    /**
     * 根据区域 用户权限获取站点列表
     *
     * @param uniqueCode 企业编码
     * @param userId     用户ID
     * @param homeQuery  区域
     * @return 站点总数
     */
    @Override
    public List<HomeSiteAlarmMap> getHomeSiteList(String uniqueCode, String userId, HomeQuery homeQuery) {
        return homePageMongo.getHomeSiteList(uniqueCode,userId,homeQuery);
    }

    /**
     * 首页 - 实时告警统计 只统计交维态
     *
     * @param uniqueCode 企业编码
     * @param userId     用户ID
     * @param homeQuery  区域
     * @return 实时告警统计 只统计交维态
     */
    @Override
    public HomeAlarmLevelNum getHomeAlarmLevelNum(String uniqueCode, String userId, HomeQuery homeQuery) {
        //根据区域 权限查询 交维态的站点code list
        List<HomeSiteInfo> siteList =  homePageMongo.getOperationSiteList(uniqueCode,userId,homeQuery);
        if(siteList==null){
            LOGGER.error("根据用户 userId:{} 未查询该企业:{} 下相关权限的 交维态的FSU",userId,uniqueCode);
            return new HomeAlarmLevelNum();
        }
        List<String> siteCodes = new ArrayList<>();
        for(HomeSiteInfo siteInfo:siteList){
            siteCodes.add(siteInfo.getSiteCode());
        }
        Map<String,Integer> levelMap = new HashMap<>(); //对应告警级别的数量
        int confirm = 0;//确认告警数量
        int unConfirm = 0;//未确认告警数量
        List<HomeReportModel> reportModels = homePageMongo.getHomeAlarmLevelNum(uniqueCode,siteCodes);
        if(reportModels!=null){
            for(HomeReportModel reportModel:reportModels){
                String level = reportModel.getLevel();
                String check = reportModel.getCheckState();//确认状态
                int num = 0;
                if(levelMap.containsKey(level)){
                    num = levelMap.get(level);
                }
                num = num + reportModel.getCount();
                levelMap.put(level,num);
                if("已确认".equals(check)){
                    confirm = confirm + reportModel.getCount();
                }else{
                    unConfirm = unConfirm + reportModel.getCount();
                }
            }
        }
        HomeAlarmLevelNum value = new HomeAlarmLevelNum();
        value.setConfirm(confirm);
        value.setLevelMap(levelMap);
        value.setUnConfirm(unConfirm);
        return value;
    }

    /**
     * 首页 站点地图
     *
     * @param uniqueCode 企业编码
     * @param userId     用户ID
     * @param homeQuery  区域
     * @return 站点信息
     */
    @Override
    public List<HomeSiteAlarmMap> getHomeSiteAlarmMap(String uniqueCode, String userId, HomeQuery homeQuery) {
        List<HomeSiteAlarmMap> list = homePageMongo.getHomeSiteList(uniqueCode,userId,homeQuery);
        //统计告警相关信息
        List<HomeReportModel> alarmReportList = homePageMongo.getHomeReportModel(uniqueCode,userId,homeQuery);
        Map<String,List<HomeReportModel>> map = new HashMap<>();
        if(alarmReportList!=null){
            for(HomeReportModel homeReportModel:alarmReportList){
                String siteCode = homeReportModel.getSiteCode();
                List<HomeReportModel> siteAlarmList = new ArrayList<>();
                if(map.containsKey(siteCode)){
                    siteAlarmList = map.get(siteCode);
                }
                siteAlarmList.add(homeReportModel);
                map.put(siteCode,siteAlarmList);
            }
        }
        //查询站点交维态信息
        List<HomeFsuOnlineInfo> stateList = homePageMongo.getHomeFsuStateList(uniqueCode,userId,homeQuery);
        Map<String,List<HomeFsuOnlineInfo>> stateMap = new HashMap<>();
        if(stateList!=null){
            for(HomeFsuOnlineInfo onlineInfo:stateList){
                String siteCode = onlineInfo.getSiteCode();
                List<HomeFsuOnlineInfo> fsuList = new ArrayList<>();
                if(map.containsKey(siteCode)){
                    fsuList = stateMap.get(siteCode);
                }
                fsuList.add(onlineInfo);
                stateMap.put(siteCode,fsuList);
            }
        }
        for(HomeSiteAlarmMap siteInfo:list){
            siteInfo = initHomeSiteAlarmMap(siteInfo,map.get(siteInfo.getCode()));
            siteInfo.setFsuOnlineInfo(stateMap.get(siteInfo.getCode()));
        }
        return list;
    }

    /**
     * 统计 站点告警数
     */
    private HomeSiteAlarmMap initHomeSiteAlarmMap(HomeSiteAlarmMap homeSiteAlarmMap,List<HomeReportModel> list){
        if(list==null || list.size()==0){
            return homeSiteAlarmMap;
        }
        int total = 0;
        Map<String,Integer> levelMap = new HashMap<>();
        for(HomeReportModel homeReportModel:list){
            total = total + homeReportModel.getCount();
            levelMap.put(homeReportModel.getLevel(),homeReportModel.getCount());
        }
        homeSiteAlarmMap.setAlarmNumber(total);
        homeSiteAlarmMap.setLevelMap(levelMap);
        return homeSiteAlarmMap;
    }
    /**
     * 告警工单统计
     *
     * @param uniqueCode 企业编码
     * @param userId     用户ID
     * @param homeQuery  区域
     * @return 站点总数
     */
    @Override
    public List<HomeWorkModel> getHomeWorkModel(String uniqueCode, String userId, HomeQuery homeQuery) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Date end = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -30); //设置为前30天
        Date start =  calendar.getTime();
        homeQuery.setEndTime(end);
        homeQuery.setStartTime(start);
        return homePageMongo.getHomeWorkModel(uniqueCode,userId,homeQuery);
    }

    /**
     * FSU在线状态统计 交维态FSU设备的实时在线情况百分比
     *
     * @param uniqueCode 企业编码
     * @param userId     用户ID
     * @param homeQuery  区域
     * @return 站点总数
     */
    @Override
    public List<HomeFsuOnlineModel> getHomeFsuOnlineModel(String uniqueCode, String userId, HomeQuery homeQuery) {
        return homePageMongo.getHomeFsuOnlineModel(uniqueCode,userId,homeQuery);
    }
}
