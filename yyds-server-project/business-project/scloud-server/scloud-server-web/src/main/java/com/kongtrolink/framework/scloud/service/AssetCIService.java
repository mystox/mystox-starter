package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.SiteQuery;

/**
 * 【中台-资管】相关CI操作 接口类
 * Created by Eric on 2020/3/3.
 */
public interface AssetCIService {

    //-----------------------站点----------------------
    /**
     * 从【中台-资管】 获取站点（基本信息）
     */
    MsgResult getSiteCI(String uniqueCode, SiteQuery siteQuery);

    /**
     * 向【中台-资管】 添加站点
     */
    MsgResult addSiteCI(String uniqueCode, SiteModel siteModel);

    /**
     * 向【中台-资管】 删除站点
     */
    MsgResult deleteSiteCI(String uniqueCode, SiteQuery siteQuery);

    /**
     * 向【中台-资管】 修改站点
     */
    MsgResult modifySiteCI(String uniqueCode, SiteModel siteModel);


    //-----------------------设备----------------------
    /**
     * 从【中台-资管】 获取设备（基本信息）
     */
    MsgResult getDeviceCI();

    /**
     * 向【中台-资管】 添加设备
     */
    MsgResult addDeviceCI();

    /**
     * 向【中台-资管】 删除设备
     */
    MsgResult deleteDeviceCI();

    /**
     * 向【中台-资管】 修改设备
     */
    MsgResult modifyDeviceCI();
}
