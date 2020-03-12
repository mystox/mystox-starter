package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.constant.Const;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.exception.ParameterException;
import com.kongtrolink.framework.scloud.dao.AlarmDao;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.FilterRule;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.service.*;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.*;

/**
 * @Auther: liudd
 * @Date: 2020/2/26 14:55
 * @Description:
 */
@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    AlarmDao alarmDao;
    @Autowired
    MqttOpera mqttOpera;
    @Autowired
    DeviceService deviceService;
    @Autowired
    SiteService siteService;
    @Autowired
    DeviceSignalTypeService deviceSignalTypeService;
    @Autowired
    FilterRuleService filterRuleService;

    @Value("${alarmModule.list:alarmRemoteList}")
    private String remoteList;
    @Value("${alarmModule.check:alarmRemoteOperate}")
    private String remoteOperate;

    private static final Logger logger = LoggerFactory.getLogger(AlarmServiceImpl.class);
    /**
     * @param alarmQuery
     * @auther: liudd
     * @date: 2020/3/3 14:01
     * 功能描述:获取列表
     */
    @Override
    public JsonResult list(AlarmQuery alarmQuery) throws Exception {
        listCommon(alarmQuery);
        //具体查询历史还是实时数据，由中台告警模块根据参数判定
        try {
            MsgResult msgResult = mqttOpera.opera(remoteList, JSONObject.toJSONString(alarmQuery));
            JsonResult jsonResult = JSONObject.parseObject(msgResult.getMsg(), JsonResult.class);
            String dataStr = jsonResult.getData().toString();
            List<Alarm> alarmList = JSONObject.parseArray(dataStr, Alarm.class);
            initInfo(alarmQuery.getEnterpriseCode(), alarmList);
            jsonResult.setData(alarmList);
            return jsonResult;
        }catch (Exception e){
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSONString(alarmQuery), remoteList, e.getMessage());
            throw new Exception("获取数据超时");
        }
    }

    /**
     * @param alarmList
     * @auther: liudd
     * @date: 2020/3/3 15:59
     * 功能描述:填充设备，站点等信息
     */
    @Override
    public void initInfo(String uniqueCode, List<Alarm> alarmList) {
        if(null == alarmList){
            return;
        }
        //获取站点信息
        List<String> deviceCodeList = new ArrayList<>();
        Map<String, List<Alarm>> deviceCodeAlarmListMap = new HashMap<>();
        for(Alarm alarm : alarmList){
            String deviceId = alarm.getDeviceId();
            if(!deviceCodeList.contains(deviceId)){
                deviceCodeList.add(deviceId);
            }
            List<Alarm> deviceCodeAlarmList = deviceCodeAlarmListMap.get(deviceId);
            if(null == deviceCodeAlarmList){
                deviceCodeAlarmList = new ArrayList<>();
            }
            deviceCodeAlarmList.add(alarm);
            deviceCodeAlarmListMap.put(deviceId, deviceCodeAlarmList);
        }
        List<DeviceModel> deviceModelList = deviceService.getByCodeList(uniqueCode, deviceCodeList);
        List<String> typeCodeList = new ArrayList<>();
        for(DeviceModel deviceModel : deviceModelList){
            String code = deviceModel.getCode();
            String typeCode = deviceModel.getTypeCode();
            if(!typeCode.contains(typeCode)){
                typeCodeList.add(typeCode);
            }
            List<Alarm> deviceCodeAlarmList = deviceCodeAlarmListMap.get(code);
            for(Alarm alarm : deviceCodeAlarmList){
                alarm.setDeviceName(deviceModel.getName());
                alarm.setSiteId(deviceModel.getSiteId());
            }
        }
        //初始化站点信息
        List<Integer> siteIdList = new ArrayList<>();
        Map<Integer, List<Alarm>> siteIdAlarmListMap = new HashMap<>();
        for(Alarm alarm : alarmList){
            int siteId = alarm.getSiteId();
            if(!siteIdList.contains(siteId)){
                siteIdList.add(siteId);
            }
            List<Alarm> siteIdAlarmList = siteIdAlarmListMap.get(siteId);
            if(null == siteIdAlarmList){
                siteIdAlarmList = new ArrayList<>();
            }
            siteIdAlarmList.add(alarm);
            siteIdAlarmListMap.put(siteId, siteIdAlarmList);
        }
        List<SiteModel> siteModelList = siteService.getByIdList(uniqueCode, siteIdList);
        for(SiteModel siteModel : siteModelList){
            int siteId = siteModel.getSiteId();
            List<Alarm> siteIdAlarmList = siteIdAlarmListMap.get(siteId);
            for(Alarm alarm : siteIdAlarmList){
                alarm.setSiteName(siteModel.getName());
                alarm.setSiteAddress(siteModel.getAddress());
                alarm.setTierName(siteModel.getTierName());
            }
        }

        //填充信号点信息
        List<String> cntbIdList = new ArrayList<>();
        Map<String, List<Alarm>> cntbIdAlarmListMap = new HashMap<>();
        for(Alarm alarm : alarmList){
            String signalId = alarm.getSignalId();
            if(!cntbIdList.contains(signalId)){
                cntbIdList.add(signalId);
            }
            List<Alarm> cntbIdAlarmList = cntbIdAlarmListMap.get(signalId);
            if(cntbIdAlarmList == null){
                cntbIdAlarmList = new ArrayList<>();
            }
            cntbIdAlarmList.add(alarm);
            cntbIdAlarmListMap.put(signalId, cntbIdAlarmList);
        }
        List<SignalType> signalTypeList = deviceSignalTypeService.getByCodeListCntbIdList(uniqueCode, typeCodeList, cntbIdList);
        for(SignalType signalType : signalTypeList){
            String cntbId = signalType.getCntbId();
            List<Alarm> cntbIdAlarmList = cntbIdAlarmListMap.get(cntbId);
            for(Alarm alarm : cntbIdAlarmList){
                alarm.setSignalName(signalType.getTypeName());
            }
        }
    }

    @Override
    public JsonResult operate(AlarmQuery alarmQuery) throws Exception {
        checkOperatePara(alarmQuery);
        try {
            Date curTime = new Date();
            alarmQuery.setOperateTime(curTime);
            String jsonStr = JSONObject.toJSONString(alarmQuery);
            MsgResult msgResult = mqttOpera.opera(remoteOperate, jsonStr);
            JsonResult jsonResult = JSONObject.parseObject(msgResult.getMsg(), JsonResult.class);
            return jsonResult;
        }catch (Exception e){
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSONString(alarmQuery), remoteOperate, e.getMessage());
            throw new Exception("连接超时");
        }
    }

    private void checkPara(@RequestBody AlarmQuery alarmQuery)throws ParameterException{
        String enterpriseCode = alarmQuery.getEnterpriseCode();
        if(StringUtil.isNUll(enterpriseCode)){
            throw new ParameterException(Const.ALARM_LIST_ENTERPRISECODE + Const.PARA_MISS);
        }
        if(StringUtil.isNUll(alarmQuery.getServerCode())){
            throw new ParameterException(Const.ALARM_LIST_SERVERCODE + Const.PARA_MISS);
        }
        if(StringUtil.isNUll(alarmQuery.getType())){
            throw new ParameterException(Const.ALARM_LIST_TYPE + Const.PARA_MISS);
        }
    }

    private void listCommon(AlarmQuery alarmQuery)throws ParameterException {
        checkPara(alarmQuery);
        //liuddtodo 需要先根据前端选取站点层级和用户权限，获取用户站点编码列表,再获取用户管辖范围设备编码列表
        List<String> siteCodeList = alarmQuery.getSiteCodeList();
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setSiteCodes(siteCodeList);
        deviceQuery.setOperationState(alarmQuery.getOperationState());
        List<DeviceEntity> deviceEntityList = deviceService.listEntity(alarmQuery.getEnterpriseCode(), deviceQuery);
        List<String> deviceCodeList = deviceService.entityList2CodeList(deviceEntityList);
        alarmQuery.setDeviceCodeList(deviceCodeList);
        combineFilter(alarmQuery);
    }

    private void combineFilter(AlarmQuery alarmQuery){
        String uniqueCode = alarmQuery.getEnterpriseCode();
        //累加告警过滤功能
        FilterRule filterRule = filterRuleService.getUserInUse(uniqueCode, alarmQuery.getOperateUserId());
        if(null != filterRule){
            //比较时间
            Date startBeginTime = filterRule.getStartBeginTime();
            if(null != startBeginTime){
                Date sourStartBeginTime = alarmQuery.getStartBeginTime();
                if(null == sourStartBeginTime){
                    alarmQuery.setStartBeginTime(startBeginTime);
                }else{
                    if(startBeginTime.getTime() > sourStartBeginTime.getTime()){
                        alarmQuery.setStartBeginTime(startBeginTime);
                    }
                }
            }
            Date startEndTime = filterRule.getStartEndTime();
            if(null != startEndTime){
                Date sourEndTime = alarmQuery.getStartEndTime();
                if(null == sourEndTime){
                    alarmQuery.setStartEndTime(startEndTime);
                }else{
                    if(startEndTime.getTime() < sourEndTime.getTime()){
                        alarmQuery.setStartEndTime(startEndTime);
                    }
                }
            }
            Date clearBeginTime = filterRule.getClearBeginTime();
            if(null != clearBeginTime){
                Date sourBeginTime = alarmQuery.getClearBeginTime();
                if(null == sourBeginTime){
                    alarmQuery.setClearBeginTime(clearBeginTime);
                }else{
                    if(clearBeginTime.getTime() > sourBeginTime.getTime()){
                        alarmQuery.setClearBeginTime(clearBeginTime);
                    }
                }
            }
            Date clearEndTime = filterRule.getClearEndTime();
            if(null != clearEndTime){
                Date sourEndTime = alarmQuery.getClearEndTime();
                if(null == sourEndTime){
                    alarmQuery.setClearEndTime(clearEndTime);
                }else{
                    if(clearEndTime.getTime() < sourEndTime.getTime()){
                        alarmQuery.setClearEndTime(clearEndTime);
                    }
                }
            }
            //告警等级
            List<String> alarmLevelList = filterRule.getAlarmLevelList();
            if(null != alarmLevelList){
                String targetLevelName = alarmQuery.getTargetLevelName();
                if(StringUtil.isNUll(targetLevelName)){
                    alarmQuery.setTargetLevelNameList(alarmLevelList);
                }else{
                    if(alarmLevelList.contains(targetLevelName)){

                    }else{
                        alarmQuery.setTargetLevelName(targetLevelName + "特殊等级@特殊等级");
                    }
                }
            }
            //告警名称
            String alarmName = filterRule.getAlarmName();
            if(!StringUtil.isNUll(alarmName)){  //告警过滤规则中有告警名称
                String name = alarmQuery.getName();
                if(StringUtil.contant(name, alarmName)){

                }else if(StringUtil.contant(alarmName, name)){
                    alarmQuery.setName(alarmName);
                }else{
                    alarmQuery.setName(name + "特殊字符@特殊字符" + alarmName); //  如果告警列表与告警过滤的告警名称排斥，必然无法获取到任何告警数据
                }
            }
            //比对设备类型
            List<String> deviceTypeList = filterRule.getDeviceTypeList();
            if(null != deviceTypeList){
                String deviceType = alarmQuery.getDeviceType();
                if(StringUtil.isNUll(deviceType)){
                    alarmQuery.setDeviceTypeList(deviceTypeList);
                }else{
                    if(!deviceTypeList.contains(deviceType)){
                        alarmQuery.setDeviceType(deviceType + "特殊类型@特殊类型");
                    }
                }
            }
            //根据告警过滤规则，获取站点编码列表，与告警列表传递的站点列表取交集
            List<String> siteCodeList = filterRule.getSiteCodeList();
            DeviceQuery deviceQuery = new DeviceQuery();
            deviceQuery.setPageSize(Integer.MAX_VALUE);
            deviceQuery.setSiteCodes(siteCodeList);
            List<DeviceEntity> deviceEntityList = deviceService.listEntity(uniqueCode, deviceQuery);
            List<String> deviceCodeList = deviceService.entityList2CodeList(deviceEntityList);
            List<String> sourceDeviceCodeList = alarmQuery.getDeviceCodeList();
            deviceCodeList.retainAll(sourceDeviceCodeList);
            alarmQuery.setDeviceCodeList(deviceCodeList);
        }
    }

    private void checkOperatePara(AlarmQuery alarmQuery)throws ParameterException{
        checkPara(alarmQuery);
        //判定告警id，上报时间
        List<String> idList = alarmQuery.getIdList();
        List<Date> treportList = alarmQuery.getTreportList();
        if(null == idList || idList.size() == 0){
            throw new ParameterException("告警id为空");
        }
        if(null == treportList || treportList.size() == 0){
            throw new ParameterException("上报时间为空");
        }
        if(idList.size() != treportList.size()){
            throw new ParameterException("告警id和告警上报时间数量一致");
        }
        if(StringUtil.isNUll(alarmQuery.getOperate())){
            throw new ParameterException("操作类型为空");
        }
    }
}
