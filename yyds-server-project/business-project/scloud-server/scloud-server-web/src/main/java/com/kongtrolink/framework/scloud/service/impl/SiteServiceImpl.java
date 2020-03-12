package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.dao.SiteMongo;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicSiteEntity;
import com.kongtrolink.framework.scloud.mqtt.entity.CIResponseEntity;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.AssetCIService;
import com.kongtrolink.framework.scloud.service.SiteService;
import com.kongtrolink.framework.scloud.util.ExcelUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 资产管理-站点资产管理 接口实现类
 * Created by Eric on 2020/2/12.
 */
@Service
public class SiteServiceImpl implements SiteService {

    @Autowired
    SiteMongo siteMongo;
    @Autowired
    MqttOpera mqttOpera;
    @Autowired
    AssetCIService assetCIService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteServiceImpl.class);

    /**
     * 生成站点编码
     */
    @Override
    public String createSiteCode(String uniqueCode, String tierCode) {
        String siteCode;

        //查询当前区域下站点数量,生成站点流水号。若siteCode已存在，则流水号再自增 1
        Integer num = siteMongo.countSiteInTier(uniqueCode, tierCode);
        do {
            num += 1;
            siteCode = tierCode + String.format("%04d", num);
        }while(checkCodeExisted(uniqueCode, siteCode));
        return siteCode;
    }

    /**
     * 检查站点编码是否重复
     */
    @Override
    public boolean checkCodeExisted(String uniqueCode, String siteCode) {
        return siteMongo.findSiteByCode(uniqueCode, siteCode) != null;
    }

    /**
     * 添加站点
     */
    @Override
    public void addSite(String uniqueCode, SiteModel siteModel) {
        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setTierCode(siteModel.getTierCode());
        siteEntity.setTierName(siteModel.getTierName());
        siteEntity.setCode(siteModel.getCode());
        siteEntity.setSiteType(siteModel.getSiteType());
        siteEntity.setCoordinate(siteModel.getCoordinate());
        siteEntity.setAddress(siteModel.getAddress());
        siteEntity.setRespName(siteModel.getRespName());
        siteEntity.setRespPhone(siteModel.getRespPhone());
        siteEntity.setTowerHeight(siteModel.getTowerHeight());
        siteEntity.setTowerType(siteModel.getTowerType());
        siteEntity.setShareInfo(siteModel.getShareInfo());
        siteEntity.setAssetNature(siteModel.getAssetNature());
        siteEntity.setCreateTime(siteModel.getCreateTime());
        siteEntity.setAreaCovered(siteModel.getAreaCovered());
        siteEntity.setFileId(siteModel.getFileId());

        //向【资管】下发添加（批量）站点的MQTT消息
        MsgResult msgResult = assetCIService.addAssetSite(uniqueCode, siteModel);
        int stateCode = msgResult.getStateCode();
        if (stateCode == CommonConstant.SUCCESSFUL){    //通信成功
            CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
            if (response.getResult() == CommonConstant.SUCCESSFUL) {    //请求成功
                LOGGER.info("【站点管理】,向【资管】发送添加站点MQTT 请求成功");
                //保存站点（扩展信息）
                siteMongo.addSite(uniqueCode, siteEntity);
            }else {
                LOGGER.error("【站点管理】,向【资管】发送添加站点MQTT 请求失败");
            }
        }else {
            LOGGER.error("【站点管理】,向【资管】发送添加站点MQTT 通信失败");
        }
    }

    /**
     * 获取所有站点列表
     */
    @Override
    public List<SiteModel> findSiteList(String uniqueCode, SiteQuery siteQuery) {
        List<SiteModel> list = new ArrayList<>();

        //从【资管】获取站点基本信息
        MsgResult msgResult = assetCIService.getAssetSitesInTier(uniqueCode, siteQuery);
        int stateCode = msgResult.getStateCode();
        if (stateCode == CommonConstant.SUCCESSFUL){    //通信成功
            CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
            if (response.getResult() == CommonConstant.SUCCESSFUL) {    //请求成功
                LOGGER.info("【站点管理】，从【资管】获取站点基本信息成功");
                List<String> siteCodes = new ArrayList<>();
                Map<String, BasicSiteEntity> map = new HashMap<>();
                for (JSONObject jsonObject : response.getInfos()) {
                    BasicSiteEntity basicSiteEntity = JSONObject.toJavaObject(jsonObject, BasicSiteEntity.class);
                    siteCodes.add(basicSiteEntity.getCode());
                    map.put(basicSiteEntity.getCode(), basicSiteEntity);    //key：code站点编码，value：站点基本信息
                }

                if (siteCodes.size() > 0) { //如果从资管中查出的站点不为空
                    siteQuery.setSiteCodes(siteCodes);
                    List<SiteEntity> siteEntityList = siteMongo.findSiteList(uniqueCode, siteQuery);
                    if (siteEntityList != null && siteEntityList.size() > 0) {
                        for (SiteEntity siteEntity : siteEntityList) {
                            SiteModel siteModel = new SiteModel();
                            siteModel.setSiteId(siteEntity.getId());
                            siteModel.setTierCode(siteEntity.getTierCode());
                            siteModel.setTierName(siteEntity.getTierName());
                            siteModel.setName(map.get(siteEntity.getCode()).getName());
                            siteModel.setCode(siteEntity.getCode());
                            siteModel.setSiteType(map.get(siteEntity.getCode()).getSiteType());
                            siteModel.setCoordinate(siteEntity.getCoordinate());
                            siteModel.setAddress(siteEntity.getAddress());
                            siteModel.setRespName(siteEntity.getRespName());
                            siteModel.setRespPhone(siteEntity.getRespPhone());
                            siteModel.setTowerHeight(siteEntity.getTowerHeight());
                            siteModel.setTowerType(siteEntity.getTowerType());
                            siteModel.setShareInfo(siteEntity.getShareInfo());
                            siteModel.setAssetNature(siteEntity.getAssetNature());
                            siteModel.setCreateTime(siteEntity.getCreateTime());
                            siteModel.setAreaCovered(siteEntity.getAreaCovered());
                            siteModel.setFileId(siteEntity.getFileId());

                            list.add(siteModel);
                        }
                    }
                }
            }else {
                LOGGER.error("【站点管理】，从【资管】获取站点 请求失败");
            }
        }else {
            LOGGER.error("【站点管理】，从【资管】获取站点 MQTT通信失败");
        }

        return list;
    }

    /**
     * 导出站点列表
     */
    @Override
    public HSSFWorkbook exportSiteList(List<SiteModel> list) {
        String[][] userSheet = getSiteListAsTable(list);

        HSSFWorkbook workBook = ExcelUtil.getInstance().createWorkBook(
                new String[] {"站点资产信息列表"}, new String[][][] { userSheet });
        return workBook;
    }

    /**
     * 修改站点
     */
    @Override
    public boolean modifySite(String uniqueCode, SiteModel siteModel) {
        Boolean isModified = siteModel.getModified();   //修改站点时，是否修改了站点名称
        boolean modifyResult = false;
        if (isModified) {   //如果修改了站点的基本属性(即站点名称)
            //向【资管】下发修改站点的MQTT消息
            MsgResult msgResult = assetCIService.modifyAssetSite(uniqueCode, siteModel);
            int stateCode = msgResult.getStateCode();
            if (stateCode == CommonConstant.SUCCESSFUL) {   //通信成功
                CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
                if (response.getResult() == CommonConstant.SUCCESSFUL) {    //请求成功
                    LOGGER.info("向【资管】发送修改站点MQTT 请求成功");
                    //对平台端数据库中保存的站点扩展信息进行修改
                    modifyResult = siteMongo.modifySite(uniqueCode, siteModel);
                }else {
                    LOGGER.error("向【资管】发送修改站点MQTT 请求失败");
                }
            }else {
                LOGGER.error("向【资管】发送修改站点MQTT 通信失败");
            }
        }else {
            //对平台端数据库中保存的站点扩展信息进行修改
            modifyResult = siteMongo.modifySite(uniqueCode, siteModel);
        }

        return modifyResult;
    }

    /**
     * 删除站点
     */
    @Override
    public void deleteSite(String uniqueCode, SiteQuery siteQuery) {
        //向【资管】下发删除（批量）站点的MQTT消息
        MsgResult msgResult = assetCIService.deleteAssetSite(uniqueCode, siteQuery);
        int stateCode = msgResult.getStateCode();
        if (stateCode == CommonConstant.SUCCESSFUL) {   //通信成功
            CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
            if (response.getResult() == CommonConstant.SUCCESSFUL) {
                LOGGER.info("向【资管】发送删除站点MQTT 请求成功");
                //（批量）删除站点
                siteMongo.deleteSites(uniqueCode, siteQuery);

                // TODO: 2020/2/2 删除每个站点下设备（包含FSU）

                // TODO: 2020/2/12 修改系统用户和维护用户的站点权限

            }else {
                LOGGER.error("向【资管】发送删除站点MQTT 请求失败");
            }
        }else {
            LOGGER.error("向【资管】发送删除站点MQTT 通信失败");
        }
    }

    /**
     * 获取资产管理员列表
     */
    @Override
    public List<String> getRespList(String uniqueCode, SiteQuery siteQuery) {
        int pageSize = siteQuery.getPageSize();
        // TODO: 2020/2/12 获取前六页站点中存在的资产管理员列表

        return new ArrayList<>();
    }

    private String[][] getSiteListAsTable(List<SiteModel> list) {
        int colNum = 14;
        int rowNum = list.size() + 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String[][] tableDatas = new String[rowNum][colNum];

        for (int i = 0; i < rowNum; i++) {
            String[] row = tableDatas[i];
            if (i == 0) {
                row[0] = "站点编码";
                row[1] = "区域层级";
                row[2] = "站点名称";
                row[3] = "站点类型";
                row[4] = "站点经纬";
                row[5] = "站点地址";
                row[6] = "资产管理员";
                row[7] = "联系电话";
                row[8] = "铁塔高度";
                row[9] = "铁塔类型";
                row[10] = "共享信息";
                row[11] = "产权性质";
                row[12] = "投入使用时间";
                row[13] = "占地面积";
            } else {
                SiteModel siteModel = list.get(i - 1);
                row[0] = siteModel.getCode();
                row[1] = siteModel.getTierName();
                row[2] = siteModel.getName();
                row[3] = siteModel.getSiteType();
                row[4] = siteModel.getCoordinate();
                row[5] = siteModel.getAddress();
                row[6] = siteModel.getRespName();
                row[7] = siteModel.getRespPhone();
                row[8] = siteModel.getTowerHeight();
                row[9] = siteModel.getTowerType();
                row[10] = siteModel.getShareInfo();
                row[11] = siteModel.getAssetNature();
                row[12] = sdf.format(new Date(siteModel.getCreateTime()));
                row[13] = siteModel.getAreaCovered();
            }
        }
        return tableDatas;
    }

    /**
     * @param uniqueCode
     * @param siteIdList
     * @auther: liudd
     * @date: 2020/3/3 13:44
     * 功能描述:根据id列表获取
     */
    @Override
    public List<SiteModel> getByIdList(String uniqueCode, List<Integer> siteIdList) {
        return null;
    }
}
