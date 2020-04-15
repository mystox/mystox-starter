package com.kongtrolink.framework.scloud.entity;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/14 16:22
 * @Description:统计返回前端专用类
 */
public class Statistics {

    private String code;
    private String name;
    private Integer intPro;
    private int count;
    private List<String> properties;
    private List<String> values;

    public Integer getIntPro() {
        return intPro;
    }

    public void setIntPro(Integer intPro) {
        this.intPro = intPro;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
