package tech.mystox.framework.entity;

/**
 * Created by mystox on 2022/4/26, 9:57.
 * company:
 * description:
 * update record:
 */
public class MsgPackage {
    private String msgId;
    private String topic;
    private PayloadType payloadType;
    private String payload;
    private byte[] bytePayload;
    private boolean subpackage;
    private Integer packageCount;//分包总数
    private Integer packageNum;//分包编号
    private Integer crc;//总包校验

    public MsgPackage(String msgId, byte[] bytePayload, boolean subpackage, Integer packageNum, Integer packageCount, Integer crc) {
        setMsgId(msgId);
        setPayloadType(PayloadType.BYTE);
        setBytePayload(bytePayload);
        setPackageNum(packageNum);
        setSubpackage(subpackage);
        setPackageCount(packageCount);
        setCrc(crc);
    }

    public MsgPackage(String msgId, String payload) {
        this.msgId = msgId;
        this.payload = payload;
        this.payloadType = PayloadType.STRING;
    }

    public MsgPackage(String msgId, String topic, String payload) {
        this(msgId, payload);
        this.topic = topic;
    }

    public MsgPackage() {
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        setPayloadType(PayloadType.STRING);
        this.payload = payload;
    }

    public byte[] getBytePayload() {
        return bytePayload;
    }

    public void setBytePayload(byte[] bytePayload) {
        setPayloadType(PayloadType.BYTE);
        this.bytePayload = bytePayload;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public boolean isSubpackage() {
        return subpackage;
    }

    public void setSubpackage(boolean subpackage) {
        this.subpackage = subpackage;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public Integer getCrc() {
        return crc;
    }

    public void setCrc(Integer crc) {
        this.crc = crc;
    }
}
