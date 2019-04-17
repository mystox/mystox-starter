package com.kongtrolink.framework.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
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
import java.text.SimpleDateFormat;
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
    private Map<String, JSONObject>  alarmMap;
    private RedisUtils redisUtils;
    private String sn__alarm_hash = RedisHashTable.SN_ALARM_HASH;

    public SaveAalarmTask(String hostname, int port,RpcModule rpcModule, JsonFsu jsonFsu, Map<String, JSONObject>  alarmMap, RedisUtils redisUtils, RpcClient rpcClient) {
        super(rpcClient);
        this.hostname = hostname;
        this.port = port;
        this.rpcModule = rpcModule;
        this.jsonFsu = jsonFsu;
        this.alarmMap = alarmMap;
        this.redisUtils = redisUtils;
    }

    @Override
    public void run() {
        InetSocketAddress registAddr = new InetSocketAddress(hostname, port);
        try {
            ModuleMsg msg = new ModuleMsg();
            msg.setSN(jsonFsu.getSN());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("alarmMap", alarmMap);
            msg.setPayload(jsonObject);
            msg.setPktType(PktType.ALARM_REGISTER);
            RpcNotifyProto.RpcMessage rpcMessage = rpcModule.postMsg("", registAddr, JSON.toJSONString(msg));
            String payload = rpcMessage.getPayload();
            JSONObject resultJson = JSONObject.parseObject(payload);
            String result1 = resultJson.getString("result");
            //如果成功，重新遍历告警
            if("1".equals(result1)){
//                Map<String, Object> resolveAlarmMap = new HashMap<>();
                List<Alarm> resolveAlarmList = new ArrayList<>();
                List<String> resolveKey = new ArrayList<>();
                Map<String, JSONObject> saveRedisAlarmMap = new HashMap<>();
                parseAlarm(alarmMap, resolveAlarmList, resolveKey, saveRedisAlarmMap);
                if(!resolveAlarmList.isEmpty()) {
                    String alarmMsg = createAlarmMsg(resolveAlarmList);
                    //发送命令给服务中心保存告警
                    rpcModule.postMsg("", registAddr, alarmMsg);
                }
                alarmMap = saveRedisAlarmMap;
                for(String key : resolveKey) {
                    redisUtils.hdel(sn__alarm_hash + jsonFsu.getSN(), key);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        redisUtils.hmset(sn__alarm_hash + jsonFsu.getSN(), alarmMap);
    }

    /**
     * @auther: liudd
     * @date: 2019/4/16 16:03
     * 功能描述:告警注册后将告警保存到redis中和mongdb中
     */
    private void parseAlarm(Map<String, JSONObject> alarmMap, List<Alarm> resolveAlarmList, List<String> resolveKey,
                            Map<String, JSONObject> saveRedisAlarmMap) {
        for(String key : alarmMap.keySet()){
            Alarm alarm = JSONObject.parseObject(alarmMap.get(key).toString(), Alarm.class);
            byte link = alarm.getLink();
            if( (link & EnumAlarmStatus.BEGINREPORT.getValue()) == 0 ){
                link = (byte)(link | EnumAlarmStatus.BEGINREPORT.getValue());
                alarm.setLink(link);
                saveRedisAlarmMap.put(key, (JSONObject)JSONObject.toJSON(alarm));
            }else if((link & EnumAlarmStatus.REALEND.getValue()) != 0){//这是消除的告警，注册成功后直接删除，并保存到mongdb中
                link = (byte)(link | EnumAlarmStatus.ENDREPORT.getValue());
                alarm.setLink(link);
                resolveAlarmList.add(alarm);
                resolveKey.add(key);
            }else{
                saveRedisAlarmMap.put(key, (JSONObject)JSONObject.toJSON(alarm));
            }
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/4/1 19:56
     * 功能描述:构造保存告警报文
     */
    private String createAlarmMsg(List<Alarm> resolveAlarmList){
        ModuleMsg moduleMsg = new ModuleMsg();
        JSONObject o = new JSONObject();
        o.put("list", resolveAlarmList);
        moduleMsg.setPktType(PktType.ALARM_SAVE);
        moduleMsg.setPayload(o);
        return JSON.toJSONString(moduleMsg);
    }


    public static void main(String[] a){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(1555483410836l);
        System.out.println("time:" + format.format(date));
    }
}
