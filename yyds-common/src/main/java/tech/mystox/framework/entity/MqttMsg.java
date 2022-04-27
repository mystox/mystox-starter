package tech.mystox.framework.entity;

/**
 * Created by mystoxlol on 2019/8/13, 10:35.
 * company: mystox
 * description:
 * update record:
 */
public class MqttMsg extends MsgPackage {
    private String operaCode;
    private String sourceAddress; //消息源地址，一般为生产消息的服务code serverName+"_"+serverVersion
    private Boolean hasAck = false;
    private PayloadType payloadType;


    public MqttMsg(String msgId, byte[] bytePayload, boolean subpackage, Integer packageNum, Integer packageCount, Integer crc) {
        super(msgId, bytePayload, subpackage, packageNum, packageCount, crc);
    }

    public MqttMsg(String msgId, String topic,String payload,String operaCode,String sourceAddress,Boolean hasAck) {
        super(msgId, topic,payload);
        this.operaCode = operaCode;
        this.sourceAddress = sourceAddress;
        this.hasAck = hasAck;
    }

    public MqttMsg() {

    }

    public Boolean getHasAck() {
        return hasAck;
    }

    public void setHasAck(Boolean hasAck) {
        this.hasAck = hasAck;
    }

    public String getOperaCode() {
        return operaCode;
    }

    public void setOperaCode(String operaCode) {
        this.operaCode = operaCode;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }


    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }


    @Override
    public String toString() {
        return "MqttMsg{" +
                "msgId='" + getMsgId() + '\'' +
                ", topic='" + getTopic() + '\'' +
                ", payloadType=" + payloadType +
                ", payload='" + getPayload() + '\'' +
//                ", bytePayload=" + Arrays.toString(getBytePayload()) +
                '}';
    }
}
