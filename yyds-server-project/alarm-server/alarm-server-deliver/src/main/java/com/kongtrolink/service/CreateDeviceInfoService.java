package com.kongtrolink.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.dao.InformMsgDao;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.enttiy.InformMsg;
import com.kongtrolink.framework.mqtt.*;
import com.kongtrolink.framework.service.MqttSender;
import org.aspectj.weaver.Dump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: liudd
 * @Date: 2019/10/22 08:58
 * @Description:轮询获取对应的设备信息和区域信息，并调用对应的投递动作模块
 * 1，从资产管理获取设备名称，地区编码字段
 * 2，根据地区编码，从云管获取地区名称和拥有该地区的用户id
 * 3，没有地区权限的用户，不需要发送推送
 * 4，调用推送模块，发送推送
 */
@Service
public class CreateDeviceInfoService {

    @Autowired
    MqttSender mqttSender;
    @Autowired
    InformMsgDao informMsgDao;

    @Value("${deliver.count:300}")
    private int count;
    @Value("${deliver.time:5}")
    private int time;
    private int currentTime = 0;

    @Value("${deliver.assets:theAssets}")
    private String assetsServer;
    @Value("${deliver.getCI:getCI}")
    private String getCI;
    @Value("${yunguan.serverVersoin:AUTH_PLATFORM_1.0.0}")
    private String yunguanServerVersion;
    @Value("${yunguan.getRegionCodeEntity:getRegionCodeEntity}")
    private String getRegionCodeEntity;
    @Value("${yunguan.getUserListByRegionCodes:getUserListByRegionCodes}")
    private String getUserListByRegionCodes;
    @Resource(name = "getUserExecutor")
    ThreadPoolTaskExecutor getUserExecutor;

    private List<InformMsg> informMsgList = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(CreateDeviceInfoService.class);

    public synchronized void handleInformMsgList(List<InformMsg> msgList, String type){
        if(Contant.ZERO.equals(type)){
            informMsgList.clear();
            currentTime = 0;
        }else{
            if(null != msgList){
                informMsgList.addAll(msgList);
            }
        }
    }

    private synchronized List<InformMsg> beformHandle(){
        int size = informMsgList.size();
        if(size == 0){
            return null;
        }
        if(size < count && currentTime<time){
            currentTime ++;
            return null;
        }
        List<InformMsg> list = new ArrayList<>();
        list.addAll(informMsgList);
        handleInformMsgList(null, Contant.ZERO);
        return list;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/25 10:23
     * 功能描述:服务启动时调用
     */
    public void handle(){
        ScheduledExecutorService handleScheduler = Executors.newSingleThreadScheduledExecutor();
        handleScheduler.scheduleWithFixedDelay(new HandleTask(), 10 * 1000, 10 * 1000,
                TimeUnit.MILLISECONDS);
    }

    class HandleTask implements Runnable{
        @Override
        public void run(){
            List<InformMsg> handleInformMsgList = beformHandle();
            if(null == handleInformMsgList || handleInformMsgList.size() == 0){
                return ;
            }
            //从资管根据设备id列表，获取地区编码
            boolean getAddressResult = fullDeviceInfo(handleInformMsgList);
            if(!getAddressResult){   //mqtt消息异常，则不需要其他投递操作
                return ;
            }
            //统一获取并填充地区名称
            boolean addressNameResult = getAddressName(handleInformMsgList);
            if(!addressNameResult){
                return ;
            }
            //将需要获取用户权限的投递对象，放入另一个task处理
            //服务分类将投递对象分类
            Map<String, List<InformMsg>> serverInformListMap = new HashMap<>();
            for(InformMsg informMsg : handleInformMsgList){
//                String key = informMsg.getEnterpriseCode() + Contant.UNDERLINE + informMsg.getServerCode();
                String key = informMsg.getServerCode();
                List<InformMsg> informMsgList = serverInformListMap.get(key);
                if(null == informMsgList){
                    informMsgList = new ArrayList<>();
                }
                serverInformListMap.put(key, informMsgList);
            }
            for(String key : serverInformListMap.keySet()){
                getUserExecutor.execute(()->getUserInfo(key, serverInformListMap.get(key)));
            }
        }
    }

    private boolean fullDeviceInfo(List<InformMsg> handleInformMsgList){
        //1，根据设备id，将通知信息分类，获取设备名称和地区编码
        Map<String, List<InformMsg>> enterServerDeviceIdInformMsgListMap = new HashMap<>();
        List<String> deviceIdList = new ArrayList<>();
        for(InformMsg informMsg : handleInformMsgList){
            String deviceId = informMsg.getDeviceId();
            if(!deviceIdList.contains(deviceId)){
                deviceIdList.add(deviceId);
            }
            String enterpriseCode = informMsg.getEnterpriseCode();
            String serverCode = informMsg.getServerCode();
            String key = enterpriseCode + Contant.UNDERLINE + serverCode + Contant.UNDERLINE + deviceId;
            List<InformMsg> msgList = enterServerDeviceIdInformMsgListMap.get(key);
            if(null == msgList){
                msgList = new ArrayList<>();
            }
            msgList.add(informMsg);
            enterServerDeviceIdInformMsgListMap.put(key, msgList);
        }
        //从资产管理获取设备信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sns", deviceIdList);
        try {
            MsgResult msgResult = mqttSender.sendToMqttSyn(assetsServer, getCI, jsonObject.toJSONString());
            logger.info("获取设备信息返回结果：{}", JSON.toJSON(msgResult));
            if(1 == msgResult.getStateCode()) {
                String msg = msgResult.getMsg();
                return getDeviceSucc(msg, handleInformMsgList, enterServerDeviceIdInformMsgListMap);
            }else{
                handleMqttFail(handleInformMsgList);
                return false;
            }
        }catch (Exception e){
            //连接超时，已经到达投递重复次数的消息则当做未获取到设备信息处理
            logger.info("MQTT服务连接超时");
            handleMqttFail(handleInformMsgList);
            return false;
        }
    }

    private boolean getDeviceSucc(String msg, List<InformMsg> handleInformMsgList,
                               Map<String, List<InformMsg>> enterServerDeviceIdInformMsgListMap){
        CIResponseEntity ciResponseEntity = JSONObject.parseObject(msg, CIResponseEntity.class);
        for (JSONObject jsonObject : ciResponseEntity.getInfos()) {
            String enterpriseCode = jsonObject.getString("enterpriseCode");
            String serverCode = jsonObject.getString("serverCode");
            String sn = jsonObject.getString("sn");
            String key = enterpriseCode+ Contant.UNDERLINE + serverCode + Contant.UNDERLINE + sn;
            String address = jsonObject.getString("address");
            List<InformMsg> informMsgs = enterServerDeviceIdInformMsgListMap.get(key);
            if (null != informMsgs) {
                for (InformMsg informMsg : informMsgs) {
                    informMsg.setAddress(address);
                }
            }
            enterServerDeviceIdInformMsgListMap.remove(key);
        }
        //未被移除的key，说明资管没有该设备信息
        if(enterServerDeviceIdInformMsgListMap.size() > 0) {
            List<InformMsg> noFullInformList = new ArrayList<>();
            for (String key : enterServerDeviceIdInformMsgListMap.keySet()) {
                List<InformMsg> noDeviceInformList = enterServerDeviceIdInformMsgListMap.get(key);
                for (InformMsg informMsg : noDeviceInformList) {
                    informMsg.setResult(Contant.MQTT_RES_NODATA);
                }
                noFullInformList.addAll(noDeviceInformList);
            }
            //保存没有设备信息的投递信息
            informMsgDao.save(noFullInformList);
            //移除没有设备信息的投递对象
            handleInformMsgList.retainAll(noFullInformList);
            if (handleInformMsgList.size() == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/31 11:20
     * 功能描述:mqtt消息失败
     */
    public void handleMqttFail(List<InformMsg> handleInformMsgList){
        List<InformMsg> saveInformList = new ArrayList<>();
        for(InformMsg informMsg : handleInformMsgList){
            int currentTime = informMsg.getCurrentTime() + 1;
            //如果当前消息重发次数等于规定次数，则不再重发，按照没有设备信息处理
            if(currentTime >= informMsg.getCount()){
                informMsg.setResult(Contant.MQTT_RES_FAIL);
                saveInformList.add(informMsg);
            }else{
                informMsg.setCurrentTime(currentTime+1);
            }
        }
        if(saveInformList.size() > 0) {
            //保没有设备信息的投递对象
            informMsgDao.save(saveInformList);
            //移除所有重复次数超过的投递对象
            handleInformMsgList.removeAll(saveInformList);
            if(handleInformMsgList.size()>0){        //如果有重复次数内的投递对象，加入队列，下次重复投递
                handleInformMsgList(handleInformMsgList, Contant.ONE);
            }
        }
    }

    private boolean getAddressName(List<InformMsg> handleInformMsgList){
        Map<String, List<InformMsg>> addressInformListMap = new HashMap<>();
        List<String> addressList = new ArrayList<>();
        for(InformMsg informMsg : handleInformMsgList){
            addressList.add(informMsg.getAddress());
            List<InformMsg> addInformList = addressInformListMap.get(informMsg.getAddress());
            if(null == addInformList){
                addInformList = new ArrayList<>();
            }
            addInformList.add(informMsg);
            addressInformListMap.put(informMsg.getAddress(), addInformList);
        }
        try{
            MsgResult msgResult = mqttSender.sendToMqttSyn(yunguanServerVersion, getRegionCodeEntity, addressList.toString());
            int stateCode = msgResult.getStateCode();
            if(1 == stateCode){
                String msg = msgResult.getMsg();
                return getAddressSucc(msg, addressInformListMap, handleInformMsgList);
            }else{
                handleMqttFail(handleInformMsgList);
                return false;
            }
        }catch (Exception e){
            handleMqttFail(handleInformMsgList);
            return false;
        }

    }

    private boolean getAddressSucc(String msg, Map<String, List<InformMsg>> addressInformListMap, List<InformMsg> handleInformMsgList){
        List<Region> regionList = JSONArray.parseArray(msg, Region.class);
        for (Region region : regionList){
            String code = region.getCode();
            List<InformMsg> informMsgList = addressInformListMap.get(code);
            if(null != informMsgList){
                for (InformMsg informMsg : informMsgList){
                    informMsg.setAddressName(region.getName());
                }
            }
            addressInformListMap.remove(code);
        }
        //没有获取到地区，则存入设备
        if(addressInformListMap.size()>0){
            List<InformMsg> noAddressInformList = new ArrayList<>();
            for(String code : addressInformListMap.keySet()){
                List<InformMsg> msgs = addressInformListMap.get(code);
                for(InformMsg informMsg : msgs){
                    informMsg.setResult(Contant.MQTT_RES_NOADDRESS);
                    noAddressInformList.add(informMsg);
                }
            }
            informMsgDao.save(noAddressInformList);     //云管没有地区的投递对象，直接持久化，不在做投递处理
            handleInformMsgList.retainAll(noAddressInformList);
            if(handleInformMsgList.size() == 0){
                return false;
            }
        }
        return true;
    }

    public void getUserInfo(String key, List<InformMsg> informMsgList){
        List<String> userIds = new ArrayList<>();
        Map<String, List<InformMsg>> userIdInformListMap =new HashMap<>();
        for(InformMsg informMsg : informMsgList){
            userIds.add(informMsg.getUserId());
            List<InformMsg> userIdInformList = userIdInformListMap.get(informMsg.getUserId());
            if(null == userIdInformList){
                userIdInformList = new ArrayList<>();
            }
            userIdInformList.add(informMsg);
            userIdInformListMap.put(informMsg.getUserId(), userIdInformList);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("serverCode", key);
        jsonObject.put("userIds", userIds);
        try {
            MsgResult msgResult = mqttSender.sendToMqttSyn(yunguanServerVersion, getUserListByRegionCodes, jsonObject.toJSONString());
            //填充用户信息
            initUserInfo(msgResult.getMsg(), informMsgList, userIdInformListMap);
            for(InformMsg informMsg : informMsgList){
                mqttSender.sendToMqtt(informMsg.getServerVerson(), informMsg.getOperateCode(), JSONObject.toJSONString(informMsg));
            }
        }catch (Exception e){
            handleMqttFail(informMsgList);
        }
    }

    private void initUserInfo(String msg, List<InformMsg> informMsgList, Map<String, List<InformMsg>> userIdInformListMap){
        List<InformMsg> noRegionInformList = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(msg, JSONObject.class);
        for(String userId : jsonObject.keySet()){
            List<InformMsg> userIdInformList = userIdInformListMap.get(userId);
            if(null == userIdInformList){
                continue;
            }
            JSONObject userInfo = (JSONObject)jsonObject.get(userId);
            for(InformMsg informMsg : userIdInformList){
                List<String> regions = (List<String>)jsonObject.get("region");
                if(!regions.contains(informMsg.getAddress())){
                    noRegionInformList.add(informMsg);
                    continue;
                }
                String type = informMsg.getType();
                if(Contant.TEMPLATE_MSG.equals(type)){
                    informMsg.setInformAccount(userInfo.getString("phone"));
                }else if(Contant.TEMPLATE_EMAIL.equals(type)){
                    informMsg.setInformAccount(userInfo.getString("email"));
                }else{//如果是APP推送，则直接使用userID
                    informMsg.setInformAccount(userId);
                }
            }
            userIdInformListMap.remove(userId);
        }
        //排除没有地区权限的消息投递对象
        informMsgList.removeAll(noRegionInformList);
    }
}
