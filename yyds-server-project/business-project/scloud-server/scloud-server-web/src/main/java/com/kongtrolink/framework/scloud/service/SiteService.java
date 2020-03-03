package com.kongtrolink.framework.scloud.service;

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
     * 导出站点列表
     */
    HSSFWorkbook exportSiteList(List<SiteModel> list);

    /**
     * 修改站点
     */
    void modifySite(String uniqueCode, SiteModel siteModel);

    /**
     * 删除站点
     *
     * @param uniqueCode 企业识别码
     * @param code 站点编码
     */
    void deleteSite(String uniqueCode, String code);

    /**
     * 获取资产管理员列表
     */
    List<String> getRespList(String uniqueCode, SiteQuery siteQuery);

    /**
     * @auther: liudd
     * @date: 2020/3/3 13:44
     * 功能描述:根据id列表获取
     */
    List<SiteModel> getByIdList(String uniqueCode, List<Integer> siteIdList);
}
