package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.Config3dDao;
import com.kongtrolink.framework.scloud.entity.config3d.Config3dScene;
import com.kongtrolink.framework.scloud.entity.config3d.ConfigAppLocateMap;
import com.kongtrolink.framework.scloud.service.Config3dService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mag
 **/
@Service
public class Config3dServiceImpl implements Config3dService {
    @Autowired
    private Config3dDao config3dDao;
    /**
     * 更新保存组态信息
     *
     * @param uniqueCode    企业编码
     * @param config3dScene 组态信息
     */
    @Override
    public void upsert(String uniqueCode, Config3dScene config3dScene) {
        config3dDao.upsert(uniqueCode, config3dScene);
    }

    /**
     * 根据站点信息查询组态信息
     *
     * @param uniqueCode 企业编码
     * @param siteId     站点数据库ID
     * @return 组态信息
     */
    @Override
    public Config3dScene find(String uniqueCode, int siteId) {
        return config3dDao.find(uniqueCode,siteId);
    }

    /**
     * 存储app坐标映射数据
     *
     * @param uniqueCode 企业编码
     * @param locateMap  坐标数据
     */
    @Override
    public void saveConfigAppLocateMap(String uniqueCode, ConfigAppLocateMap locateMap) {
        config3dDao.saveConfigAppLocateMap(uniqueCode, locateMap);
    }

    /**
     * 查询条件
     *
     * @param uniqueCode 企业编码
     * @param siteId     站点信息
     * @return 坐标映射数据
     */
    @Override
    public ConfigAppLocateMap findConfigAppLocateMap(String uniqueCode, int siteId) {
        return config3dDao.findConfigAppLocateMap(uniqueCode,siteId);
    }

    /**
     * 更新保存 坐标映射数据
     *
     * @param uniqueCode 企业编码
     * @param locateMap  坐标映射数据
     */
    @Override
    public void upsertConfigAppLocateMap(String uniqueCode, ConfigAppLocateMap locateMap) {
        config3dDao.upsertConfigAppLocateMap(uniqueCode, locateMap);
    }

    /**
     * 删除  坐标映射数据
     *
     * @param uniqueCode 企业编码
     * @param siteId     站点信息
     */
    @Override
    public void removeConfigAppLocateMap(String uniqueCode, int siteId) {
        config3dDao.removeConfigAppLocateMap(uniqueCode, siteId);
    }

}
