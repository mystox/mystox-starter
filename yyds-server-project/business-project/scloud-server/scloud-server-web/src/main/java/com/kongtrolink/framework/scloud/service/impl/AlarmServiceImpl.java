package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.constant.Const;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.exception.ParameterException;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.dao.AlarmDao;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.AlarmBusinessQuery;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SiteQuery;
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
    @Autowired
    AlarmBusinessService businessService;
    @Autowired
    AlarmFocusService focusService;

    @Value("${alarmModule.list:alarmRemoteList}")
    private String remoteList;
    @Value("${alarmModule.check:alarmRemoteOperate}")
    private String remoteOperate;
    @Value("${alarmModule.updateWorkInfo:alarmRemoteUpdateWork}")
    private String alarmRemoteUpdateWork;

    private static final Logger logger = LoggerFactory.getLogger(AlarmServiceImpl.class);
    /**
     * @param alarmQuery
     * @auther: liudd
     * @date: 2020/3/3 14:01
     * 功能描述:获取列表
     */
    @Override
    public JsonResult list(AlarmQuery alarmQuery) throws Exception {
        checkListPara(alarmQuery);
        //从告警业务信息表中获取数据
        AlarmBusinessQuery businessQuery = AlarmBusinessQuery.createByAlarmQuery(alarmQuery);
        List<AlarmBusiness> businessList = businessService.list(alarmQuery.getEnterpriseCode(), businessQuery);
        Map<String, AlarmBusiness> keyBusinessMap = new HashMap<>();
        List<String> keyList = new ArrayList<>();
        for(AlarmBusiness alarmBusiness : businessList){
            keyBusinessMap.put(alarmBusiness.getKey(), alarmBusiness);
            keyList.add(alarmBusiness.getKey());
        }
        alarmQuery.setKeyList(keyList);
        //具体查询历史还是实时数据，由中台告警模块根据参数判定
        try {
            MsgResult msgResult = mqttOpera.opera(remoteList, JSONObject.toJSONString(alarmQuery));
            JsonResult jsonResult = JSONObject.parseObject(msgResult.getMsg(), JsonResult.class);
            String dataStr = jsonResult.getData().toString();
            List<Alarm> alarmList = JSONObject.parseArray(dataStr, Alarm.class);
            List<String> entDevSigList = new ArrayList<>();
            Map<String, List<Alarm>> entDevSigAlarmListMap = new HashMap<>();
            for(Alarm alarm : alarmList){
                alarm.initByBusiness(keyBusinessMap.get(alarm.getKey()));
                String entDevSig = alarm.getEntDevSig();
                entDevSigList.add(entDevSig);
                List<Alarm> entDevSigAlarmList = entDevSigAlarmListMap.get(entDevSig);
                if(null == entDevSigAlarmList){
                    entDevSigAlarmList = new ArrayList<>();
                }
                entDevSigAlarmList.add(alarm);
                entDevSigAlarmListMap.put(entDevSig, entDevSigAlarmList);

            }
            //填充告警关注信息
            String operateUserId = alarmQuery.getOperatorId();
            List<AlarmFocus> alarmFocusList = focusService.listByUserIdEntDevSigs(alarmQuery.getEnterpriseCode(), operateUserId, entDevSigList);
            for(AlarmFocus alarmFocus : alarmFocusList){
                List<Alarm> entDevSigAlarmList = entDevSigAlarmListMap.get(alarmFocus.getEntDevSig());
                for(Alarm alarm : entDevSigAlarmList){
                    alarm.setFocusId(alarmFocus.getId());
                }
            }
            jsonResult.setData(alarmList);
            return jsonResult;
        }catch (Exception e){
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSONString(alarmQuery), remoteList, e.getMessage());
            throw new Exception("获取数据超时");
        }
    }

    /**
     * @auther: liudd
     * @date: 2020/3/3 15:59
     * 功能描述:填充设备，站点等信息
     */
    @Override
    public void initInfo(String uniqueCode, String serverCode, List<AlarmBusiness> businessList) {
        if(null == businessList){
            return ;
        }
        //获取站点信息
        List<String> deviceCodeList = new ArrayList<>();
        Map<String, List<AlarmBusiness>> deviceCodeAlarmListMap = new HashMap<>();
        List<String> keyList = new ArrayList<>();
        for(AlarmBusiness alarm : businessList){
            keyList.add(alarm.getKey());
            alarm.setCheckState(BaseConstant.NOCHECK);
            if(null != alarm.getCheckTime()){
                alarm.setCheckState(BaseConstant.CHECKED);
            }
            String deviceCode = alarm.getDeviceCode();
            if(!deviceCodeList.contains(deviceCode)){
                deviceCodeList.add(deviceCode);
            }
            List<AlarmBusiness> deviceCodeAlarmList = deviceCodeAlarmListMap.get(deviceCode);
            if(null == deviceCodeAlarmList){
                deviceCodeAlarmList = new ArrayList<>();
            }
            deviceCodeAlarmList.add(alarm);
            deviceCodeAlarmListMap.put(deviceCode, deviceCodeAlarmList);
        }
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setServerCode(serverCode);
        deviceQuery.setPageSize(Integer.MAX_VALUE);
        deviceQuery.setDeviceCodes(deviceCodeList);
        List<DeviceModel> deviceModelList = new ArrayList<>();
        try{
            deviceModelList = deviceService.listDeviceList(uniqueCode, deviceQuery);
        }catch (Exception e){
            e.printStackTrace();
        }
        List<String> typeCodeList = new ArrayList<>();
        for(DeviceModel deviceModel : deviceModelList){
            String code = deviceModel.getCode();
            String typeCode = deviceModel.getTypeCode();
            if(!typeCodeList.contains(typeCode)){
                typeCodeList.add(typeCode);
            }
            List<AlarmBusiness> deviceCodeAlarmList = deviceCodeAlarmListMap.get(code);
            for(AlarmBusiness alarm : deviceCodeAlarmList){
                alarm.setDeviceName(deviceModel.getName());
                alarm.setSiteCode(deviceModel.getSiteCode());
                alarm.setOperationState(deviceModel.getOperationState());
            }
        }
//        初始化站点信息
        List<String> siteCodeList = new ArrayList<>();
        Map<String, List<AlarmBusiness>> siteCodeAlarmListMap = new HashMap<>();
        for(AlarmBusiness alarm : businessList){
            String siteCode = alarm.getSiteCode();
            if(!siteCodeList.contains(siteCode)){
                siteCodeList.add(siteCode);
            }
            List<AlarmBusiness> siteIdAlarmList = siteCodeAlarmListMap.get(siteCode);
            if(null == siteIdAlarmList){
                siteIdAlarmList = new ArrayList<>();
            }
            siteIdAlarmList.add(alarm);
            siteCodeAlarmListMap.put(siteCode, siteIdAlarmList);
        }
        SiteQuery siteQuery = new SiteQuery();
        siteQuery.setServerCode(serverCode);
        siteQuery.setPageSize(Integer.MAX_VALUE);
        siteQuery.setSiteCodes(siteCodeList);
        List<SiteModel> siteModelList = siteService.findSiteList(uniqueCode, siteQuery);
        for(SiteModel siteModel : siteModelList){
            String code = siteModel.getCode();
            List<AlarmBusiness> siteIdAlarmList = siteCodeAlarmListMap.get(code);
            for(AlarmBusiness alarm : siteIdAlarmList){
                alarm.setSiteName(siteModel.getName());
                alarm.setSiteAddress(siteModel.getAddress());
                alarm.setSiteType(siteModel.getSiteType());
                alarm.setTierCode(siteModel.getTierCode());
                alarm.setTierName(siteModel.getTierName());
                alarm.setTierName(siteModel.getTierName());
            }
        }

        //填充信号点信息
        List<String> cntbIdList = new ArrayList<>();
        Map<String, List<AlarmBusiness>> cntbIdAlarmListMap = new HashMap<>();
        for(AlarmBusiness alarm : businessList){
            String signalId = alarm.getCntbId();
            if(!cntbIdList.contains(signalId)){
                cntbIdList.add(signalId);
            }
            List<AlarmBusiness> cntbIdAlarmList = cntbIdAlarmListMap.get(signalId);
            if(cntbIdAlarmList == null){
                cntbIdAlarmList = new ArrayList<>();
            }
            cntbIdAlarmList.add(alarm);
            cntbIdAlarmListMap.put(signalId, cntbIdAlarmList);
        }
        List<SignalType> signalTypeList = deviceSignalTypeService.getByCodeListCntbIdList(uniqueCode, typeCodeList, cntbIdList);
        for(SignalType signalType : signalTypeList){
            String cntbId = signalType.getCntbId();
            List<AlarmBusiness> cntbIdAlarmList = cntbIdAlarmListMap.get(cntbId);
            for(AlarmBusiness alarm : cntbIdAlarmList){
                alarm.setSignalName(signalType.getTypeName());
                alarm.setMeasurement(signalType.getMeasurement());
            }
        }
    }

    @Override
    public JSONObject operate(AlarmQuery alarmQuery) throws Exception {
        checkOperatePara(alarmQuery);
        try {
            String jsonStr = JSONObject.toJSONString(alarmQuery);
            MsgResult msgResult = mqttOpera.opera(remoteOperate, jsonStr);
            JSONObject jsonObject = JSONObject.parseObject(msgResult.getMsg(), JSONObject.class);
            return jsonObject;
        }catch (Exception e){
            logger.error(" remote call error, msg:{}, operate:{}, result:{}", JSONObject.toJSONString(alarmQuery), remoteOperate, e.getMessage());
            throw new Exception("连接超时");
        }
    }

    private void checkListPara(@RequestBody AlarmQuery alarmQuery)throws ParameterException{
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

    private void checkOperatePara(AlarmQuery alarmQuery)throws ParameterException{
        checkListPara(alarmQuery);
        //判定告警id，上报时间
        List<String> keyList = alarmQuery.getKeyList();
        List<Date> treportList = alarmQuery.getTreportList();
        if(null == keyList || keyList.size() == 0){
            throw new ParameterException("告警key为空");
        }
        if(null == treportList || treportList.size() == 0){
            throw new ParameterException("上报时间为空");
        }
        if(keyList.size() != treportList.size()){
            throw new ParameterException("告警key和告警上报时间数量一致");
        }
        if(StringUtil.isNUll(alarmQuery.getOperate())){
            throw new ParameterException("操作类型为空");
        }
    }

    @Override
    public JSONObject updateWorkInfo(AlarmQuery alarmQuery) {
        JSONObject jsonObject = new JSONObject();
        //具体查询历史还是实时数据，由中台告警模块根据参数判定
        try {
            MsgResult msgResult = mqttOpera.opera(alarmRemoteUpdateWork, JSONObject.toJSONString(alarmQuery));
            String msg = msgResult.getMsg();
            jsonObject.put("success", true);
            jsonObject.put("data", msg);

        }catch (Exception e){
            jsonObject.put("success", false);
            jsonObject.put("info", "远程调用错误");
            jsonObject.put("data", e.getMessage());
        }
        return jsonObject;
    }
}
