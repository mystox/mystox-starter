package com.kongtrolink.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.enttiy.InformMsg;
import com.kongtrolink.framework.mqtt.AddressInfoRequest;
import com.kongtrolink.framework.mqtt.CIRequestEntity;
import com.kongtrolink.framework.mqtt.CIResponseEntity;
import com.kongtrolink.framework.service.MqttSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
public class AlarmDeliverService {

    @Autowired
    MqttSender mqttSender;
    @Autowired
    SendService sendService;

    @Value("${deliver.count:300}")
    private int count;
    @Value("${deliver.time:5}")
    private int time;
    private int currentTime = 0;

    @Value("${deliver.assets:theAssets}")
    private String assetsServer;
    @Value("${deliver.getCI:getCI}")
    private String getCI;
    @Value("${deliver.yunguan:theAssets}")
    private String yunguanServer;
    @Value("${deliver.yunguan:getAddressInfo}")
    private String getAddressInfo;

    private List<InformMsg> informMsgList = new ArrayList<>();

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
            List<InformMsg> informMsgList = beformHandle();
            if(null == informMsgList){
                return ;
            }
            //1，根据设备id，将通知信息分类，获取设备名称和地区编码
            Map<String, List<InformMsg>> deviceId_InformMsgListMap = new HashMap<>();
            List<String> deviceIdList = new ArrayList<>();
            for(InformMsg informMsg : informMsgList){
                String deviceId = informMsg.getDeviceId();
                if(!deviceIdList.contains(deviceId)){
                    deviceIdList.add(deviceId);
                }
                List<InformMsg> msgList = deviceId_InformMsgListMap.get(deviceId);
                if(null == msgList){
                    msgList = new ArrayList<>();
                }
                msgList.add(informMsg);
                deviceId_InformMsgListMap.put(deviceId, msgList);
            }
            CIRequestEntity requestEntity = new CIRequestEntity();
            requestEntity.setIds(deviceIdList);
            //liuddtodo 需要捕捉超时异常
            MsgResult msgResult = mqttSender.sendToMqttSyn(assetsServer, getCI, JSONObject.toJSONString(requestEntity));
            String msg = msgResult.getMsg();
            CIResponseEntity ciResponseEntity = JSONObject.parseObject(msg, CIResponseEntity.class);
            Map<String, JSONObject> deviceId_jsonObjMap = new HashMap<>();
            List<String> addressList = new ArrayList<>();
            for (JSONObject jsonObject : ciResponseEntity.getInfos()) {
                //告警上报中的deviceId，等于资产的sn，业务+服务内唯一
                deviceId_jsonObjMap.put(jsonObject.getString("sn"), jsonObject);
                String address = jsonObject.getString("address");
                if(!addressList.contains(address)){
                    addressList.add(address);
                }
            }
            //填充设备编码信息
            for(InformMsg informMsg : informMsgList){
                String deviceId = informMsg.getDeviceId();
                JSONObject jsonObject = deviceId_jsonObjMap.get(deviceId);
                if(null == jsonObject){
                    //该设备没有获取到设备信息，记录异常，不发送消息
                    continue;
                }
                informMsg.setDeviceName(jsonObject.getString("name"));
                informMsg.setAddress(jsonObject.getString("address"));


            }
            //2，从云管获取设备信息
            AddressInfoRequest addressInfoRequest = new AddressInfoRequest();
            addressInfoRequest.setAddressList(addressList);
            MsgResult addInfoResult = mqttSender.sendToMqttSyn(yunguanServer, getAddressInfo, JSONObject.toJSONString(addressInfoRequest));
            String addInfoResultMsg = addInfoResult.getMsg();
            //liuddtodo 需要判定是否成功，并捕捉超时异常
            List<JSONObject> jsonObjectList = JSON.parseArray(addInfoResultMsg, JSONObject.class);


        }
    }


}
