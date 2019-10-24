package com.kongtrolink.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.enttiy.InformMsg;
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

    @Value("${cycle.assets:theAssets}")
    private String assetsServer;
    @Value("${cycle.getCI:getCI}")
    private String getCI;
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
            //根据设备id，将通知信息分类
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
            MsgResult msgResult = mqttSender.sendToMqttSyn(assetsServer, getCI, JSONObject.toJSONString(requestEntity));
            //liuddtodo 根据返回值，填充消息中字段，最后执行发送
            String msg = msgResult.getMsg();
            CIResponseEntity ciResponseEntity = JSONObject.parseObject(msg, CIResponseEntity.class);
            //liuddtodo 需要判定失败的返回结果
            Map<String, JSONObject> deviceId_jsonObjMap = new HashMap<>();
            for (JSONObject jsonObject : ciResponseEntity.getInfos()) {
                //告警上报中的deviceId，等于资产的sn，业务+服务内唯一
                deviceId_jsonObjMap.put(jsonObject.getString("sn"), jsonObject);
            }
            //liuddtodo 还需要从云管获取区域信息

        }
    }


}
