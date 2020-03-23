package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.AssetTypeConstant;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.constant.OperaCodeConstant;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicDeviceEntity;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicParentEntity;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicSiteEntity;
import com.kongtrolink.framework.scloud.mqtt.query.BasicCommonQuery;
import com.kongtrolink.framework.scloud.mqtt.query.BasicDeviceQuery;
import com.kongtrolink.framework.scloud.mqtt.query.BasicParentQuery;
import com.kongtrolink.framework.scloud.mqtt.query.BasicSiteQuery;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.AssetCIService;
import com.kongtrolink.framework.scloud.service.DeviceService;
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
    @Autowired
    DeviceService deviceService;

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
            basicSiteQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_FUZZY, siteQuery.getSiteCode()));  //站点编码-模糊搜索
        }
        if (siteQuery.getSiteName() != null && !siteQuery.getSiteName().equals("")) {
            basicSiteQuery.setSiteName(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_FUZZY, siteQuery.getSiteName()));    //站点名称-模糊搜索
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
        if (siteQuery.getSiteCodes() != null && siteQuery.getSiteCodes().size() > 0){   //站点编码-IN搜索
            basicSiteQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_IN, siteQuery.getSiteCodes()));
        }else {
            if (siteQuery.getSiteCode() != null && !siteQuery.getSiteCode().equals("")) {   //站点编码-精确搜索
                basicSiteQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, siteQuery.getSiteCode()));
            }
        }
        if (siteQuery.getSiteName() != null && !siteQuery.getSiteName().equals("")) {
            basicSiteQuery.setSiteName(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_FUZZY, siteQuery.getSiteName()));    //站点名称模糊搜索
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
        if (deviceQuery.getDeviceCodes() != null) {
            basicDeviceQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_IN, deviceQuery.getDeviceCodes()));
        }
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
     * 从【中台-资管】获取站点下FSU 列表
     */
    @Override
    public MsgResult getAssetFsuList(String uniqueCode, DeviceQuery deviceQuery) {
        BasicDeviceQuery basicDeviceQuery = new BasicDeviceQuery();
        basicDeviceQuery.setServerCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, deviceQuery.getServerCode()));
        basicDeviceQuery.setEnterpriseCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, uniqueCode));
        basicDeviceQuery.setType(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, CommonConstant.DEVICE_TYPE_FSU));
        BasicParentQuery basicParentQuery = new BasicParentQuery();
        basicParentQuery.setType(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, AssetTypeConstant.ASSET_TYPE_SITE));    //父资产类型：site
        basicParentQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_IN, deviceQuery.getSiteCodes()));    //父资产SN：站点编码（集合）
        basicDeviceQuery.set_parent(basicParentQuery);

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_CI_SCLOUD, JSON.toJSONString(basicDeviceQuery));
        return msgResult;
    }

    /**
     * 从【中台-资管】获取FSU下的关联设备
     */
    @Override
    public MsgResult getRelatedDeviceList(String uniqueCode, DeviceQuery deviceQuery) {
        BasicDeviceQuery basicDeviceQuery = new BasicDeviceQuery();
        basicDeviceQuery.setServerCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, deviceQuery.getServerCode()));
        basicDeviceQuery.setEnterpriseCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, uniqueCode));

        BasicParentQuery basicParentQuery = new BasicParentQuery();
        basicParentQuery.setType(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, CommonConstant.DEVICE_TYPE_FSU));    //父资产类型：FSU动环主机
        basicParentQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, deviceQuery.getDeviceCode()));    //父资产SN：FSU编码
        basicDeviceQuery.set_parent(basicParentQuery);

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_CI_SCLOUD, JSON.toJSONString(basicDeviceQuery));
        return msgResult;
    }

    /**
     * 从【中台-资管】获取站点下未关联FSU的设备 列表
     */
    @Override
    public MsgResult getUnrelatedDeviceList(String uniqueCode, DeviceQuery deviceQuery) {
        BasicDeviceQuery basicDeviceQuery = new BasicDeviceQuery();
        basicDeviceQuery.setServerCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, deviceQuery.getServerCode()));
        basicDeviceQuery.setEnterpriseCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, uniqueCode));
        basicDeviceQuery.setType(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_IN, deviceQuery.getDeviceTypes()));    //所有非FSU动环主机设备类型

        BasicParentQuery basicParentQuery = new BasicParentQuery(); //父资产信息
        basicParentQuery.setType(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, AssetTypeConstant.ASSET_TYPE_SITE));    //父资产类型：site
        basicParentQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, deviceQuery.getSiteCode()));  //父资产SN：站点编码
        basicDeviceQuery.set_parent(basicParentQuery);

        BasicParentQuery basicNotParentQuery = new BasicParentQuery();  //未关联的资产信息
        basicNotParentQuery.setType(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, CommonConstant.DEVICE_TYPE_FSU)); //未关联资产类型：FSU动环主机
        basicDeviceQuery.set_notParent(basicNotParentQuery);

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_CI_SCLOUD, JSON.toJSONString(basicDeviceQuery));
        return msgResult;
    }

    /**
     * 向【中台-资管】 添加设备
     */
    @Override
    public MsgResult addAssetDevice(String uniqueCode, DeviceModel deviceModel) {
        List<BasicDeviceEntity> basicDeviceEntityList = new ArrayList<>();

        BasicDeviceEntity basicDeviceEntity = new BasicDeviceEntity();
        basicDeviceEntity.setServerCode(deviceModel.getServerCode());
        basicDeviceEntity.setUniqueCode(uniqueCode);
        basicDeviceEntity.setAssetType(deviceModel.getType());
        basicDeviceEntity.setCode(deviceModel.getCode());
        basicDeviceEntity.setDeviceName(deviceModel.getName());
        if (deviceModel.getModel() != null && !deviceModel.getModel().equals("")) {
            basicDeviceEntity.setModel(deviceModel.getModel());
        }
        BasicParentEntity basicParentEntity = new BasicParentEntity(AssetTypeConstant.ASSET_TYPE_SITE, deviceModel.getSiteCode(), CommonConstant.RELATIONSHIP_TYPE_INSTALL);
        basicDeviceEntity.set_parent(basicParentEntity);

        basicDeviceEntityList.add(basicDeviceEntity);

        if (deviceModel.getType().equals(CommonConstant.DEVICE_TYPE_FSU)){   //如果添加的是FSU主机设备，则自动添加个监控设备
            basicDeviceEntityList = autoAddMonitorDevice(uniqueCode, deviceModel, basicDeviceEntityList);
        }

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.ADD_CI_SCLOUD, JSON.toJSONString(basicDeviceEntityList));
        return msgResult;
    }

    //当添加FSU动环主机设备时，自动添加监控设备
    private List<BasicDeviceEntity> autoAddMonitorDevice(String uniqueCode, DeviceModel deviceModel, List<BasicDeviceEntity> list){
        BasicDeviceEntity basicDeviceEntity = new BasicDeviceEntity();
        basicDeviceEntity.setServerCode(deviceModel.getServerCode());
        basicDeviceEntity.setUniqueCode(uniqueCode);
        basicDeviceEntity.setAssetType("监控设备");

        DeviceModel model = new DeviceModel();
        model.setSiteCode(deviceModel.getSiteCode());
        model.setTypeCode("019");   //监控设备类型编码
        String monitorDeviceCode = deviceService.createDeviceCode(uniqueCode, model);
        basicDeviceEntity.setCode(monitorDeviceCode);

        basicDeviceEntity.setDeviceName(deviceModel.getName() + "_监控设备");  //监控设备名称规则：FSU名称+"_监控设备"
        BasicParentEntity basicParentEntity = new BasicParentEntity(AssetTypeConstant.ASSET_TYPE_SITE, deviceModel.getSiteCode(), CommonConstant.RELATIONSHIP_TYPE_INSTALL);
        basicDeviceEntity.set_parent(basicParentEntity);

        list.add(basicDeviceEntity);

        return list;
    }

    /**
     * 向【中台-资管】 删除设备
     */
    @Override
    public MsgResult deleteAssetDevice(String uniqueCode, DeviceQuery deviceQuery) {
        List<String> deviceCodes = deviceQuery.getDeviceCodes();
        List<BasicDeviceEntity> basicDeviceEntityList = new ArrayList<>();
        for (String deviceCode : deviceCodes){
            BasicDeviceEntity basicDeviceEntity = new BasicDeviceEntity();
            basicDeviceEntity.setServerCode(deviceQuery.getServerCode());
            basicDeviceEntity.setUniqueCode(uniqueCode);
            basicDeviceEntity.setCode(deviceCode);

            basicDeviceEntityList.add(basicDeviceEntity);
        }

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.DELETE_CI_SCLOUD, JSON.toJSONString(basicDeviceEntityList));
        return msgResult;
    }

    /**
     * 向【中台-资管】 修改设备
     */
    @Override
    public MsgResult modifyAssetDevice(String uniqueCode, DeviceModel deviceModel) {
        BasicDeviceEntity basicDeviceEntity = new BasicDeviceEntity();
        basicDeviceEntity.setServerCode(deviceModel.getServerCode());
        basicDeviceEntity.setUniqueCode(uniqueCode);
        basicDeviceEntity.setAssetType(deviceModel.getType());
        basicDeviceEntity.setCode(deviceModel.getCode());
        basicDeviceEntity.setDeviceName(deviceModel.getName());
        basicDeviceEntity.setModel(deviceModel.getModel());

        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.MODIFY_CI_SCLOUD, JSON.toJSONString(basicDeviceEntity));
        return msgResult;
    }
}
