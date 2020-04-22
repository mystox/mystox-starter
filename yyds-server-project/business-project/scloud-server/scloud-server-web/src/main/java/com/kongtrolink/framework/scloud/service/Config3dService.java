/** *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ****************************************************** */
package com.kongtrolink.framework.scloud.service;



import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.config3d.Config3dScene;
import com.kongtrolink.framework.scloud.entity.config3d.ConfigAppLocateMap;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * copy from scloud
 *  组态功能
 * @author Mag
 */
public interface Config3dService {

    /**
     * 更新保存组态信息
     * @param uniqueCode 企业编码
     * @param config3dScene 组态信息
      */
     void upsert(String uniqueCode, Config3dScene config3dScene);

    /**
     * 根据站点信息查询组态信息
     * @param uniqueCode 企业编码
     * @param siteId 站点数据库ID
     * @return 组态信息
     */
     Config3dScene find(String uniqueCode, int siteId);

    /**
     * 存储app坐标映射数据
     * @param uniqueCode 企业编码
     * @param locateMap 坐标数据
     */
     void saveConfigAppLocateMap(String uniqueCode, ConfigAppLocateMap locateMap);

    /**
     * 查询条件
     * @param uniqueCode 企业编码
     * @param siteId 站点信息
     * @return 坐标映射数据
     */
    ConfigAppLocateMap findConfigAppLocateMap(String uniqueCode, int siteId);
    /**
     * 更新保存 坐标映射数据
     * @param uniqueCode 企业编码
     * @param locateMap 坐标映射数据
     */
    void upsertConfigAppLocateMap(String uniqueCode, ConfigAppLocateMap locateMap) ;
    /**
     * 删除  坐标映射数据
     * @param uniqueCode 企业编码
     * @param siteId 站点信息
     */
    void removeConfigAppLocateMap(String uniqueCode, int siteId);
}
