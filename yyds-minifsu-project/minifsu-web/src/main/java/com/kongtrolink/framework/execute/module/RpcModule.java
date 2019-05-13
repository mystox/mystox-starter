package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.rpc.RpcModuleBase;
import com.kongtrolink.framework.core.service.ModuleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

/**
 * Created by mystoxlol on 2019/3/22, 9:04.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
@RefreshScope
public class RpcModule extends RpcModuleBase implements ModuleInterface
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${rpc.controller.hostname}")
    private String host;
    @Value("${rpc.controller.port}")
    private int port;

    public <T> T syncRequestData(ModuleMsg message, Class<T> clazz){
        return syncRequestData(message, clazz,null);
    }

 public <T> T syncRequestData(ModuleMsg message,  Class<T> clazz, Long timeOut){
     String payload = "";
        try
        {
            RpcNotifyProto.RpcMessage result = super.postMsg(message.getMsgId(), new InetSocketAddress(host, port), JSONObject.toJSONString(message),timeOut);
            payload = result.getPayload();
            Object o = JSON.parse(payload);
            if(clazz.isInstance(o)){ //判断返回类型是否匹配
            T data = JSON.parseObject(payload, clazz);
                return data;
            } else {
                logger.error("error class type...");
                return null;
            }
        } catch (Exception e)
        {
            logger.error("请求错误 {} [{}]",payload,e.toString());
            e.printStackTrace();
            return null;
        }
    }

}