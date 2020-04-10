package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.home.HomeFsuNumber;
import com.kongtrolink.framework.scloud.entity.model.home.HomeFsuOnlineModel;
import com.kongtrolink.framework.scloud.entity.model.home.HomeQuery;
import com.kongtrolink.framework.scloud.entity.model.home.HomeWorkModel;

import java.util.List;

/**
 * 首页接口
 */
public interface HomePageService {
    /**
     * 首页 - 站点数量(交维态/所有)
     * @param uniqueCode 企业编码
     * @param userId   站点ID
     * @param homeQuery  区域信息
     * @return 返回
     */
    HomeFsuNumber getHomeFsuNumber(String uniqueCode, String userId, HomeQuery homeQuery);
    /**
     * 根据区域 用户权限获取站点列表
     * @param uniqueCode 企业编码
     * @param userId 用户ID
     * @param homeQuery 区域
     * @return 站点总数
     */
    List<SiteEntity> getHomeSiteList(String uniqueCode, String userId, HomeQuery homeQuery);

    /**
     * 告警工单统计
     * @param uniqueCode 企业编码
     * @param userId  用户ID
     * @param homeQuery  区域
     * @return 站点总数
     */
    List<HomeWorkModel> getHomeWorkModel(String uniqueCode, String userId, HomeQuery homeQuery);

    /**
     * FSU在线状态统计 交维态FSU设备的实时在线情况百分比
     * @param uniqueCode 企业编码
     * @param userId  用户ID
     * @param homeQuery  区域
     * @return 站点总数
     */
    List<HomeFsuOnlineModel> getHomeFsuOnlineModel(String uniqueCode, String userId, HomeQuery homeQuery);

}
