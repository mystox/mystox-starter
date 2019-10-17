package com.kongtrolink.framework.gateway.common.entity.send.base;

import java.io.Serializable;

/**
 * 基本的回复消息体
 *      发送心跳 回复 HeartbeatAck
 *      推送设备资产信息 回复 PushDeviceAssetAck
 *      上报变化数据  回复 PushRealtimeDataAck
 *      上报告警 回复 PushAlarmAck
 *
 * Created by Mag on 2019/10/14.
 */
public class AckBase implements Serializable{

    private static final long serialVersionUID = 2504993544829075352L;

    private String msgId;

    public AckBase() {
    }

    public AckBase(String msgId) {
        this.msgId = msgId;
    }
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

}
