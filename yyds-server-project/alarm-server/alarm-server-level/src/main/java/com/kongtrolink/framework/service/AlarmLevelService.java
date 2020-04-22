package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.dao.AlarmLevelDao;
import com.kongtrolink.framework.dao.EnterpriseLevelDao;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/10/12 14:09
 * @Description:
 */
@Service
public class AlarmLevelService {

    @Autowired
    AlarmLevelDao alarmLevelDao;
    @Autowired
    EnterpriseLevelDao enterpriseLevelDao;
    @Value("${dataLevel.enterpriseLevel: true}")
    private boolean useEnterpriseLevel;
    @Value("${dataLevel.alarmLevel:false}")
    private boolean userAlarmLevel;

    //存储企业告警等级和系统默认等级(enterpriseCode_serverCode -- enterpriseLevelList)
    Map<String, List<EnterpriseLevel>> enterpriseLevelMap = new HashMap<>();
    //存储告警等级（enterpriseCode_serverCode_deviceType_deviceModel_level -- alarmLevel）
    Map<String, AlarmLevel> alarmLevelMap = new HashMap<>();
    //enterpriceCode_serverCode--alarmLevelList
    Map<String, List<AlarmLevel>> enterSerCodeAlarmLevelListMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(AlarmLevelService.class);

    public AlarmLevel getLevelByAlarm(Alarm alarm){
        String enterpriseCode = alarm.getEnterpriseCode();
        String serverCode = alarm.getServerCode();
        String deviceType = alarm.getDeviceType();
        String deviceModel = alarm.getDeviceModel();
        Integer level = alarm.getLevel();
        AlarmLevel alarmLevel ;
        if(userAlarmLevel){
            alarmLevel = matchAlarmLevel(enterpriseCode, serverCode, deviceType, deviceModel, level);
            logger.info("【告警等级模块】启用了告警等级对应，获取到的告警等级：{}", alarmLevel.toString());
        }else {
            alarmLevel = alarmLevelDao.matchLevel(enterpriseCode, serverCode, deviceType, deviceModel, level);
            logger.info("【告警等级模块】没启用告警等级对应，从数据库获取到的告警等级：{}", alarmLevel.toString());
        }
        if(null == alarmLevel) {
            EnterpriseLevel enterpriseLevel;
            if(useEnterpriseLevel) {
                enterpriseLevel = matchEnterpriseLevel(enterpriseCode, serverCode, level);
                logger.info("【告警等级模块】告警等级对应为空，启用了企业等级对应，获取的企业等级为：{}", enterpriseLevel.toString());
            }else{
                enterpriseLevel = enterpriseLevelDao.matchLevel(enterpriseCode, serverCode, level);
                logger.info("【告警等级模块】告警等级对应为空，未启用企业等级对应，从数据库获取的企业等级为：{}", enterpriseLevel.toString());
            }
            if (null != enterpriseLevel) {
                alarmLevel = new AlarmLevel();
                alarmLevel.setTargetLevel(enterpriseLevel.getLevel());
                alarmLevel.setTargetLevelName(enterpriseLevel.getLevelName());
                alarmLevel.setColor(enterpriseLevel.getColor());
            }
        }
        return alarmLevel;
    }

    public void initEnterpriseLevelMap(){
        if(!useEnterpriseLevel){
            logger.info("不是用内存存储系统等级.useEnterpriseLevel:{}", useEnterpriseLevel);
        }
        //获取所有企业告警等级code
        List<String> codes = enterpriseLevelDao.getCodes();
        //获取所有企业告警等级，包括默认告警等级
        List<EnterpriseLevel> byCodes = enterpriseLevelDao.getByCodes(codes);
        for(EnterpriseLevel enterpriseLevel : byCodes){
            String key = enterpriseLevel.getEnterpriseCode() + Contant.UNDERLINE + enterpriseLevel.getServerCode();
            List<EnterpriseLevel> enterpriseLevelList = enterpriseLevelMap.get(key);
            if(null == enterpriseLevelList){
                enterpriseLevelList = new ArrayList<>();
            }
            enterpriseLevelList.add(enterpriseLevel);
            enterpriseLevelMap.put(key, enterpriseLevelList);
        }
        logger.info("初始化企业告警等级map, useEnterpriseLevel:{}, map keyset:{}", useEnterpriseLevel, enterpriseLevelMap.keySet().toString());
    }

    /**
     * @auther: liudd
     * @date: 2019/11/6 16:14
     * 功能描述:修改内存中企业告警等级
     * 20191228企业告警等级已经不存在删除了，web模块禁用，启用企业告警等级，都是修改
     */
    public void updateEnterpriseLevelMap(String jsonStr){
        JSONObject jsonObject = JSONObject.parseObject(jsonStr, JSONObject.class);
        String key = jsonObject.getString("key");
        String enterpriseLevelListStr = jsonObject.getString("enterpriseLevelList");
        List<EnterpriseLevel> enterpriseLevelList = JSONArray.parseArray(enterpriseLevelListStr, EnterpriseLevel.class);
        enterpriseLevelMap.put(key, enterpriseLevelList);
        logger.info("add enterpriseLevel, key:{}, enterpriseListStr:{}", key, enterpriseLevelListStr);
    }

    /**
     * @auther: liudd
     * @date: 2019/12/3 15:15
     * 功能描述:修改企业告警等级
     * 2191228enterpriseLevelList存储顺序为lastUse
     */
    public EnterpriseLevel matchEnterpriseLevel(String enterpriseCode, String serverCode, int level){
        String key = enterpriseCode + Contant.UNDERLINE + serverCode;
        List<EnterpriseLevel> enterpriseLevelList = enterpriseLevelMap.get(key);
        if(null == enterpriseLevelList){
            logger.info("enterpriseCode：{}，serverCode:{} 对应的企业告警等级不存在，将使用默认告警等级 ", enterpriseCode, serverCode);
            enterpriseLevelList = enterpriseLevelMap.get("system_code");
        }
        if(null == enterpriseLevelList || enterpriseLevelList.size() == 0){
            logger.info("默认告警等级不存在，请重启服务器，初始化默认告警等级");
            return null;
        }
        for(int i=0; i<enterpriseLevelList.size(); i++){
            EnterpriseLevel enterpriseLevel = enterpriseLevelList.get(i);
            if(enterpriseLevel.getLevel() > level){
                continue;
            }
            return enterpriseLevel;
        }
        EnterpriseLevel enterpriseLevel = enterpriseLevelList.get(0);
        logger.info("enterpriseCode：{}，serverCode:{}, level:{} 对应最小企业告警等级：{}-{}-{}-{}",
                enterpriseCode, serverCode, level,
                enterpriseLevel.getEnterpriseCode(), enterpriseLevel.getServerCode(), enterpriseLevel.getLevel(), enterpriseLevel.getLevelName());
        return enterpriseLevel;
    }

    public void initAlarmLevelMap(){
        if(!userAlarmLevel){
            logger.info("不是用内存存储告警等级.userAlarmLevel:{}", userAlarmLevel);
            return ;
        }
        List<AlarmLevel> all = alarmLevelDao.getAll();
        for(AlarmLevel alarmLevel : all){
            String enterpriseCode = alarmLevel.getEnterpriseCode();
            String serverCode = alarmLevel.getServerCode();
            String key = alarmLevel.getKey();
            alarmLevelMap.put(key, alarmLevel);
            //保存企业下所有告警等级，方便web端远程调用修改
            String enterServer = enterpriseCode + Contant.EXCLAM + serverCode;
            List<AlarmLevel> enterSerCodeAlarmLevelList = enterSerCodeAlarmLevelListMap.get(enterServer);
            if(null == enterSerCodeAlarmLevelList){
                enterSerCodeAlarmLevelList = new ArrayList<>();
            }
            enterSerCodeAlarmLevelList.add(alarmLevel);
            enterSerCodeAlarmLevelListMap.put(enterServer, enterSerCodeAlarmLevelList);
        }
        logger.info("初始化告警等级map, userAlarmLevel:{}, map.size:{} ", userAlarmLevel, alarmLevelMap.size());
    }

    public AlarmLevel matchAlarmLevel(String enterpriseCode, String serverCode, String deviceType, String deviceModel, int sourceLevel){
        String key = enterpriseCode + Contant.EXCLAM + serverCode + Contant.EXCLAM + deviceType + Contant.EXCLAM + deviceModel + Contant.EXCLAM + sourceLevel;
        return alarmLevelMap.get(key);
    }

    public void updateAlarmLevelMap(String jsonStr){
        //type:level:key
        if(StringUtil.isNUll(jsonStr)){
            return;
        }
        String[] split = jsonStr.split(Contant.COLON);
        if(split.length != 4){
            logger.info("接收到错误修改告警等级调用：{}", jsonStr);
            return;
        }
        logger.info("接收到远程告警等级调用消息：{}", jsonStr);
        String type = split[0];
        String level = split[1];
        String enterServerDevice = split[2];
        String deleteKey = split[3];
        String[] info = enterServerDevice.split(Contant.EXCLAM);
        if(info.length <2){
            logger.info("接收到错误修改告警等级调用：{}", jsonStr);
            return;
        }
        String enterpriseCode = info[0];
        String serverCode = info[1];
        String enterServer = enterpriseCode + Contant.EXCLAM + serverCode;
        if(Contant.DEVICELEVEL.equals(level)) {
            if (info.length != 4) {
                logger.info("接收到错误修改告警等级调用：{}", jsonStr);
                return;
            }
            //添加设备type:level:enterpriseCode!serverCode!deviceType!deviceModel:THENULL
            //告警等级页面修改告警等级，修改设备等级，删除设备等级type:level:enterpriseCode!serverCode!deviceType!deviceModel:key1,key2...
            if (!Contant.THENULL.equals(deleteKey)){
                for (String key : deleteKey.split(Contant.COMMA)) {
                    alarmLevelMap.remove(key);
                }
            }
            if(Contant.UPDATE.equals(type)) {
                //根据enterpriseCode, serverCode, deviceType, deviceModel更新alarmLevelMap中告警信息
                List<AlarmLevel> alarmLevelList = alarmLevelDao.getByInfo(info[0], info[1], info[2], info[3]);
                for (AlarmLevel alarmLevel : alarmLevelList) {
                    alarmLevelMap.put(alarmLevel.getKey(), alarmLevel);
                }
            }
            //重置enterSerCodeAlarmLevelListMap
            //1根据enterpriseCode和serverCode从数据库获取告警
            List<AlarmLevel> alarmLevelList = alarmLevelDao.getByInfo(enterpriseCode, serverCode, null, null);
            //2，跟新enterSerCodeAlarmLevelListMap中数据
            enterSerCodeAlarmLevelListMap.put(enterServer, alarmLevelList);

        }else if(Contant.ENTERPRISELEVEL.equals(level)){
            //企业告警等级只有修改type:level:enterpriseCode!serverCode:THENULL
            List<AlarmLevel> enterSerCodeAlarmLevelList = enterSerCodeAlarmLevelListMap.get(enterServer);
            if(null != enterSerCodeAlarmLevelList){
                for(AlarmLevel alarmLevel : enterSerCodeAlarmLevelList){
                    alarmLevelMap.remove(alarmLevel.getKey());
                }
            }
            enterSerCodeAlarmLevelListMap.remove(enterServer);
            //修改企业告警，引发的修改告警等级
            if(Contant.UPDATE.equals(type)){
                //1根据enterpriseCode和serverCode从数据库获取告警
                List<AlarmLevel> alarmLevelList = alarmLevelDao.getByInfo(enterpriseCode, serverCode, null, null);
                //2，跟新enterSerCodeAlarmLevelListMap中数据
                enterSerCodeAlarmLevelListMap.put(enterServer, alarmLevelList);
                //3,更新alarmLevelMap中数据
                for(AlarmLevel alarmLevel : alarmLevelList){
                    alarmLevelMap.put(alarmLevel.getKey(), alarmLevel);
                }
            }
        }
    }
}
