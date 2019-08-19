/**
 * FSUServiceServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.5  Built on : May 06, 2017 (03:45:26 BST)
 */
package com.kongtrolink.framework.fsuservice;


import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.rpc.RpcModuleBase;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 *  FSUServiceServiceSkeleton java skeleton for the axisService
 */
@Service("fsuService")
public class FSUServiceServiceSkeleton implements FSUServiceServiceSkeletonInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(FSUServiceServiceSkeleton.class);

    @Value("${tower.service.hostname}")
    private String hostname;
    @Value("${tower.service.port}")
    private int port;

    /**
     * Auto generated method signature
     *
     * @param invoke0
     * @return invokeResponse1
     */
    public InvokeResponse invoke(Invoke invoke0) {
        LOGGER.info("*********************FSU回复 start****************************************");
        LOGGER.info("[LSCService Web Server]  receive request...");
        try {
            String request = invoke0.getXmlData().getString();

            ModuleMsg msg = new ModuleMsg("gw_service", "MINI210121000001");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", request);

            msg.setPayload(jsonObject);

            try {
                Configuration conf = new Configuration();
                RpcClient rpcClient = new RpcClient(conf);
                RpcModuleBase rpcModuleBase = new RpcModuleBase(rpcClient);
                RpcNotifyProto.RpcMessage rpcMessage = rpcModuleBase.postMsg("", new InetSocketAddress(hostname, port), JSONObject.toJSONString(msg));
                JSONObject response = JSONObject.parseObject(rpcMessage.getPayload());

                if (response.containsKey("result") && (response.getInteger("result") == 1)) {
                    org.apache.axis2.databinding.types.soapencoding.String param =
                            new org.apache.axis2.databinding.types.soapencoding.String();
                    param.setString(response.getString("msg"));
                    InvokeResponse invokeResponse = new InvokeResponse();
                    invokeResponse.setInvokeReturn(param);
                    LOGGER.info("[LSCService Web Server]  response info :\n " + param.toString());
                    LOGGER.info("********************* FSU回复 end *******************************");
                    return  invokeResponse;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        LOGGER.info("*********************FSU回复 end******************************");
        return new InvokeResponse();
    }



//    /**
//     * 生成 返回报文
//     * @throws Exception
//     */
//    private InvokeResponse baseInvokeResponse(MessageResp respMessage) throws Exception{
//        org.apache.axis2.databinding.types.soapencoding.String param =
//                new org.apache.axis2.databinding.types.soapencoding.String();
//        param.setString(MessageUtil.messageToString(respMessage));
//        InvokeResponse invokeResponse = new InvokeResponse();
//        invokeResponse.setInvokeReturn(param);
//        LOGGER.info("[LSCService Web Server]  response info :\n " + param.toString());
//        LOGGER.info("********************* FSU上报 end *******************************");
//        return  invokeResponse;
//    }

}
