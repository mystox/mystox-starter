package com.kongtrolink.framework.entity.xml.msg;

import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.entity.xml.base.Info;
import com.kongtrolink.framework.entity.xml.base.PKType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author fengw
 * 发送告警
 * 新建文件　2019-4-22 13:56:11
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Info")
public class SendAlarm extends Info {
    @XmlElement(name = "Values")
    private Value value;

    public SendAlarm() {
        value = new Value();
    }

    @Override
    public PKType pkType() {
        return new PKType(CntbPktTypeTable.SEND_ALARM, CntbPktTypeTable.SEND_ALARM_CODE);
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
