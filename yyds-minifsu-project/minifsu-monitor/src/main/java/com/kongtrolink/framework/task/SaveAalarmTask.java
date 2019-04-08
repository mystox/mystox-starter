package com.kongtrolink.framework.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.rpc.RpcModuleBase;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.jsonType.JsonSignal;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * @Auther: liudd
 * @Date: 2019/4/3 11:25
 * @Description:保存告警task
 */
public class SaveAalarmTask extends RpcModuleBase implements Runnable{

    private String hostname;
    private int port;
    private RpcModule rpcModule;
    private JsonFsu jsonFsu;
    private RedisUtils redisUtils;
    private String sn__alarm_hash = RedisHashTable.SN_ALARM_HASH;

    public SaveAalarmTask(String hostname, int port,RpcModule rpcModule, JsonFsu jsonFsu, RedisUtils redisUtils) {
        this.hostname = hostname;
        this.port = port;
        this.rpcModule = rpcModule;
        this.jsonFsu = jsonFsu;
        this.redisUtils = redisUtils;
    }

    @Override
    public void run() {
        InetSocketAddress registAddr = new InetSocketAddress(hostname, port);
        try {
            ModuleMsg msg = new ModuleMsg();
            msg.setPayload(JSONObject.parseObject(JSON.toJSONString(jsonFsu)));
            msg.setPktType(PktType.ALARM_REGISTER);
            RpcNotifyProto.RpcMessage rpcMessage = postMsg(registAddr, JSON.toJSONString(msg));
            String payload = rpcMessage.getPayload();
            ModuleMsg moduleMsg = JSON.parseObject(payload, ModuleMsg.class);
            JSONObject resultPayload = moduleMsg.getPayload();
            Object res = resultPayload.get("result");
            //如果成功，重新遍历告警
            boolean result = "1".equals(res) ? true : false;
            if(result){
                List<Alarm> alarmList = new ArrayList<>();
                parseAlarm(jsonFsu, alarmList);
                if(!alarmList.isEmpty()) {
                    String alarmMsg = createAlarmMsg(alarmList);
                    //发送命令给服务中心保存告警
                    postMsg(registAddr, alarmMsg);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //保存实时告警到redis中
        redisUtils.hset(sn__alarm_hash, jsonFsu.getSN(), JSON.toJSONString(jsonFsu));
    }

    /**
     * @auther: liudd
     * @date: 2019/4/3 16:44
     * 功能描述:修改告警状态
     */
    private void parseAlarm(JsonFsu fsu, List<Alarm> alarmList){
        Iterator<JsonDevice> deviceIterator = fsu.getData().iterator();
        while (deviceIterator.hasNext()){
            JsonDevice device = deviceIterator.next();
            Iterator<JsonSignal> signalIterator = device.getSignalList().iterator();
            while(signalIterator.hasNext()){
                JsonSignal jsonSignal = signalIterator.next();
                Map<String, Alarm> alarmMap = jsonSignal.getAlarmMap();
                Iterator<Map.Entry<String, Alarm>> iterator = alarmMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, Alarm> next = iterator.next();
                    Alarm alarm = next.getValue();
                    byte link = alarm.getLink();
                    if( (link & EnumAlarmStatus.BEGINREPORT.getValue()) == 0 ){
                        link = (byte)(link | EnumAlarmStatus.BEGINREPORT.getValue());
                        alarm.setLink(link);
                    }else{
                        link = (byte)(link | EnumAlarmStatus.ENDREPORT.getValue());
                        alarm.setLink(link);
                        alarmList.add(alarm);
                        iterator.remove();
                    }
                }
                if(jsonSignal.getAlarmMap().isEmpty()){
                    signalIterator.remove();
                }
            }
            if(device.getSignalList().isEmpty()){
                deviceIterator.remove();
            }
        }
    }

    /**
     * 发送至事务处理的消息
     *
     * @param msgId
     * @param payload
     * @return
     */
//    private JSONObject sendPayLoad(String msgId, String payload, String host, int port) {
//        JSONObject result = new JSONObject();
//        RpcNotifyProto.RpcMessage response ;
//        try {
//            response = rpcModule.postMsg(msgId, new InetSocketAddress(host, port), payload);
//            if (RpcNotifyProto.MessageType.ERROR.equals(response.getType()))//错误请求信息
//            {
//                result.put("result", 0);
//            } else {
//                return JSONObject.parseObject(response.getPayload());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.put("result", 0);
//        }
//        return result;
//    }

    /**
     * @auther: liudd
     * @date: 2019/4/1 19:56
     * 功能描述:构造保存告警报文
     */
    private String createAlarmMsg(List<Alarm> alarmList){
        ModuleMsg moduleMsg = new ModuleMsg();
        JSONObject o = new JSONObject();
        o.put("list", alarmList);
        moduleMsg.setPktType(PktType.ALARM_SAVE);
        moduleMsg.setPayload(o);
        return JSON.toJSONString(moduleMsg);
    }
}
