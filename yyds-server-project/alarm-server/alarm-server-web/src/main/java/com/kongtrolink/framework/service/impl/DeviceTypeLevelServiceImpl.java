package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.dao.DeviceTypeLevelDao;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.DeviceTypeLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import com.kongtrolink.framework.service.DeviceTypeLevelService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 16:43
 * @Description:
 */
@Service
public class DeviceTypeLevelServiceImpl implements DeviceTypeLevelService {
    @Autowired
    DeviceTypeLevelDao typeLevelDao;
    @Autowired
    AlarmLevelService alarmLevelService;
    @Autowired
    EnterpriseLevelService enterpriseLevelService;
    //    @Autowired
//    MqttSender mqttSender;
    @Autowired
    MqttOpera mqttOpera;

    @Value("${level.serverVersion:ALARM_SERVER_LEVEL_V1.0.0}")
    private String levelServerVersion;
    @Value("${level.updateAlarmLevelMap:updateAlarmLevelMap}")
    private String updateAlarmLevelMap;
    @Value("${level.useAlarmLevel:false}")
    private boolean useAlarmLevel;
    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeLevelServiceImpl.class);

    @Override
    public boolean add(DeviceTypeLevel deviceTypeLevel) {
        typeLevelDao.add(deviceTypeLevel);
        if (!StringUtil.isNUll(deviceTypeLevel.getId())) {
            //添加成功，则生成对应的自定义告警等级
            String enterpriseCode = deviceTypeLevel.getEnterpriseCode();
            String serverCode = deviceTypeLevel.getServerCode();
            List<EnterpriseLevel> lastUse = enterpriseLevelService.getLastUse(enterpriseCode, serverCode);
            addAlarmLevelByDeviceLevel(deviceTypeLevel, lastUse);
            String deviceType = deviceTypeLevel.getDeviceType();
            String deviceModel = deviceTypeLevel.getDeviceModel();
            //修改等级模块告警等级，和修改设备告警等级一致
            String key = enterpriseCode + Contant.EXCLAM + serverCode + Contant.EXCLAM + deviceType + Contant.EXCLAM + deviceModel;
            updateAlarmLevelModel(Contant.UPDATE, Contant.DEVICELEVEL, key, Contant.THENULL);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String deviceTypeLevelId) {
        DeviceTypeLevel deviceTypeLevel = get(deviceTypeLevelId);
        String enterpriseCode = deviceTypeLevel.getEnterpriseCode();
        String serverCode = deviceTypeLevel.getServerCode();
        String deviceType = deviceTypeLevel.getDeviceType();
        String deviceModel = deviceTypeLevel.getDeviceModel();
        boolean delete = typeLevelDao.delete(deviceTypeLevelId);
        if (delete) {
            //获取等级模块该设备需要删除的告警等级
            String deleteKey = alarmLevelService.getDeleteKey(enterpriseCode, serverCode, deviceType, deviceModel);
            //删除设备对应的等级关系
            int result = alarmLevelService.deleteList(enterpriseCode, serverCode, deviceType, deviceModel);
            //删除等级模块告警等级
            String key = enterpriseCode + Contant.EXCLAM + serverCode + Contant.EXCLAM + deviceType + Contant.EXCLAM + deviceModel;
            updateAlarmLevelModel(Contant.DELETE, Contant.DEVICELEVEL, key, deleteKey);
            if (result > 0) {
                delete = true;
            } else {
                delete = false;
            }
        }
        return delete;
    }

    @Override
    public boolean update(DeviceTypeLevel deviceTypeLevel) {
        boolean update = typeLevelDao.update(deviceTypeLevel);
        if (update) {
            String enterpriseCode = deviceTypeLevel.getEnterpriseCode();
            String serverCode = deviceTypeLevel.getServerCode();
            String deviceType = deviceTypeLevel.getDeviceType();
            String deviceModel = deviceTypeLevel.getDeviceModel();
            //先获取等级模块需要删除的 该设备原来的告警等级
            String deleteKey = alarmLevelService.getDeleteKey(enterpriseCode, serverCode, deviceType, deviceModel);
            //删除之前设备型号等级对应的告警等级
            alarmLevelService.deleteList(enterpriseCode, serverCode, deviceType, deviceModel);
            //生成对应的自定义告警等级
            List<EnterpriseLevel> lastUse = enterpriseLevelService.getLastUse(enterpriseCode, serverCode);
            addAlarmLevelByDeviceLevel(deviceTypeLevel, lastUse);
            //修改等级模块告警等级
            String key = enterpriseCode + Contant.EXCLAM + serverCode + Contant.EXCLAM + deviceType + Contant.EXCLAM + deviceModel;
            updateAlarmLevelModel(Contant.UPDATE, Contant.DEVICELEVEL, key, deleteKey);
        }
        return update;
    }

    /**
     * @param deviceTypeLevelId
     * @auther: liudd
     * @date: 2019/9/26 13:47
     * 功能描述:根据id获取
     */
    @Override
    public DeviceTypeLevel get(String deviceTypeLevelId) {
        return typeLevelDao.get(deviceTypeLevelId);
    }

    @Override
    public List<DeviceTypeLevel> list(DeviceTypeLevelQuery levelQuery) {
        return typeLevelDao.list(levelQuery);
    }

    @Override
    public int count(DeviceTypeLevelQuery levelQuery) {
        return typeLevelDao.count(levelQuery);
    }

    @Override
    public DeviceTypeLevel getOne(DeviceTypeLevelQuery levelQuery) {
        return typeLevelDao.getOne(levelQuery);
    }

    @Override
    public boolean isRepeat(DeviceTypeLevel typeLevel) {
        DeviceTypeLevelQuery levelQuery = new DeviceTypeLevelQuery();
        levelQuery.setEnterpriseCode(typeLevel.getEnterpriseCode());
        levelQuery.setServerCode(typeLevel.getServerCode());
        levelQuery.setDeviceType(typeLevel.getDeviceType());
        levelQuery.setDeviceModel(typeLevel.getDeviceModel());
        DeviceTypeLevel one = getOne(levelQuery);
        if (null == one) {
            return false;
        }
        if (one.getId().equals(typeLevel.getId())) {
            return false;
        }
        return true;
    }

    /**
     * @param deviceTypeLevel
     * @auther: liudd
     * @date: 2019/9/26 13:33
     * 功能描述:根据设备型号告警，生成新告警自定义等级
     */
    @Override
    public boolean addAlarmLevelByDeviceLevel(DeviceTypeLevel deviceTypeLevel, List<EnterpriseLevel> lastUse) {
        String enterpriseCode = deviceTypeLevel.getEnterpriseCode();
        String serverCode = deviceTypeLevel.getServerCode();
        for (Integer level : deviceTypeLevel.getLevels()) {
            EnterpriseLevel match = getMatch(lastUse, level);
            AlarmLevel alarmLevel = new AlarmLevel(enterpriseCode, serverCode, deviceTypeLevel.getDeviceType(), deviceTypeLevel.getDeviceModel());
            alarmLevel.setEnterpriseName(deviceTypeLevel.getEnterpriseName());
            alarmLevel.setServerName(deviceTypeLevel.getServerName());
            alarmLevel.setSourceLevel(level);
            alarmLevel.setTargetLevel(match.getLevel());
            alarmLevel.setTargetLevelName(match.getLevelName());
            alarmLevel.setColor(match.getColor());
            alarmLevel.setUpdateTime(new Date());
            alarmLevelService.save(alarmLevel);
        }
        return true;
    }

    /**
     * @auther: liudd
     * @date: 2019/12/28 9:50
     * 功能描述:匹配设备等级的最小企业等级
     * 要求企业等级列表是倒叙
     */
    private EnterpriseLevel getMatch(List<EnterpriseLevel> enterpriseLevels, Integer level) {
        EnterpriseLevel resultEnter = null;
        for (EnterpriseLevel enterpriseLevel : enterpriseLevels) {
            if (enterpriseLevel.getLevel() > level) {
                continue;
            }
            resultEnter = enterpriseLevel;
            break;
        }
        return resultEnter;
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @auther: liudd
     * @date: 2019/10/16 16:13
     * 功能描述:根据企业信息获取所有设备等级
     */
    @Override
    public List<DeviceTypeLevel> listByEnterpriseInfo(String enterpriseCode, String serverCode) {
        return typeLevelDao.listByEnterpriseInfo(enterpriseCode, serverCode);
    }

    /**
     * @auther: liudd
     * @date: 2019/12/5 12:24
     * 功能描述:远程修改等级模块中告警等级
     * 告警等级和设备等级
     * String key = enterpriseCode + Contant.EXCLAM + serverCode + Contant.EXCLAM + deviceType + Contant.EXCLAM + deviceModel
     * 企业等级：
     * String key = enterpriseCode + Contant.EXCLAM + serverCode
     */
    public void updateAlarmLevelModel(String type, String level, String key, String deleteKey) {
        if (!useAlarmLevel) {
            logger.info("不需要修改告警等级模块企业告警等级, useAlarmLevel:{}", useAlarmLevel);
            return;
        }
        int resultCode;
        if (!Contant.DEVICELEVEL.equals(level) && !Contant.ENTERPRISELEVEL.equals(level)) {
            return;
        }
        try {
            String jsonStr = type + Contant.COLON + level + Contant.COLON + key + Contant.COLON + deleteKey;
            MsgResult msgResult = mqttOpera.opera(updateAlarmLevelMap, jsonStr);
            resultCode = msgResult.getStateCode();
            if (resultCode != 1) {
                logger.info("修改告警等级模块告警等级失败，请重启告警等级模块,key:{}, result:", jsonStr, resultCode);
            } else {
                logger.info("修改告警等级模块告警等级成功.key:{}, result:", jsonStr, resultCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
