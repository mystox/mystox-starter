package com.kongtrolink.framework.business.scloud.his.service;


import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.business.scloud.his.dao.HistoryDao;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.core.constant.ScloudBusinessOperate;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.gateway.tower.core.entity.RedisFsuInfo;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.dto.HistoryModuleDto;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.*;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import com.kongtrolink.framework.scloud.constant.RedisKey;
import com.kongtrolink.framework.scloud.entity.FocusSignalEntity;
import com.kongtrolink.framework.scloud.entity.HistoryDataEntity;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.entity.model.SignalModel;
import com.kongtrolink.framework.scloud.entity.realtime.SignalInfoEntity;
import com.kongtrolink.framework.scloud.query.SignalQuery;
import com.kongtrolink.framework.scloud.util.redis.RedisUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * MQTT 数据结构上报信息任务
 *
 */
@Service
public class HistoryService {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    MqttSender mqttSender;
    @Autowired
    HistoryDao historyDao;
    @Value("${server.group}")
    private String group;

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryService.class);


    public void scloudHistory(String message){
        try {
            HistoryModuleDto dto = JSONObject.parseObject(message,HistoryModuleDto.class);
            RedisFsuInfo fsu = dto.getRedisFsuInfo();
            String uniqueCode = dto.getUniqueCode();
            String fsuShortCode = fsu.getShortCode();
            String gatewayCode = fsu.getGatewayServerCode();
            GetDataMessage getDataMessage = getGetDataMessage(fsuShortCode);
            //下发到网关获取实时数据
            String serverCode = MqttUtils.preconditionGroupServerCode(group,gatewayCode);
            MsgResult result = mqttSender.sendToMqttSync(serverCode,ScloudBusinessOperate.GET_DATA,JSONObject.toJSONString(getDataMessage));
            String ack = result.getMsg();//消息返回内容
            GetDataAckMessage getDataAckMessage = JSONObject.parseObject(ack,GetDataAckMessage.class);
            saveData(uniqueCode,fsuShortCode,getDataAckMessage);
            LOGGER.info("开始轮询:fsu :{} 历史数据完成...",fsuShortCode);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取实时数据
     * 整理返回下前段展现 并存放到redis中
     */
    private void saveData(String uniqueCode,String fsuCode,GetDataAckMessage getDataAckMessage){
        Map<String,Object> valueMap  ;//存放信号点数据 key:cntbId value:值
        if(getDataAckMessage!=null
                && getDataAckMessage.getPayload()!=null
                && getDataAckMessage.getPayload().getDeviceIds() !=null){
            //单个设备查询的
            for(DeviceIdInfo deviceIdInfo:getDataAckMessage.getPayload().getDeviceIds()){
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
                    //保存到数据库中
                    HistoryDataEntity historyDataEntity = new HistoryDataEntity();
                    historyDataEntity.setDeviceCode(deviceIdInfo.getDeviceId());
                    historyDataEntity.setFsuCode(fsuCode);
                    historyDataEntity.setTime(new Date().getTime());
                    historyDataEntity.setValue(valueMap);
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//                    String tableTime = sdf.format(new Date());
                    historyDao.saveHistory(uniqueCode,historyDataEntity);
                }catch (Exception e){
                    e.printStackTrace();
                    LOGGER.error("历史数据异常 {} ,{} ",uniqueCode,fsuCode);
                }


            }
        }
    }
    /**
     * 获取实该FSU的所有设备的数据
     * 整理返回下发网关的传递参数
     */
    private GetDataMessage getGetDataMessage(String fsuCode){
        GetDataMessage getDataMessage = new GetDataMessage();
        DeviceIdEntity payload = new DeviceIdEntity();
        getDataMessage.setFsuId(fsuCode);
        getDataMessage.setPayload(payload);
        return getDataMessage;
    }
}
