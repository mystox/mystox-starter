package com.kongtrolink.framework.reports.entity.query;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/10 10:45
 * \* Description:
 * \
 */
public class FsuEntity {
    private String fsuId;
    private String operationState;
    private String manufacturer;

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getOperationState() {
        return operationState;
    }

    public void setOperationState(String operationState) {
        this.operationState = operationState;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}