package com.kongtrolink.framework.scloud.mqtt.query;

/**
 * 基础通用 查询条件属性
 *  SCloud->中台资管
 * Created by Eric on 2020/3/4.
 */
public class BasicCommonQuery {

    private int searchType; //搜索类型(0-模糊搜索,1-精确搜索,2-in搜索)
    private Object value;   //具体属性值

    public BasicCommonQuery(int searchType, Object value) {
        this.searchType = searchType;
        this.value = value;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
