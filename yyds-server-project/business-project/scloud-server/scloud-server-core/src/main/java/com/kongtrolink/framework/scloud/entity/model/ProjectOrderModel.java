package com.kongtrolink.framework.scloud.entity.model;

import com.kongtrolink.framework.scloud.entity.ProjectOrderEntity;

/**
 * 工程管理测试单 前端显示模型
 * Created by Eric on 2020/4/13.
 */
public class ProjectOrderModel extends ProjectOrderEntity{
    /**
     *
     */
    private static final long serialVersionUID = -8569586561261883037L;

    private String fsuName; //FSU名称
    private String manufacturer;    //厂家
    private String fsuState;    //FSU注册状态
    private String operationState;  //FSU运行状态

    public String getFsuName() {
        return fsuName;
    }

    public void setFsuName(String fsuName) {
        this.fsuName = fsuName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getFsuState() {
        return fsuState;
    }

    public void setFsuState(String fsuState) {
        this.fsuState = fsuState;
    }

    public String getOperationState() {
        return operationState;
    }

    public void setOperationState(String operationState) {
        this.operationState = operationState;
    }
}
