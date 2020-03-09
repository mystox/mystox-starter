package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.AssetTypeConstant;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.constant.OperaCodeConstant;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicSiteEntity;
import com.kongtrolink.framework.scloud.mqtt.query.BasicCommonQuery;
import com.kongtrolink.framework.scloud.mqtt.query.BasicDeviceQuery;
import com.kongtrolink.framework.scloud.mqtt.query.BasicSiteQuery;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.AssetCIService;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 【中台-资管】相关CI操作 接口实现类
 * Created by Eric on 2020/3/3.
 */
@Service
public class AssetCIServiceImpl implements AssetCIService{
    @Autowired
    MqttOpera mqttOpera;

    /**
     * 从【中台-资管】 获取区域下所有站点（基本信息）
     *
     * @param uniqueCode
     * @param siteQuery
     */
    @Override
    public MsgResult getAssetSitesInTier(String uniqueCode, SiteQuery siteQuery) {
        BasicSiteQuery basicSiteQuery = new BasicSiteQuery();
        basicSiteQuery.setServerCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, siteQuery.getServerCode()));
        basicSiteQuery.setEnterpriseCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, uniqueCode));
        basicSiteQuery.setType(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, AssetTypeConstant.ASSET_TYPE_SITE));

        if (siteQuery.getTierCodes() != null) {
            basicSiteQuery.setAddress(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_IN, siteQuery.getTierCodes()));
        }
        if (siteQuery.getSiteCode() != null && !siteQuery.getSiteCode().equals("")) {
            basicSiteQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_FUZZY, siteQuery.getSiteCode()));  //模糊搜索
        }
        if (siteQuery.getSiteName() != null && !siteQuery.getSiteName().equals("")) {
            basicSiteQuery.setSiteName(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_FUZZY, siteQuery.getSiteName()));    //模糊搜索
        }
        if (siteQuery.getSiteType() != null) {
            basicSiteQuery.setSiteType(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, siteQuery.getSiteType()));
        }

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_CI_SCLOUD, JSON.toJSONString(basicSiteQuery));
        return msgResult;
    }

    /**
     * 从【中台-资管】通过站点Code获取单个站点（基本信息）
     *
     * @param uniqueCode
     * @param siteQuery
     */
    @Override
    public MsgResult getAssetSiteByCode(String uniqueCode, SiteQuery siteQuery) {
        BasicSiteQuery basicSiteQuery = new BasicSiteQuery();
        basicSiteQuery.setServerCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, siteQuery.getServerCode()));
        basicSiteQuery.setEnterpriseCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, uniqueCode));
        basicSiteQuery.setType(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, AssetTypeConstant.ASSET_TYPE_SITE));
        if (siteQuery.getSiteCode() != null && !siteQuery.getSiteCode().equals("")) {
            basicSiteQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, siteQuery.getSiteCode()));
        }
        if (siteQuery.getSiteCodes() != null && siteQuery.getSiteCodes().size() > 0){
            basicSiteQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_IN, siteQuery.getSiteCodes()));
        }

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_CI_SCLOUD, JSON.toJSONString(basicSiteQuery));
        return msgResult;
    }

    /**
     * 向【中台-资管】 添加站点
     */
    @Override
    public MsgResult addAssetSite(String uniqueCode, SiteModel siteModel) {
        List<BasicSiteEntity> basicSiteEntityList = new ArrayList<>();

        BasicSiteEntity basicSiteEntity = new BasicSiteEntity();
        basicSiteEntity.setServerCode(siteModel.getServerCode());
        basicSiteEntity.setUniqueCode(uniqueCode);
        basicSiteEntity.setTierCode(siteModel.getTierCode());
        basicSiteEntity.setAssetType(AssetTypeConstant.ASSET_TYPE_SITE);
        basicSiteEntity.setCode(siteModel.getCode());
        basicSiteEntity.setName(siteModel.getName());
        basicSiteEntity.setSiteType(siteModel.getSiteType());
        basicSiteEntityList.add(basicSiteEntity);

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.ADD_CI_SCLOUD, JSON.toJSONString(basicSiteEntityList));
        return msgResult;
    }

    /**
     * 向【中台-资管】 删除站点
     */
    @Override
    public MsgResult deleteAssetSite(String uniqueCode, SiteQuery siteQuery) {
        List<String> siteCodes = siteQuery.getSiteCodes();
        List<BasicSiteEntity> basicSiteEntityList = new ArrayList<>();
        for (String siteCode : siteCodes) {
            BasicSiteEntity basicSiteEntity = new BasicSiteEntity();
            basicSiteEntity.setServerCode(siteQuery.getServerCode());
            basicSiteEntity.setUniqueCode(uniqueCode);
            basicSiteEntity.setCode(siteCode);

            basicSiteEntityList.add(basicSiteEntity);
        }

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.DELETE_CI_SCLOUD, JSON.toJSONString(basicSiteEntityList));
        return msgResult;
    }

    /**
     * 向【中台-资管】 修改站点
     */
    @Override
    public MsgResult modifyAssetSite(String uniqueCode, SiteModel siteModel) {
        BasicSiteEntity basicSiteEntity = new BasicSiteEntity();
        basicSiteEntity.setServerCode(siteModel.getServerCode());
        basicSiteEntity.setUniqueCode(uniqueCode);
        basicSiteEntity.setAssetType(AssetTypeConstant.ASSET_TYPE_SITE);
        basicSiteEntity.setCode(siteModel.getCode());
        basicSiteEntity.setName(siteModel.getName());

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.MODIFY_CI_SCLOUD, JSON.toJSONString(basicSiteEntity));
        return msgResult;
    }

    /**
     * 从【中台-资管】 获取站点下设备（基本信息）列表
     */
    @Override
    public MsgResult getAssetDeviceList(String uniqueCode, DeviceQuery deviceQuery) {
        BasicDeviceQuery basicDeviceQuery = new BasicDeviceQuery();
        basicDeviceQuery.setServerCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, deviceQuery.getServerCode()));
        basicDeviceQuery.setEnterpriseCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, uniqueCode));
        basicDeviceQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_IN, deviceQuery.getDeviceCodes()));
        if (deviceQuery.getDeviceName() != null && !deviceQuery.getDeviceName().equals("")) {
            basicDeviceQuery.setDeviceName(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_FUZZY, deviceQuery.getDeviceName()));
        }
        if (deviceQuery.getModel() != null && !deviceQuery.getModel().equals("")) {
            basicDeviceQuery.setModel(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_FUZZY, deviceQuery.getModel()));
        }

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_CI_SCLOUD, JSON.toJSONString(basicDeviceQuery));
        return msgResult;
    }

    /**
     * 从【中台-资管】 获取单个设备（基本信息）
     */
    @Override
    public MsgResult getAssetDeviceByCode(String uniqueCode, DeviceQuery deviceQuery) {
        BasicDeviceQuery basicDeviceQuery = new BasicDeviceQuery();
        basicDeviceQuery.setServerCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, deviceQuery.getServerCode()));
        basicDeviceQuery.setEnterpriseCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, uniqueCode));
        basicDeviceQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, deviceQuery.getDeviceCode()));

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_CI_SCLOUD, JSON.toJSONString(basicDeviceQuery));
        return msgResult;
    }

    /**
     * 向【中台-资管】 添加设备
     */
    @Override
    public MsgResult addAssetDevice() {
        return null;
    }

    /**
     * 向【中台-资管】 删除设备
     */
    @Override
    public MsgResult deleteAssetDevice() {
        return null;
    }

    /**
     * 向【中台-资管】 修改设备
     */
    @Override
    public MsgResult modifyAssetDevice() {
        return null;
    }
}
