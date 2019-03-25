package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.MqttStandardMessage;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.rpc.RpcModuleBase;
import com.kongtrolink.framework.core.service.ModuleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Created by mystoxlol on 2019/3/22, 9:04.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class RpcModule extends RpcModuleBase implements ModuleInterface
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${rpc.business.hostname}")
    private String host;
    @Value("${rpc.business.port}")
    private int port;

    public <T> T syncRequestData(MqttStandardMessage message, Class<T> clazz){
        return syncRequestData(message, clazz,null);
    }

 public <T> T syncRequestData(MqttStandardMessage message,  Class<T> clazz, Long timeOut){
        try
        {
            RpcNotifyProto.RpcMessage result = super.postMsg(UUID.randomUUID().toString(), new InetSocketAddress(host, port), JSONObject.toJSONString(message),timeOut);
            String payload = result.getPayload();
            T data = JSON.parseObject(payload, clazz);
            return data;
        } catch (IOException e)
        {
            logger.error("message 解析报错 {}");
            e.printStackTrace();
            return null;
        }
    }


}