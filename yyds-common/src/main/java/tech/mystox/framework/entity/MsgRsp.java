package tech.mystox.framework.entity;

import java.util.Arrays;

/**
 * Created by mystoxlol on 2019/9/5, 14:48.
 * company: mystox
 * description:
 * update record:
 */
public class MsgRsp extends MsgPackage{
    private Integer stateCode = StateCode.SUCCESS;

    public MsgRsp(String msgId, byte[] bytePayload, boolean subpackage, Integer packageNum, Integer packageCount, Integer crc) {
        super(msgId,bytePayload,subpackage,packageNum,packageCount,crc);
    }


    public MsgRsp(String msgId, String payload) {
       super(msgId,payload);
    }

    public MsgRsp() {
    }

    public Integer getStateCode() {
        return stateCode;
    }

    public void setStateCode(Integer stateCode) {
        this.stateCode = stateCode;
    }


    @Override
    public String toString() {
        return "MqttResp{" +
                "msgId='" + getMsgId() + '\'' +
                ", topic='" + getTopic() + '\'' +
                ", payloadType=" + getPayloadType() +
                ", payload='" + getPayload() + '\'' +
                ", bytePayload=" + Arrays.toString(getBytePayload()) +
                ", stateCode=" + stateCode +
                '}';
    }
}
