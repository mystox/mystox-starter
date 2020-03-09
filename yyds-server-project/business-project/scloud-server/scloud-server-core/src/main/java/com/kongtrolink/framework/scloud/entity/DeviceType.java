package com.kongtrolink.framework.scloud.entity;

import com.kongtrolink.framework.scloud.base.GeneratedValue;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.data.mongodb.core.mapping.Field;

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
    @Field(value = "id")
    @GeneratedValue
    private int id; //主键
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SignalType getSignalTypeByCntbId(String cntbId){
        if(StringUtil.isNUll(cntbId)){
            return null;
        }
        if(null == signalTypeList || signalTypeList.size()==0){
            return null;
        }
        for(SignalType signalType : signalTypeList){
            if(cntbId.equals(signalType.getCntbId())){
                return signalType;
            }
        }
        return null;
    }
}
