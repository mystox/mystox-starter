package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.core.constant.ScloudBusinessOperate;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.*;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import com.kongtrolink.framework.scloud.constant.RedisKey;
import com.kongtrolink.framework.scloud.dao.RealTimeDataDao;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.entity.model.SignalModel;
import com.kongtrolink.framework.scloud.entity.realtime.SignalDiInfo;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.realtime.SignalDiInfoKo;
import com.kongtrolink.framework.scloud.entity.realtime.SignalInfoEntity;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SignalQuery;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.scloud.service.FocusSignalService;
import com.kongtrolink.framework.scloud.service.RealTimeDataService;
import com.kongtrolink.framework.scloud.util.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 数据监控 - 实时数据
 * @author Mag
 */
@Service
public class RealTimeDataServiceImpl implements RealTimeDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeDataServiceImpl.class);

    @Autowired
    RealTimeDataDao realTimeDataDao;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    FocusSignalService focusSignalService;
    @Autowired
    @Lazy
    MqttSender mqttSender;
    @Value("${server.groupCode}")
    private String groupCode;
    @Autowired
    DeviceService deviceService;
    /**
     * 实时数据-获取设备列表
     *
     */
    @Override
    public ListResult<DeviceModel> getDeviceList(String uniqueCode, DeviceQuery query) throws Exception{
        List<DeviceModel> list = deviceService.findDeviceList(uniqueCode, query);
        if(list!=null && list.size()>0){
            Map<String,Integer> deviceTypeMap = realTimeDataDao.queryDeviceType(uniqueCode);
            //根据设备类型取得该设备类型有多少信号点
            for(DeviceModel model:list){
                String deviceType = model.getTypeCode();
                if(deviceTypeMap.containsKey(deviceType)){
                    model.setCountSignal(deviceTypeMap.get(deviceType));
                }
            }
        }
        int count = realTimeDataDao.getDeviceCount(uniqueCode, query);
        ListResult<DeviceModel> value = new ListResult(list,count);
        return value;
    }

    /**
     * 根据 设备code 获取 该设备所属的FSU
     *
     * @param uniqueCode 企业编码
     * @param query      查询
     * @return FSU信息
     */
    @Override
    public DeviceEntity getFsuInfoByDeviceCode(String uniqueCode, DeviceQuery query) {
        try{
            String redisKey = RedisUtil.getRealDataKey(uniqueCode,query.getDeviceCode());
            Object value = redisUtils.hget(com.kongtrolink.framework.gateway.tower.core.constant.RedisKey.ASSENT_DEVICE_INFO,redisKey);
            if(value==null){
                LOGGER.error("根据deviceCode:{} 未找到关联的FSU信息",query.getDeviceCode());
                return null;
            }
            return realTimeDataDao.getFsuInfo(uniqueCode,String.valueOf(value));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 实时数据-获取改设备的实时数据
     *
     * @param uniqueCode 企业编码
     */
    @Override
    public SignalModel getData(String uniqueCode,SignalQuery signalQuery,String userId) {
        try{
            String gatewayServerCode = signalQuery.getGatewayServerCode();
            //拼凑group
            gatewayServerCode = MqttUtils.preconditionGroupServerCode(groupCode,gatewayServerCode);
            String devType = signalQuery.getDeviceType();//设备类型
            String type = signalQuery.getType();//是否是特定类的查询
            DeviceType deviceType = realTimeDataDao.queryDeviceType(uniqueCode,devType);
            if(deviceType==null || deviceType.getSignalTypeList()==null || deviceType.getSignalTypeList().size()==0){
                return new SignalModel();
            }
            List<SignalType> signalTypes = deviceType.getSignalTypeList();
            GetDataMessage getDataMessage = getGetDataMessage(signalQuery,deviceType.getSignalTypeList());
            ;
            MsgResult result = mqttSender.sendToMqttSync(gatewayServerCode,ScloudBusinessOperate.GET_DATA,JSONObject.toJSONString(getDataMessage));
            String ack = result.getMsg();//消息返回内容
            if(StateCode.SUCCESS!=result.getStateCode()){
                LOGGER.error("获取实时数据失败:{}",result.getMsg());
                return null;
            }
            GetDataAckMessage getDataAckMessage = JSONObject.parseObject(ack,GetDataAckMessage.class);
            //获取阈值
            GetThresholdMessage getThresholdMessage = new GetThresholdMessage();
            getThresholdMessage.setFsuId(getDataMessage.getFsuId());
            getThresholdMessage.setPayload(getDataMessage.getPayload());
            //下发到网关获取实时数据
            MsgResult resultThreshold = mqttSender.sendToMqttSync(gatewayServerCode,ScloudBusinessOperate.GET_THRESHOLD,JSONObject.toJSONString(getThresholdMessage));
            String ackThreshold = resultThreshold.getMsg();//消息返回内容
            GetThresholdAckMessage getThresholdAckMessage = JSONObject.parseObject(ackThreshold,GetThresholdAckMessage.class);
            //查询关注的信号点
            List<FocusSignalEntity> focusSignalEntityList = focusSignalService.queryListByDevice(uniqueCode,signalQuery.getDeviceId(),userId);
            SignalModel signalModel = getSignalModel(uniqueCode,type,getDataAckMessage,getThresholdAckMessage, signalTypes,focusSignalEntityList);
            return signalModel;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 单个信号点的实时数据
     *
     * @param uniqueCode
     * @param signalQuery
     */
    @Override
    public Object getDataDetail(String uniqueCode, SignalQuery signalQuery) {
        try{
            String fsuCode = signalQuery.getFsuCode();
            String gatewayServerCode = signalQuery.getGatewayServerCode();
            //下发到网关获取实时数据
            gatewayServerCode = MqttUtils.preconditionGroupServerCode(groupCode,gatewayServerCode);
            GetDataMessage getDataMessage = getGetDataDetailMessage(signalQuery);
            //下发到网关获取实时数据
            MsgResult result = mqttSender.sendToMqttSync(gatewayServerCode,ScloudBusinessOperate.GET_DATA,JSONObject.toJSONString(getDataMessage));
            String ack = result.getMsg();//消息返回内容
            if(StateCode.SUCCESS!=result.getStateCode()){
                LOGGER.error("获取实时数据失败:{}",result.getMsg());
                return null;
            }
            GetDataAckMessage getDataAckMessage = JSONObject.parseObject(ack,GetDataAckMessage.class);
            Map<String,Object> valueMap = null ;//存放信号点数据 key:cntbId value:值
            if(getDataAckMessage!=null
                    && getDataAckMessage.getPayload()!=null
                    && getDataAckMessage.getPayload().getDeviceIds() !=null) {
                //单个设备查询的
                DeviceIdInfo deviceIdInfo = getDataAckMessage.getPayload().getDeviceIds().get(0);
                List<SignalIdInfo> ids = deviceIdInfo.getIds();
                String redisKey = RedisUtil.getRealDataKey(uniqueCode,deviceIdInfo.getDeviceId());
                Object value = redisUtils.hget(RedisKey.DEVICE_REAL_DATA, redisKey);
                try {
                    if (value == null) {
                        valueMap = new HashMap<>();
                    } else {
                        valueMap = JSONObject.parseObject(String.valueOf(value));
                    }
                    if (ids != null) {
                        for (SignalIdInfo signalIdInfo : ids) {
                            valueMap.put(signalIdInfo.getId(), signalIdInfo.getValue());
                        }
                    }
                    //更新redis里面的值
                    redisUtils.hset(RedisKey.DEVICE_REAL_DATA, redisKey, valueMap);
                } catch (Exception e) {
                    LOGGER.error("获取redis数据异常 {} ,{} ", RedisKey.DEVICE_REAL_DATA, redisKey);
                }
                return valueMap.get(signalQuery.getCntbId());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置值
     *
     * @param signalQuery 参数
     * @return 返回
     */
    @Override
    public SetPointAckMessage setPoint(SignalQuery signalQuery) {
        String gatewayServerCode = signalQuery.getGatewayServerCode();
        //拼凑group
        gatewayServerCode = MqttUtils.preconditionGroupServerCode(groupCode,gatewayServerCode);
        SetPointMessage setPointMessage = new SetPointMessage();
        DeviceIdEntity deviceIdEntity = getPayload(signalQuery,1);
        setPointMessage.setPayload(deviceIdEntity);
        setPointMessage.setFsuId(signalQuery.getFsuCode());
        //下发到网关
        MsgResult result =  mqttSender.sendToMqttSync(gatewayServerCode,ScloudBusinessOperate.SET_POINT,JSONObject.toJSONString(setPointMessage));
        String ack = result.getMsg();//消息返回内容
        if(StateCode.SUCCESS!=result.getStateCode()){
            LOGGER.error("设置数据失败:{}",result.getMsg());
            return null;
        }
        String redisKey = RedisUtil.getSetPointKey(signalQuery.getUniqueCode(),signalQuery.getDeviceCode(),signalQuery.getCntbId());
        //更新redis里面的值
        redisUtils.hset(RedisKey.DEVICE_SET_POINT,redisKey,signalQuery.getValue());
        SetPointAckMessage setPointAckMessage = JSONObject.parseObject(ack,SetPointAckMessage.class);
        return setPointAckMessage;
    }

    /**
     * 设置阈值
     *
     * @param signalQuery 参数
     * @return 返回
     */
    @Override
    public SetThresholdAckMessage setThreshold(SignalQuery signalQuery) {
        String gatewayServerCode = signalQuery.getGatewayServerCode();
        //拼凑group
        gatewayServerCode = MqttUtils.preconditionGroupServerCode(groupCode,gatewayServerCode);
        SetThresholdMessage setThresholdMessage = new SetThresholdMessage();
        DeviceIdEntity deviceIdEntity = getPayload(signalQuery,1);
        setThresholdMessage.setPayload(deviceIdEntity);
        setThresholdMessage.setFsuId(signalQuery.getFsuCode());
        //下发到网关
        MsgResult result = mqttSender.sendToMqttSync(gatewayServerCode,ScloudBusinessOperate.SET_THRESHOLD,JSONObject.toJSONString(setThresholdMessage));
        String ack = result.getMsg();//消息返回内容
        if(StateCode.SUCCESS!=result.getStateCode()){
            LOGGER.error("设置阈值失败:{}",result.getMsg());
            return null;
        }
        SetThresholdAckMessage setThresholdAckMessage = JSONObject.parseObject(ack,SetThresholdAckMessage.class);
        return setThresholdAckMessage;
}
    /**
     * 设置值和设置阈值的数据结构类似 精简单位
     * @param type 1=设置值 2=设置阈值
     * @return 对象
     */
    private DeviceIdEntity getPayload(SignalQuery signalQuery,int type){
        DeviceIdEntity payload = new DeviceIdEntity();
        List<DeviceIdInfo> deviceIds = new ArrayList<>();
        List<SignalIdInfo> ids = new ArrayList<>();
        SignalIdInfo signalIdInfo = new SignalIdInfo();
        signalIdInfo.setId(signalQuery.getCntbId());
        if(type==1){
            signalIdInfo.setValue(signalQuery.getValue());
        }else if(type==2){
            signalIdInfo.setThreshold(signalQuery.getValue());
        }
        ids.add(signalIdInfo);
        DeviceIdInfo deviceIdInfo = new DeviceIdInfo(signalQuery.getDeviceCode());
        deviceIdInfo.setIds(ids);
        deviceIds.add(deviceIdInfo);
        payload.setDeviceIds(deviceIds);
        return payload;
    }
    /**
     * 获取实时数据
     * 整理返回下前段展现 并存放到redis中
     */
    private SignalModel getSignalModel(String uniqueCode,String type,
                                       GetDataAckMessage getDataAckMessage,
                                       GetThresholdAckMessage getThresholdAckMessage,
                                       List<SignalType> signalTypeList,
                                       List<FocusSignalEntity> focusSignalEntityList){
        SignalModel signalModel = new SignalModel();
        signalModel.setReportTime(new Date().getTime());
        Map<String,List<SignalInfoEntity>> infoList = new HashMap<>();//前端返回值
        if(type!=null){
            List<SignalInfoEntity> typeInfoList = new ArrayList<>();
            infoList.put(type,typeInfoList);
        }
        Map<String,Object> valueMap = null ;//存放信号点数据 key:cntbId value:值
        if(getDataAckMessage!=null
                && getDataAckMessage.getPayload()!=null
                && getDataAckMessage.getPayload().getDeviceIds() !=null){
            //单个设备查询的
            DeviceIdInfo deviceIdInfo = getDataAckMessage.getPayload().getDeviceIds().get(0);
            List<SignalIdInfo> ids = deviceIdInfo.getIds();

            String redisKey = RedisUtil.getRealDataKey(uniqueCode,deviceIdInfo.getDeviceId());
            Object value = redisUtils.hget(RedisKey.DEVICE_REAL_DATA,redisKey);
            try{
                if(value==null){
                    valueMap = new HashMap<>();
                }else{
                    valueMap = JSONObject.parseObject(String.valueOf(value));
                }
                if(ids !=null){
                    for(SignalIdInfo signalIdInfo:ids){
                        valueMap.put(signalIdInfo.getId(),signalIdInfo.getValue());
                    }
                }
                //更新redis里面的值
                redisUtils.hset(RedisKey.DEVICE_REAL_DATA,redisKey,valueMap);
            }catch (Exception e){
                LOGGER.error("获取redis数据异常 {} ,{} ",RedisKey.DEVICE_REAL_DATA,redisKey);
            }
            //获取关注点
            Map<String,FocusSignalEntity> focusMap = new HashMap<>();
            if(focusSignalEntityList!=null){
                for(FocusSignalEntity focusSignalEntity:focusSignalEntityList){
                    focusMap.put(focusSignalEntity.getCntbId(),focusSignalEntity);
                }
            }
            //获取阈值
            Map<String,Object> thresholdMap = messageThresholdAckTran(getThresholdAckMessage);

            for(SignalType signalType:signalTypeList){
                if(type!=null && !type.equals(signalType.getType())){
                    //判断查询的是指定类型的信号点类型
                    continue;
                }
                String signalTypes = signalType.getType();
                Object realData = valueMap.get(signalType.getCntbId());
                Object threshold = thresholdMap.get(signalType.getCntbId());
                SignalInfoEntity signalInfoEntity = new SignalInfoEntity();
                signalInfoEntity.init(signalType,realData,threshold);
                if(infoList.containsKey(signalTypes)){
                    List<SignalInfoEntity> typeInfoList = infoList.get(signalTypes);
                    typeInfoList.add(signalInfoEntity);
                    infoList.put(signalTypes,typeInfoList);
                }else{
                    List<SignalInfoEntity> typeInfoList = new ArrayList<>();
                    typeInfoList.add(signalInfoEntity);
                    infoList.put(signalTypes,typeInfoList);
                }
                if(focusMap.containsKey(signalType.getCntbId())){
                    FocusSignalEntity focusSignalEntity = focusMap.get(signalType.getCntbId());
                    signalInfoEntity.setFocusId(focusSignalEntity.getId());
                }
            }
            signalModel.setInfoList(infoList);
        }
        return signalModel;
    }

    private Map<String,Object> messageThresholdAckTran(GetThresholdAckMessage getThresholdAckMessage){
        Map<String,Object> valueMap = new HashMap<>(); ;//存放信号点数据 key:cntbId value:值
        if(getThresholdAckMessage!=null
                && getThresholdAckMessage.getPayload()!=null
                && getThresholdAckMessage.getPayload().getDeviceIds() !=null){
            //单个设备查询的
            DeviceIdInfo deviceIdInfo = getThresholdAckMessage.getPayload().getDeviceIds().get(0);
            List<SignalIdInfo> ids = deviceIdInfo.getIds();
            try{
                if(ids !=null){
                    for(SignalIdInfo signalIdInfo:ids){
                        valueMap.put(signalIdInfo.getId(),signalIdInfo.getThreshold());
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return valueMap;
    }
    /**
     * 获取实时数据
     * 整理返回下发网关的传递参数
     */
    private GetDataMessage getGetDataMessage(SignalQuery signalQuery,List<SignalType> signalTypeList){
        GetDataMessage getDataMessage = new GetDataMessage();
        DeviceIdEntity payload = new DeviceIdEntity();
        List<DeviceIdInfo> deviceIds = new ArrayList<>();
        String type = signalQuery.getType();
        DeviceIdInfo deviceIdInfo = new DeviceIdInfo(signalQuery.getDeviceCode());
        if(type !=null && !"".equals(type)){
            List<SignalIdInfo> ids = new ArrayList<>();
            for(SignalType signalType:signalTypeList){
                if(type.equals(signalType.getType())){
                    SignalIdInfo signalIdInfo = new SignalIdInfo();
                    signalIdInfo.setId(signalType.getCntbId());
                    ids.add(signalIdInfo);
                }
            }
            deviceIdInfo.setIds(ids);
        }
        deviceIds.add(deviceIdInfo);
        payload.setDeviceIds(deviceIds);
        getDataMessage.setFsuId(signalQuery.getFsuCode());
        getDataMessage.setPayload(payload);
        return getDataMessage;
    }

    private GetDataMessage getGetDataDetailMessage(SignalQuery signalQuery){
        GetDataMessage getDataMessage = new GetDataMessage();
        DeviceIdEntity payload = new DeviceIdEntity();
        List<DeviceIdInfo> deviceIds = new ArrayList<>();

        DeviceIdInfo deviceIdInfo = new DeviceIdInfo(signalQuery.getDeviceCode());
        List<SignalIdInfo> ids = new ArrayList<>();
        SignalIdInfo signalIdInfo = new SignalIdInfo();
        signalIdInfo.setId(signalQuery.getCntbId());
        deviceIdInfo.setIds(ids);
        deviceIds.add(deviceIdInfo);
        payload.setDeviceIds(deviceIds);
        getDataMessage.setFsuId(signalQuery.getFsuCode());
        getDataMessage.setPayload(payload);
        return getDataMessage;
    }
    /**
     * 根据查询 某一个遥测信号值列表
     *
     * @param uniqueCode 企业唯一吗
     * @param query      查询参数
     * @return 信号值列表
     */
    @Override
    public List<SignalDiInfo> getSignalDiInfo(String uniqueCode, DeviceQuery query) throws Exception{
        List<SignalDiInfo> list = new ArrayList<>();
        //根据区域数查询获取站点ID列表和局站类型 封装在SignalDiInfoKo对象中
        SignalDiInfoKo ko = getSiteList(uniqueCode,query);
        Map<String, SiteEntity> siteMap =ko.getSiteMap();//局站类型Map
        List<Integer> siteIds = ko.getSiteIds();//站点ID列表
        String signalCntbId = query.getSignalCode(); //遥测信号点ID
        query.setSiteIds(siteIds);
        List<DeviceModel> deviceList = deviceService.findDeviceList(uniqueCode, query);// 采用晓龙的获取设备方法
        for(DeviceModel device:deviceList){
            SignalDiInfo info = new SignalDiInfo();
            int siteId = device.getSiteId();
            String deviceCode = device.getCode();
            if(siteMap.containsKey(String.valueOf(siteId))){
                SiteEntity siteEntity = siteMap.get(String.valueOf(siteId));
                info.setTier(siteEntity.getTierName());
                info.setSiteCode(siteEntity.getCode());
            }
            info.setSiteName(device.getSiteName());
            info.setDeviceName(device.getName());
            info.setDeviceCode(device.getCode());
            info.setValue(getRealDateCntbId(uniqueCode,deviceCode,signalCntbId));
            list.add(info);
        }

        return list;
    }

    /**
     * 根据查询 某一个遥测信号值列表 - 取得总数
     *
     * @param uniqueCode 企业唯一吗
     * @param query      查询参数
     * @return 信号值列表
     */
    @Override
    public int getSignalDiInfoNum(String uniqueCode, DeviceQuery query) throws Exception{
        //根据区域数查询获取站点ID列表和局站类型 封装在SignalDiInfoKo对象中
        SignalDiInfoKo ko = getSiteList(uniqueCode,query);
        List<Integer> siteIds = ko.getSiteIds();//站点ID列表
        query.setSiteIds(siteIds);
        return realTimeDataDao.findDeviceDiCount(uniqueCode,siteIds,query);
    }

    /**
     * 根据设备类型获取遥测信号点列表
     *
     * @param uniqueCode 企业编码
     * @param devType    设备类型
     * @param type       具体哪一类信号点 不传全部
     * @return 信号点列表
     */
    @Override
    public DeviceType getDeviceType(String uniqueCode, String devType, String type) {
        DeviceType deviceType = realTimeDataDao.queryDeviceType(uniqueCode,devType);
        if(deviceType==null || deviceType.getSignalTypeList()==null || deviceType.getSignalTypeList().size()==0){
            return null;
        }
        if(type!=null && !"".equals(type)){
            List<SignalType> newList = new ArrayList<>();
            List<SignalType> signalTypes = deviceType.getSignalTypeList();
            for(SignalType signalType:signalTypes){
                if(type.equals(signalType.getType())){
                    newList.add(signalType);
                }
            }
            deviceType.setSignalTypeList(newList);
        }
        return deviceType;
    }

    /**
     * 根据cntbId 取得实时数据
     *
     * @param uniqueCode 企业编码
     * @param deviceCode 设备code
     * @param cntbId     信号点code
     * @return 实时数据
     */
    @Override
    public String getRealDateCntbId(String uniqueCode, String deviceCode, String cntbId) {
        String redisKey = RedisUtil.getRealDataKey(uniqueCode,deviceCode);
        Object value = redisUtils.hget(RedisKey.DEVICE_REAL_DATA,redisKey);
        try{
            Map<String,Object> redisValue = JSONObject.parseObject(String.valueOf(value));
            if(redisValue!=null && redisValue.containsKey(cntbId)){
                return String.valueOf(redisValue.get(cntbId));
            }
        }catch (Exception e){
            LOGGER.error("获取redis数据异常 {} ,{} ",RedisKey.DEVICE_REAL_DATA,redisKey);
        }
        return null;
    }
    /**
     * 根据cntbId 取得下发数据
     *
     * @param uniqueCode 企业编码
     * @param deviceCode 设备code
     * @param cntbId     信号点code
     * @return 实时数据
     */
    @Override
    public String getSetPointCntbId(String uniqueCode, String deviceCode, String cntbId) {
        String redisKey = RedisUtil.getSetPointKey(uniqueCode,deviceCode,cntbId);
        Object value = redisUtils.hget(RedisKey.DEVICE_SET_POINT,redisKey);
        try{
            if(value!=null){
                return String.valueOf(value);
            }
        }catch (Exception e){
            LOGGER.error("获取redis数据异常 {} ,{} ",RedisKey.DEVICE_SET_POINT,redisKey);
        }
        return null;
    }
    /**
     * 根据 区域数 返回需要查询的站点ID列表

     */
    private SignalDiInfoKo getSiteList(String uniqueCode, DeviceQuery query){
        Map<String,SiteEntity> siteMap = new HashMap<>();
        List<Integer> siteIds = new ArrayList<>();
        List<SiteEntity> siteEntityList = realTimeDataDao.findSite(uniqueCode,query);
        for(SiteEntity siteEntity : siteEntityList){
            if(!siteMap.containsKey(siteEntity.getId())) {
                siteMap.put(String.valueOf(siteEntity.getId()), siteEntity);
            }
            siteIds.add(siteEntity.getId());
        }
        SignalDiInfoKo ko = new SignalDiInfoKo();
        ko.setSiteIds(siteIds);
        ko.setSiteMap(siteMap);
        return ko;
    }

}
