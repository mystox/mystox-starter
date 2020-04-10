package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.HomePageMongo;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.home.HomeFsuNumber;
import com.kongtrolink.framework.scloud.entity.model.home.HomeFsuOnlineModel;
import com.kongtrolink.framework.scloud.entity.model.home.HomeQuery;
import com.kongtrolink.framework.scloud.entity.model.home.HomeWorkModel;
import com.kongtrolink.framework.scloud.service.HomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Mag
 **/
@Service
public class HomePageServiceImpl implements HomePageService {
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
        List<SiteEntity> list = getHomeSiteList(uniqueCode,userId,homeQuery);
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
    public List<SiteEntity> getHomeSiteList(String uniqueCode, String userId, HomeQuery homeQuery) {
        return homePageMongo.getHomeSiteList(uniqueCode,userId,homeQuery);
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
