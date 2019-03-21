/** *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ****************************************************** */
package com.kongtrolink.framework.mqtt.message;

import java.util.List;

/**
 * 蓄电池模块设置多点遥信信号报文
 * 
 * @author Mosaico
 */
public class MultiBatteryDIMessage implements MqttStandardMessage {
    
    protected String uniqueCode;
    protected List<String> bbdsIds;     // BBDS 编码
    protected Integer voltageLevel;     //蓄电池组的电压等级
    protected Integer specVoltage;      //蓄电池组的电压规格
    protected Integer specCapacity;     //蓄电池组的容量规格
    protected String signalType;        //要修改的告警对应的遥信信号类型（来源于信号量字典表中的信号类型）
    protected Float threshold;          //将门限值修改为这个值

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public List<String> getBbdsIds() {
        return bbdsIds;
    }

    public void setBbdsIds(List<String> bbdsIds) {
        this.bbdsIds = bbdsIds;
    }

    public Integer getVoltageLevel() {
        return voltageLevel;
    }

    public void setVoltageLevel(Integer voltageLevel) {
        this.voltageLevel = voltageLevel;
    }

    public Integer getSpecVoltage() {
        return specVoltage;
    }

    public void setSpecVoltage(Integer specVoltage) {
        this.specVoltage = specVoltage;
    }

    public Integer getSpecCapacity() {
        return specCapacity;
    }

    public void setSpecCapacity(Integer specCapacity) {
        this.specCapacity = specCapacity;
    }

    public String getSignalType() {
        return signalType;
    }

    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    public Float getThreshold() {
        return threshold;
    }

    public void setThreshold(Float threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "MultiBatteryDIMessage{" + "uniqueCode=" + uniqueCode + ", bbdsIds=" + bbdsIds + ", voltageLevel=" + voltageLevel + ", specVoltage=" + specVoltage + ", specCapacity=" + specCapacity + ", signalType=" + signalType + ", threshold=" + threshold + '}';
    }
    
}
