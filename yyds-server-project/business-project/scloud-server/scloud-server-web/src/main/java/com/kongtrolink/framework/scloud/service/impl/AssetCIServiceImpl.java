package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.AssetTypeConstant;
import com.kongtrolink.framework.scloud.constant.OperaCodeConstant;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicSiteEntity;
import com.kongtrolink.framework.scloud.mqtt.query.BasicSiteQuery;
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
     * 从【中台-资管】 获取站点（基本信息）
     *
     * @param uniqueCode
     * @param siteQuery
     */
    @Override
    public MsgResult getSiteCI(String uniqueCode, SiteQuery siteQuery) {
        BasicSiteQuery basicSiteQuery = new BasicSiteQuery();
        basicSiteQuery.setServerCode(siteQuery.getServerCode());
        basicSiteQuery.setEnterpriseCode(uniqueCode);
        basicSiteQuery.setName(AssetTypeConstant.ASSET_TYPE_SITE);
        basicSiteQuery.setAddress(siteQuery.getTierCodes());
        basicSiteQuery.setSn(siteQuery.getSiteCode());
        basicSiteQuery.setSiteName(siteQuery.getSiteName());
        basicSiteQuery.setSiteType(siteQuery.getSiteType());

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_CI, JSON.toJSONString(basicSiteQuery));
        return msgResult;
    }

    /**
     * 向【中台-资管】 添加站点
     */
    @Override
    public MsgResult addSiteCI(String uniqueCode, SiteModel siteModel) {
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

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.ADD_CI, JSON.toJSONString(basicSiteEntityList));
        return msgResult;
    }

    /**
     * 向【中台-资管】 删除站点
     */
    @Override
    public MsgResult deleteSiteCI(String uniqueCode, SiteQuery siteQuery) {
        List<String> siteCodes = siteQuery.getSiteCodes();
        List<BasicSiteEntity> basicSiteEntityList = new ArrayList<>();
        for (String siteCode : siteCodes) {
            BasicSiteEntity basicSiteEntity = new BasicSiteEntity();
            basicSiteEntity.setServerCode(siteQuery.getServerCode());
            basicSiteEntity.setUniqueCode(uniqueCode);
            basicSiteEntity.setCode(siteCode);

            basicSiteEntityList.add(basicSiteEntity);
        }

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.DELETE_CI, JSON.toJSONString(basicSiteEntityList));
        return msgResult;
    }

    /**
     * 向【中台-资管】 修改站点
     */
    @Override
    public MsgResult modifySiteCI(String uniqueCode, SiteModel siteModel) {
        BasicSiteEntity basicSiteEntity = new BasicSiteEntity();
        basicSiteEntity.setServerCode(siteModel.getServerCode());
        basicSiteEntity.setUniqueCode(uniqueCode);
        basicSiteEntity.setAssetType(AssetTypeConstant.ASSET_TYPE_SITE);
        basicSiteEntity.setCode(siteModel.getCode());
        basicSiteEntity.setName(siteModel.getName());

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.MODIFY_CI, JSON.toJSONString(basicSiteEntity));
        return msgResult;
    }

    /**
     * 从【中台-资管】 获取设备（基本信息）
     */
    @Override
    public MsgResult getDeviceCI() {
        return null;
    }

    /**
     * 向【中台-资管】 添加设备
     */
    @Override
    public MsgResult addDeviceCI() {
        return null;
    }

    /**
     * 向【中台-资管】 删除设备
     */
    @Override
    public MsgResult deleteDeviceCI() {
        return null;
    }

    /**
     * 向【中台-资管】 修改设备
     */
    @Override
    public MsgResult modifyDeviceCI() {
        return null;
    }
}
