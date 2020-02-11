package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 信号字典表（设备类型）
 * Created by Eric on 2020/2/10.
 */
public class DeviceType implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -59867882791273266L;
    private String code;
    private String typeName;
    private List<SignalType> signalTypeList;

    @Override
    public String toString() {
        return "DeviceType{" + "code=" + code + ", typeName=" + typeName + ", signalTypeList=" + signalTypeList + '}' + '\n';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<SignalType> getSignalTypeList() {
        return signalTypeList;
    }

    public void setSignalTypeList(List<SignalType> signalTypeList) {
        this.signalTypeList = signalTypeList;
    }

    /**
     * 信号字典表（信号类型）
     */
    public class SignalType {

        private String code;
        private String typeName;
        private String measurement;
        private String cntbId;
        private boolean communicationError;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getMeasurement() {
            return measurement;
        }

        public void setMeasurement(String measurement) {
            this.measurement = measurement;
        }

        public String getCntbId() {
            return cntbId;
        }

        public void setCntbId(String cntbId) {
            this.cntbId = cntbId;
        }

        public boolean isCommunicationError() {
            return communicationError;
        }

        public void setCommunicationError(boolean communicationError) {
            this.communicationError = communicationError;
        }

        @Override
        public String toString() {
            return "SignalType{" + "code=" + code + ", typeName=" + typeName + ", measurement=" + measurement + ", cntbId=" + cntbId + ", communicationError=" + communicationError + '}';
        }

    }
}
