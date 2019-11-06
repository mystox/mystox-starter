package com.kongtrolink.framework.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.EnumLevelName;
import com.kongtrolink.framework.dao.EnterpriseLevelDao;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import com.kongtrolink.framework.service.DeviceTypeLevelService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import com.kongtrolink.framework.service.MqttSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 10:46
 * @Description:
 */
@Service
public class EnterpriseLevelServiceImpl implements EnterpriseLevelService{

    @Autowired
    EnterpriseLevelDao enterpriseLevelDao;
    @Autowired
    AlarmLevelService alarmLevelService;
    @Autowired
    DeviceTypeLevelService typeLevelService;
    @Autowired
    MqttSender mqttSender;
    @Value("${level.useEnterpriseLevel:true}")
    private boolean useEnterpriseLevel;
    @Value("${level.serverVersion:ALARM_SERVER_LEVEL_V1.0.0}")
    private String levelServerVersion;
    @Value("${level.updateEnterpriseLevelMap:updateEnterpriseLevelMap}")
    private String updateEnterpriseLevelMap;
    @Value("${level.useAlarmLevel:false}")
    private boolean useAlarmLevel;
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseLevelServiceImpl.class);
    @Override
    public void add(EnterpriseLevel enterpriseLevel) {
        List<Integer> levels = enterpriseLevel.getLevels();
        List<String> levelNames = enterpriseLevel.getLevelNames();
        List<String> colors = enterpriseLevel.getColors();
        enterpriseLevel.setLevels(null);
        enterpriseLevel.setLevelNames(null);
        enterpriseLevel.setColors(null);
        for(int i=0; i< levels.size(); i++){
            enterpriseLevel.setId(null);
            enterpriseLevel.setLevel(levels.get(i));
            enterpriseLevel.setLevelName(levelNames.get(i));
            enterpriseLevel.setColor(colors.get(i));
            enterpriseLevelDao.add(enterpriseLevel);
        }
    }

    @Override
    public boolean delete(String enterpriseLevelId) {
        //如果当前企业等级是启用状态，需要根据默认企业等级生成新得告警等级
        EnterpriseLevel enterpriseLevel = get(enterpriseLevelId);
        if(null == enterpriseLevel){
            return false;
        }
        boolean result = deleteByCode(enterpriseLevel.getCode());
        if(result) {
            if (Contant.USEING.equals(enterpriseLevel.getState())) {
                alarmLevelService.deleteList(enterpriseLevel.getEnterpriseCode(), enterpriseLevel.getServerCode(), null, null);
                addAlarmLevelByEnterpriseInfo(enterpriseLevel.getEnterpriseCode(), enterpriseLevel.getServerCode());
                //删除告警等级模块中企业告警
                updateEnterpriseLevelMap(enterpriseLevel.getEnterpriseCode(), enterpriseLevel.getServerCode(), enterpriseLevel.getCode(), Contant.ZERO);
            }
        }
        return result;
    }

    @Override
    public boolean deleteByCode(String code) {
        boolean result = enterpriseLevelDao.deleteByCode(code);
        return result;
    }

    @Override
    public boolean update(EnterpriseLevel enterpriseLevel) {
        enterpriseLevel.setUpdateTime(new Date());
        //根据code删除
        boolean delRes = deleteByCode(enterpriseLevel.getCode());
        if(delRes){
            add(enterpriseLevel);
        }
        //如果该企业等级属于启用状态，需要重新生成告警等级
        String state = enterpriseLevel.getState();
        if(Contant.USEING.equals(state)){
            alarmLevelService.deleteList(enterpriseLevel.getEnterpriseCode(), enterpriseLevel.getServerCode(), null, null);
            addAlarmLevelByEnterpriseInfo(enterpriseLevel.getEnterpriseCode(), enterpriseLevel.getServerCode());
            //修改告警等级模块的企业告警
            updateEnterpriseLevelMap(enterpriseLevel.getEnterpriseCode(), enterpriseLevel.getServerCode(), enterpriseLevel.getCode(), Contant.ONE);
        }

        return delRes;
    }

    @Override
    public EnterpriseLevel get(String enterpriseLevelId) {
        return enterpriseLevelDao.get(enterpriseLevelId);
    }

    @Override
    public List<EnterpriseLevel> list(EnterpriseLevelQuery levelQuery) {
        return enterpriseLevelDao.list(levelQuery);
    }

    @Override
    public int count(EnterpriseLevelQuery levelQuery) {
        return enterpriseLevelDao.count(levelQuery);
    }


    /**
     * @auther: liudd
     * @date: 2019/10/16 15:54
     * 功能描述:修改状态
     */
    @Override
    public boolean updateState(EnterpriseLevelQuery enterpriseLevelQuery) {
        Date curTime = new Date();
        String state = enterpriseLevelQuery.getState();
        String enterpriseCode = enterpriseLevelQuery.getEnterpriseCode();
        String serverCode = enterpriseLevelQuery.getServerCode();
        if(Contant.FORBIT.equals(state)){
            //如果禁用，需要删除原来的告警等级，然后使用默认企业等级生成告警等级
            alarmLevelService.deleteList(enterpriseCode, serverCode, null, null);
            //删除告警等级模块中企业告警
            updateEnterpriseLevelMap(enterpriseCode, serverCode, enterpriseLevelQuery.getCode(), Contant.ZERO);
        }else {
            //先禁用原来的企业等级
            boolean result = forbitBefor(enterpriseCode, serverCode, curTime);
            if(!result){
                return false;
            }
            alarmLevelService.deleteList(enterpriseCode, serverCode, null, null);
            //删除告警等级模块中企业告警
            updateEnterpriseLevelMap(enterpriseCode, serverCode, enterpriseLevelQuery.getCode(), Contant.ONE);
        }
        boolean result = enterpriseLevelDao.updateState(enterpriseLevelQuery);
        addAlarmLevelByEnterpriseInfo(enterpriseCode, serverCode);
        return result;
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @auther: liudd
     * @date: 2019/10/16 15:02
     * 功能描述:获取最后一次启用的企业告警，如果没有，则获取默认企业告警
     */
    @Override
    public List<EnterpriseLevel> getLastUse(String enterpriseCode, String serverCode) {
        return enterpriseLevelDao.getLastUse(enterpriseCode, serverCode);
    }

    @Override
    public void addAlarmLevelByEnterpriseInfo(String enterpriseCode, String serverCode) {
        List<DeviceTypeLevel> deviceTypeLevels = typeLevelService.listByEnterpriseInfo(enterpriseCode, serverCode);
        if(null == deviceTypeLevels || deviceTypeLevels.size() == 0){
            return ;
        }
        for(DeviceTypeLevel deviceTypeLevel : deviceTypeLevels){
            typeLevelService.addAlarmLevelByDeviceLevel(deviceTypeLevel);
        }
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @auther: liudd
     * @date: 2019/10/16 20:18
     * 功能描述:禁用原来的告警等级
     */
    @Override
    public boolean forbitBefor(String enterpriseCode, String serverCode, Date updateTime) {
        return enterpriseLevelDao.forbitBefor(enterpriseCode, serverCode, updateTime);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/28 14:27
     * 功能描述:初始化默认企业告警等级
     */
    @Override
    public void initEnterpriseLevel() {
        //1，获取所有默认告警
        List<EnterpriseLevel> systemLevel = enterpriseLevelDao.getSystemLevel();
        if(null == systemLevel || systemLevel.size()<8){
            logger.info("准备初始化系统默认告警...");
            //删除所有当前不完整默认告警
            enterpriseLevelDao.deleteSystemLevel();
        }else{
            logger.info("企业告警等级不需要初始化");
            return ;
        }
        Date curDate = new Date();
        List<EnterpriseLevel> enterpriseLevelList = new ArrayList<>();
        EnterpriseLevel enterpriseLevel = new EnterpriseLevel();
        enterpriseLevel.setId(null);
        enterpriseLevel.setCode("system_code");
        enterpriseLevel.setName("系统默认告警等级");
        enterpriseLevel.setColor("#DB001B");
        enterpriseLevel.setUpdateTime(curDate);
        enterpriseLevel.setLevelType(Contant.SYSTEM);
        enterpriseLevel.setState(Contant.USEING);
        enterpriseLevel.setEnterpriseCode("system");
        enterpriseLevel.setServerCode("code");
        enterpriseLevelList.add(enterpriseLevel);
        for(int i=1; i<=8; i++) {
            enterpriseLevel.setLevel(i);
            enterpriseLevel.setLevelName(EnumLevelName.getNameByLevel(i));
            logger.info("默认等级：{}", enterpriseLevel.toString());
        }
        enterpriseLevelDao.add(enterpriseLevelList);
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @auther: liudd
     * @date: 2019/11/6 16:23
     * 功能描述:调用远程接口修改告警等级模块中企业等级
     */
    @Override
    public void updateEnterpriseLevelMap(String enterpriseCode, String serverCode, String enterpriseLevelCode, String type) {
        if(!useEnterpriseLevel){
            logger.info("不需要修改告警等级模块企业告警等级, useEnterpriseLevel:{}", useEnterpriseLevel);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        String key = enterpriseCode + Contant.UNDERLINE + serverCode;
        jsonObject.put("key", key);
        int resultCode = 0;
        if (Contant.ZERO.equals(type)) {
            //删除告警等级模块的企业告警等级
        }else if(Contant.ONE.equals(type)){
            //先根据企业编码和服务编码获取企业告警等级
            List<EnterpriseLevel> enterpriseLevelList = enterpriseLevelDao.getByCodes(Arrays.asList(enterpriseCode));
            jsonObject.put("enterpriseLevelList", enterpriseLevelList);
        }
        try {
            MsgResult msgResult = mqttSender.sendToMqttSyn(levelServerVersion, updateEnterpriseLevelMap, jsonObject.toJSONString());
            resultCode = msgResult.getStateCode();
        }catch (Exception e){

        }
        if(resultCode != 1) {
            logger.info("修改告警等级模块企业告警等级失败，请重启告警等级模块.type:{}, code:{}, msg:{}, result:", type, key, jsonObject.toJSONString(), resultCode);
        }else{
            logger.info("修改告警等级模块企业告警等级成功.type:{}, code:{}, msg:{}, result:{}", type, key, jsonObject.toJSONString(), resultCode);
        }
    }
}
