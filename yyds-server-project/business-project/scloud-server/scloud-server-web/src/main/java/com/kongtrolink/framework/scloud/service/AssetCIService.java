package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.mqtt.query.BasicCommonQuery;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SiteQuery;

/**
 * 【中台-资管】相关CI操作 接口类
 * Created by Eric on 2020/3/3.
 */
public interface AssetCIService {

    //-----------------------站点----------------------
    /**
     * 从【中台-资管】 获取区域下所有站点（基本信息）
     */
    MsgResult getAssetSitesInTier(String uniqueCode, SiteQuery siteQuery);

    /**
     * 从【中台-资管】获取单个站点（基本信息）
     */
    MsgResult getAssetSiteByCode(String uniqueCode, SiteQuery siteQuery);

    /**
     * 向【中台-资管】 添加站点
     */
    MsgResult addAssetSite(String uniqueCode, SiteModel siteModel);

    /**
     * 向【中台-资管】 删除站点
     */
    MsgResult deleteAssetSite(String uniqueCode, SiteQuery siteQuery);

    /**
     * 向【中台-资管】 修改站点
     */
    MsgResult modifyAssetSite(String uniqueCode, SiteModel siteModel);


    //-----------------------设备----------------------
    /**
     * 从【中台-资管】 获取站点下设备（基本信息）列表
     */
    MsgResult getAssetDeviceList(String uniqueCode, DeviceQuery deviceQuery);

    /**
     * 向【中台-资管】 添加设备
     */
    MsgResult addAssetDevice();

    /**
     * 向【中台-资管】 删除设备
     */
    MsgResult deleteAssetDevice();

    /**
     * 向【中台-资管】 修改设备
     */
    MsgResult modifyAssetDevice();
}
