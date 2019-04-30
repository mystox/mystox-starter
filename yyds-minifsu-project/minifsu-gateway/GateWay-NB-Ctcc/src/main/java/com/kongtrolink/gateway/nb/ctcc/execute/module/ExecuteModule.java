package com.kongtrolink.gateway.nb.ctcc.execute.module;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.core.entity.RpcResult;
import com.kongtrolink.gateway.nb.ctcc.execute.SendTool;
import com.kongtrolink.gateway.nb.ctcc.iot.service.NbIotService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mag on 2019/2/25, 19:25.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ExecuteModule extends RpcNotifyImpl implements ModuleInterface {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NbIotService nbIotService;
    @Autowired
    SendTool sendTool;


    public boolean init() {
        LOGGER.info("workerExecute-execute module init");
        //初始化线程池之类的基础任务服务
        return true;
    }

    /**
     * 默认业务程序执行体
     */
    /*@Override
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payload) {
        JSONObject json = JSON.parseObject(payload);
        String uuid = json.getString("uuid");
        RpcResult result = new RpcResult();
        LOGGER.info("RPC 接收到消息. message: {}", payload);
        try{
            MiniMqttMessageResponse jsonObject = JSONObject.parseObject(payload,MiniMqttMessageResponse.class);
            String deviceId = jsonObject.getDeviceId();
            String pdu = jsonObject.getPdu();
            if(deviceId==null||"".equals(deviceId)){
                result.setResult(0);
                result.setInfo("deviceId 为NB命令 必填参数 !! 缺失");
            }else if(pdu == null || pdu.length() > 255){
                result.setResult(0);
                result.setInfo("pdu 数据为 null 或 长度超过 255 !!!");
            }else{
                String serverId = "sendCmd";
                String method = "sendCmd";
                PduEntity pduObject = JSONObject.parseObject(pdu,PduEntity.class);
                String msgid = pduObject.getMsgid();
                if(msgid !=null){
                    if( msgid.equals("GET_DATA")){
                        sendTool.clearMap(deviceId,"GET_DATA_ACK");
                        LOGGER.info("------ 清空 GET_DATA_ACK 缓存中的数据 !");
                    }else if( msgid.equals("GET_THRESHOLD")){
                        sendTool.clearMap(deviceId,"GET_THRESHOLD_ACK");
                        LOGGER.info("------ 清空 GET_THRESHOLD_ACK 缓存中的数据 !");
                    }else if( msgid.equals("GET_HISDATA_ACK")){
                        sendTool.clearMap(deviceId,"GET_HISDATA_ACK");
                        LOGGER.info("------ 清空 GET_HISDATA_ACK 缓存中的数据 !");
                    }
                    nbIotService.postNbCommand(deviceId,serverId,method,pdu);
                }
            }
        }catch (Exception e){
            LOGGER.warn("[MQTT] 出现异常,原因：{} ",e.getMessage());
        }

        String back = JSONObject.toJSONString(result);
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayload(back)
                .setMsgId(StringUtils.isBlank(msgId)?"":msgId)
                .build();
    }*/
    /**
     * 默认业务程序执行体
     */
    @Override
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payload) {
        JSONObject json = JSON.parseObject(payload);
        String deviceId = json.getString("uuid");
        String message = json.getString("payload");
        LOGGER.info("RPC 接收到消息. message: {}", payload);
        String back = setMessageArrived(msgId, deviceId, message, null);
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload(back)
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                .build();
    }
    /**
     * 字节流请求的入口
     *
     * @param msgId
     * @param payload
     * @return
     */
    @Override
    protected RpcNotifyProto.RpcMessage bytesExecute(String msgId, String uuid, ByteString payload) {
        String back = setMessageArrived(msgId, uuid, payload, null);

        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload(back)
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                .build();
    }

    public String setMessageArrived(String msgId, String deviceId, String message, String type) {

        RpcResult result = new RpcResult();
        result.setResult(1);
        try {
            sendTool.jsonResult(deviceId, message);
        } catch (Exception e) {
            result.setResult(0);
            e.printStackTrace();
        }
        return JSONObject.toJSONString(result);
    }
    public String setMessageArrived(String msgId, String deviceId, ByteString message, String type) {

        RpcResult result = new RpcResult();
        result.setResult(1);
        try {
            sendTool.returnByte(deviceId, message);
        } catch (Exception e) {
            result.setResult(0);
            e.printStackTrace();
        }
        return JSONObject.toJSONString(result);
    }

}
