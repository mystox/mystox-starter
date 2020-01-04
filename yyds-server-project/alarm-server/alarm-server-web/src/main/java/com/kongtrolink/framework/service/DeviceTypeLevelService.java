package com.kongtrolink.framework.service;

import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.DeviceTypeLevelQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 16:41
 * @Description:
 */
public interface DeviceTypeLevelService {

    boolean add(DeviceTypeLevel deviceTypeLevel);

    boolean delete(String deviceTypeLevelId);

    boolean update(DeviceTypeLevel deviceTypeLevel);

    /**
     * @auther: liudd
     * @date: 2019/9/26 13:47
     * 功能描述:根据id获取
     */
    DeviceTypeLevel get(String deviceTypeLevelId);

    List<DeviceTypeLevel> list(DeviceTypeLevelQuery levelQuery);

    int count(DeviceTypeLevelQuery levelQuery);

    DeviceTypeLevel getOne(DeviceTypeLevelQuery levelQuery);

    boolean isRepeat(DeviceTypeLevel typeLevel);

    /**
     * @auther: liudd
     * @date: 2019/9/26 13:33
     * 功能描述:根据设备型号告警，生成新告警自定义等级
     */
    boolean addAlarmLevelByDeviceLevel(DeviceTypeLevel deviceTypeLevel, List<EnterpriseLevel> lastUse);

    /**
     * @auther: liudd
     * @date: 2019/10/16 16:13
     * 功能描述:根据企业信息获取所有设备等级
     */
    List<DeviceTypeLevel> listByEnterpriseInfo(String enterpriseCode, String serverCode);

    /**
     * @auther: liudd
     * @date: 2019/12/5 12:24
     * 功能描述:远程修改等级模块中告警等级
     *  告警等级和设备等级
     * String key = enterpriseCode + Contant.EXCLAM + serverCode + Contant.EXCLAM + deviceType + Contant.EXCLAM + deviceModel
     * 企业等级：
     * String key = enterpriseCode + Contant.EXCLAM + serverCode
     */
    void updateAlarmLevelModel(String type, String level, String key, String deleteKey);
}