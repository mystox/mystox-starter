package com.kongtrolink.gateway.nb.cmcc.execute.module;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.core.entity.RpcResult;
import com.kongtrolink.gateway.nb.cmcc.execute.SendTool;
import com.kongtrolink.gateway.nb.cmcc.iot.service.NbIotService;
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
    @Override
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payload) {
        JSONObject json = JSON.parseObject(payload);
        String imei = json.getString("uuid");
        String message = json.getString("payload");
        LOGGER.info("RPC 接收到消息. message: {}", payload);
        String back = setMessageArrived(msgId, imei, message, null);
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

    public String setMessageArrived(String msgId, String imei, String message, String type) {

        RpcResult result = new RpcResult();
        result.setResult(1);
        try {
            sendTool.jsonResult(imei, message);
        } catch (Exception e) {
            result.setResult(0);
            e.printStackTrace();
        }
        return JSONObject.toJSONString(result);

       /* try{
            if("resource".equals(type)){
                ResourceInfo jsonObject = JSONObject.parseObject(message,ResourceInfo.class);
                if(jsonObject !=null || jsonObject.getPdu() ==null || jsonObject.getPdu().getJson()==null){
                    ResourceJson json = jsonObject.getPdu().getJson();
                    BaseAck value = nbIotService.getResources(imei);
                    ResourceAck ack  = EntityUtil.getEntity(value,ResourceAck.class);
                    ResourceBackInfo backInfo = new ResourceBackInfo();
                    backInfo.initBack(jsonObject,ack);
                    String requestInfo = JSONObject.toJSONString(backInfo);
                    LOGGER.info("发送消息:"+requestInfo);
//                    sendTool.sendMsgTest(requestInfo);
                }else{
                    LOGGER.info("下发的消息不符合 规范  ");
                }

            }else{
                MiniMqttMessageResponse jsonObject = JSONObject.parseObject(message,MiniMqttMessageResponse.class);
                String imei = jsonObject.getImei();
                Integer objId = jsonObject.getObjId();
                Integer objInstId = jsonObject.getObjInstId();
                Integer executeResId = jsonObject.getExecuteResId();
                String pdu = jsonObject.getPdu();
                if(pdu == null || pdu.length() > 255){
                    result.setResult(0);
                    result.setInfo("pdu 数据为 null 或 长度超过 255 !!!");
                }else{
                    PduEntity  pduObject = JSONObject.parseObject(pdu,PduEntity.class);
                    String msgid = pduObject.getMsgid();
                    if(msgid !=null){
                        if( msgid.equals("GET_DATA")){
                            sendTool.clearMap(imei,"GET_DATA_ACK");
                            LOGGER.info("------ 清空 GET_DATA_ACK 缓存中的数据  !");
                        }else if( msgid.equals("GET_THRESHOLD")){
                            sendTool.clearMap(imei,"GET_THRESHOLD_ACK");
                            LOGGER.info("------ 清空 GET_THRESHOLD_ACK 缓存中的数据   !");
                        }else if( msgid.equals("GET_HISDATA_ACK")){
                            sendTool.clearMap(imei,"GET_HISDATA_ACK");
                            LOGGER.info("------ 清空 GET_HISDATA_ACK 缓存中的数据  !");
                        }
                    }
                    nbIotService.command(pdu,imei,objId,objInstId,executeResId);
                }

            }
        }catch (Exception e){
            LOGGER.warn("[MQTT] 出现异常,原因：{} ",e.getMessage());
        }
        String back = JSONObject.toJSONString(result);
        return back;*/
    }


    public String setMessageArrived(String msgId, String imei, ByteString message, String type) {

        RpcResult result = new RpcResult();
        result.setResult(1);
        try {
            sendTool.returnByte(imei, message);
        } catch (Exception e) {
            result.setResult(0);
            e.printStackTrace();
        }
        return JSONObject.toJSONString(result);
    }
}
