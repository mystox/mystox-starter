package com.kongtrolink.framework.reports.entity;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/5 9:46
 * \* Description:
 * \
 */
public class JsonData {
    private String name;
    private String[][] data;

    private String unit;
    private String[] xAxis;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String[] getxAxis() {
        return xAxis;
    }

    public void setxAxis(String[] xAxis) {
        this.xAxis = xAxis;
    }
}