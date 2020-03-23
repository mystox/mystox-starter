package com.kongtrolink.framework.scloud.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * 资产管理-站点资产管理 接口类
 * Created by Eric on 2020/2/12.
 */
public interface SiteService {

    /**
     * 生成站点编码
     */
    String createSiteCode(String uniqueCode, String tierCode);

    /**
     * 检查站点编码是否重复
     */
    boolean checkCodeExisted(String uniqueCode, String siteCode);

    /**
     * 添加站点
     */
    void addSite(String uniqueCode, SiteModel siteModel);

    /**
     * 获取站点列表
     */
    List<SiteModel> findSiteList(String uniqueCode, SiteQuery siteQuery);

    /**
     * 获取简化版站点列表
     *  根据query.getSimplifiedSitekeys()获取到的简化版站点所需参数的key，删减Site中不需要的参数，仅保留Site中所传需要的参数
     */
    List<JSONObject> getSimplifiedSiteList(List<SiteModel> siteModelList, SiteQuery siteQuery);

    /**
     * 导出站点列表
     */
    HSSFWorkbook exportSiteList(List<SiteModel> list);

    /**
     * 修改站点
     */
    boolean modifySite(String uniqueCode, SiteModel siteModel);

    /**
     * 删除站点
     */
    void deleteSite(String uniqueCode, SiteQuery siteQuery);

    /**
     * 获取资产管理员列表
     */
    List<String> getRespList(String uniqueCode, SiteQuery siteQuery);
}
