package com.kongtrolink.framework.reports.entity;

/**
 * Created by mystoxlol on 2019/10/31, 13:31.
 * company: kongtrolink
 * description:
 * update record:
 */
public class ReportExtendProperties {
    private String field;
    private String name;
    private String type;
    private String belongs;


    public String getBelongs() {
        return belongs;
    }

    public void setBelongs(String belongs) {
        this.belongs = belongs;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
