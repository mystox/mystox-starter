package com.kongtrolink.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.dao.InformMsgDao;
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
            List<InformMsg> fullInformList = new ArrayList<>();
            List<String> addressList = new ArrayList<>();
            fullDeviceInfo(informMsgList, fullInformList, addressList);
            informMsgList = null;
            if(fullInformList.size() == 0){
                return ;
            }

            //liuddtodo 从资管根据设备id列表，获取地区编码
            //liuddtodo 根据地区编码列表，从云管获取各个地区名称
            //liuddtodo 根据地区编码列表，从云管获取各个地区用户列表（userid,username, phone, email）
            //填充地区名称，以及判定用户权限
            List<InformMsg> realInformList = new ArrayList<>();     //最终实际需要发送的通知
            initAddressInfo(fullInformList, realInformList, addressList);

            //调用投递动作接口。
            for(InformMsg informMsg : realInformList){
                mqttSender.sendToMqtt(informMsg.getUniqueCode(), informMsg.getOperateCode(), JSONObject.toJSONString(informMsg));
            }
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/10/25 14:51
     * 功能描述:填充设备地址信息或其他设备信息
     */
    private void fullDeviceInfo(List<InformMsg> informMsgList, List<InformMsg> fullInformList, List<String> addressList){
        //未获取到设备信息的通知规则
        List<InformMsg> noFullInformList = new ArrayList<>();
        //1，根据设备id，将通知信息分类，获取设备名称和地区编码
        Map<String, List<InformMsg>> deviceId_InformMsgListMap = new HashMap<>();
        List<String> deviceIdList = new ArrayList<>();
        for(InformMsg informMsg : informMsgList){
            String enterpriseCode = informMsg.getEnterpriseCode();
            String serverCode = informMsg.getServerCode();
            String deviceId = informMsg.getDeviceId();
            String key = enterpriseCode + serverCode + deviceId;
            if(!deviceIdList.contains(deviceId)){
                deviceIdList.add(deviceId);
            }

            List<InformMsg> msgList = deviceId_InformMsgListMap.get(key);
            if(null == msgList){
                msgList = new ArrayList<>();
            }
            msgList.add(informMsg);
            deviceId_InformMsgListMap.put(key, msgList);
        }

        //从资产管理获取设备信息
        CIRequestEntity requestEntity = new CIRequestEntity();
        requestEntity.setIds(deviceIdList);
        //liuddtodo 需要捕捉超时异常
        MsgResult msgResult = mqttSender.sendToMqttSyn(assetsServer, getCI, JSONObject.toJSONString(requestEntity));
        String msg = msgResult.getMsg();
        CIResponseEntity ciResponseEntity = JSONObject.parseObject(msg, CIResponseEntity.class);
        for (JSONObject jsonObject : ciResponseEntity.getInfos()) {
            //告警上报中的deviceId，等于资产的sn，业务+服务内唯一
            String enterpriseCode = jsonObject.getString("enterpriseCode");
            String serverCode = jsonObject.getString("serverCode");
            String sn = jsonObject.getString("sn");
            String key = enterpriseCode + serverCode + sn;
            String address = jsonObject.getString("address");
            if(!addressList.contains(address)){
                addressList.add(address);
            }
            List<InformMsg> informMsgs = deviceId_InformMsgListMap.get(key);
            if(null != informMsgs) {
                for (InformMsg informMsg : informMsgs) {
                    informMsg.setAddress(address);
                }
            }
            fullInformList.addAll(informMsgs);
            deviceId_InformMsgListMap.remove(key);
        }
        for(String key : deviceId_InformMsgListMap.keySet()){
            noFullInformList.addAll(deviceId_InformMsgListMap.get(key));
        }
        //保存
        informMsgDao.save(noFullInformList);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/25 15:36
     * 功能描述:从云管获取地区名称和用户权限
     * 这里可能有两个接口，分别获取地区名称和管理权限用户id列表
     */
    private void initAddressInfo(List<InformMsg> fullInformList, List<InformMsg> realInformList,List<String> addressList){
        AddressInfoRequest addressInfoRequest = new AddressInfoRequest();
        addressInfoRequest.setAddressList(addressList);
        MsgResult addInfoResult = mqttSender.sendToMqttSyn(yunguanServer, getAddressInfo, JSONObject.toJSONString(addressInfoRequest));
        String addInfoResultMsg = addInfoResult.getMsg();
        //liuddtodo 需要判定是否成功，并捕捉超时异常
        List<JSONObject> jsonObjectList = JSON.parseArray(addInfoResultMsg, JSONObject.class);

    }
}
